package infobip.test.shortener;

import infobip.test.shortener.model.*;
import infobip.test.shortener.rest.AccountResult;
import infobip.test.shortener.rest.ImmutableAccountResult;
import infobip.test.shortener.rest.ImmutableUrlResult;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static io.restassured.config.RedirectConfig.redirectConfig;
import static io.restassured.config.RestAssuredConfig.config;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShortenerTest {

    private static Logger LOG = LoggerFactory.getLogger(ShortenerTest.class);

    @Value("${local.server.port}")
    private int port;

    @Before
    public void init() {
        RestAssured.port = port;
        RestAssured.defaultParser = Parser.JSON;
        RestAssured.config = config().redirect(redirectConfig().followRedirects(false));
    }

    @Test
    public void testAccountOpening(){
        openAccount("1").then()
                .statusCode(HttpStatus.CREATED.value())
                .header("Location", Matchers.containsString("/account/1"))
                .body("success", equalTo(true))
                .body("description", equalTo("Your account is opened"))
                .body("password", notNullValue())
        ;
    }

    @Test
    public void testTryingOfOpenAccountTwice(){
        openAccount("2");
        openAccount("2").then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("success", equalTo(false))
                .body("description", equalTo("Account has been already opened"))
                .body("password", nullValue())
        ;
    }

    @Test
    public void testUrlRegistration(){
        Response r = registerUrl(
                ImmutableUrlData.builder()
                        .link("http://url1.com")
                        .redirectType(RedirectType.RT_301)
                        .build(),
                "3", createAccount("3").getPassword()
        );
        r.then()
                .statusCode(HttpStatus.CREATED.value())
                .header("Location", startsWith("http"))
                .body("shortUrl", startsWith("http"))
        ;
        //LOG.info(r.body().asString());
    }

    @Test
    public void testUrlRegistrationTwiceForSameAccount(){
         AccountResult account = createAccount("4");
         UrlData data = ImmutableUrlData.builder()
                            .link("http://url2.com")
                            .build();

         getShortUrl(data, "4", account.getPassword());
         registerUrl(data, "4", account.getPassword()).then()
                 .statusCode(HttpStatus.BAD_REQUEST.value())
         ;
    }

    @Test
    public void testUrlRegistrationTwiceForDifferentAccounts(){
        UrlData data = ImmutableUrlData.builder()
                .link("http://url3.com")
                .build();
        AccountResult account = createAccount("5");
        getShortUrl(data, "5", account.getPassword());
        account = createAccount("6");
        registerUrl(data, "6", account.getPassword()).then()
                .statusCode(HttpStatus.CREATED.value())
                .header("Location", startsWith("http"))
                .body("shortUrl", startsWith("http"))
        ;
    }

    @Test
    public void testUrlRegistrationWithoutAuthToken(){
        given().contentType(ContentType.JSON)
                .body(ImmutableUrlData.builder()
                        .link("http://url4.com")
                        .redirectType(RedirectType.RT_301)
                        .build())
                .when().post("register").then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
        ;
    }

    @Test
    public void testUrlRegistrationWithWrongPassword(){
        createAccount("7");
        UrlData data = ImmutableUrlData.builder()
                .link("http://url5.com")
                .redirectType(RedirectType.RT_301)
                .build();
        registerUrl(data, "7", Optional.of("wrong password"))
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
        ;
    }

    @Test
    public void testPermanentRedirect(){
        AccountResult result = createAccount("8");
        UrlData data = ImmutableUrlData.builder()
                .link("http://url6.com")
                .redirectType(RedirectType.RT_301)
                .build();
        String url = getShortUrl(data, "8", result.getPassword());
        given().when().get(url)
                .then()
                .statusCode(RedirectType.RT_301.getStatus().value())
                .header("Location", equalTo("http://url6.com"))
        ;
    }

    @Test
    public void testTempRedirect(){
        AccountResult result = createAccount("9");
        UrlData data = ImmutableUrlData.builder()
                .link("http://url6.com")
                .redirectType(RedirectType.RT_302)
                .build();
        String url = getShortUrl(data, "9", result.getPassword());
        given().when().get(url)
                .then()
                .statusCode(RedirectType.RT_302.getStatus().value())
                .header("Location", equalTo("http://url6.com"))
        ;
    }

    @Test
    public void testRedirectWithParams(){
        AccountResult result = createAccount("10");
        UrlData data = ImmutableUrlData.builder()
                .link("http://url6.com?a=10&b=grt")
                .build();
        String url = getShortUrl(data, "10", result.getPassword());
        given().when().get(url)
                .then()
                .header("Location", equalTo("http://url6.com?a=10&b=grt"))
        ;
    }

    @Test
    public void testStatistic(){
        AccountResult result = createAccount("11");
        ImmutableUrlData data = ImmutableUrlData.builder()
                .link("http://url8.com")
                .build();
        String url1 = getShortUrl(data, "11", result.getPassword());
        String url2 = getShortUrl(data.withLink("http://url9.com"), "11", result.getPassword());
        given().when().get(url1);
        given().when().get(url2);
        given().when().get(url1);
        Map r = given()
                .auth().preemptive().basic("11", result.getPassword().get())
                .contentType(ContentType.JSON)
                .when().get("statistic/11").body().as(Map.class);
        assertEquals(r.get("http://url8.com"), 2);
        assertEquals(r.get("http://url9.com"), 1);
    }

    @Test
    public void testStatisticWithoutAuthToken(){
        createAccount("12");
        given().contentType(ContentType.JSON)
                .when().get("statistic/12").then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
        ;
    }

    @Test
    public void testStatisticWithWrongPassword(){
        createAccount("13");
        given().contentType(ContentType.JSON)
                .auth().preemptive().basic("13", "wrong password")
                .when().get("statistic/13").then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
        ;
    }

    @Test
    public void testStatisticWithAnotherAccount(){
        createAccount("14");
        AccountResult result = createAccount("15");
        given().contentType(ContentType.JSON)
                .auth().preemptive().basic("15", result.getPassword().get())
                .when().get("statistic/14").then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
        ;
    }

    private AccountResult createAccount(String accountId){
        return openAccount(accountId).body().as(ImmutableAccountResult.class);
    }

    private String getShortUrl(UrlData data, String account, Optional<String> password){
        return registerUrl(data, account, password).body().as(ImmutableUrlResult.class).getShortUrl();
    }

    private Response registerUrl(UrlData data, String account, Optional<String> password){
        return given()
                .auth().preemptive().basic(account, password.get())
                .contentType(ContentType.JSON)
                //.log().everything()
                .body(data)
                .when().post("register");
    }

    private Response openAccount(String accountId){
        return given().contentType(ContentType.JSON)
                .body(ImmutableAccountData.builder().accountId(accountId).build())
                .when().post("account");
    }
}
