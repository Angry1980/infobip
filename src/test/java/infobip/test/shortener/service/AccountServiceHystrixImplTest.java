package infobip.test.shortener.service;

import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import infobip.test.shortener.model.AccountData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class AccountServiceHystrixImplTest {

    @Autowired
    private AccountService accountService;

    @Test
    public void testOpenSuccess(){
        TestSubscriber<String> s = new TestSubscriber<>();
        accountService.open(new AccountData("1")).subscribe(s);
        s.assertNoErrors();
        s.assertValueCount(1);
    }

    @Test
    public void testOpenExecutionException(){
        TestSubscriber<String> s = new TestSubscriber<>();
        accountService.open(new AccountData("3")).subscribe(s);
        s.assertError(HystrixBadRequestException.class);
    }

    @Test
    public void testOpenFallback(){
        TestSubscriber<String> s = new TestSubscriber<>();
        accountService.open(new AccountData("2")).subscribe(s);
        s.assertError(HystrixRuntimeException.class);
    }

    @Configuration
    @EnableAspectJAutoProxy
    public static class SpringConfig {

        @Bean
        public HystrixCommandAspect hystrixCommandAspect() {
            ConfigurationManager.getConfigInstance()
                    .addProperty("hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds", 3000);
            return new HystrixCommandAspect();
        }

        @Bean
        public AccountService accountService() {
            AccountService prototype = mock(AccountService.class);
            when(prototype.open(any(AccountData.class))).thenAnswer(c -> {
                String id =  ((AccountData) c.getArguments()[0]).getAccountId();
                return Observable.create(s -> {
                    switch(id){
                        case "1":
                            s.onNext("answer");
                            s.onCompleted();
                            break;
                        case "2":
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "3":
                            s.onError(new IllegalStateException(""));
                            break;
                    }
                });
            });
            return new AccountServiceHystrixImpl(prototype);
        }
    }

}
