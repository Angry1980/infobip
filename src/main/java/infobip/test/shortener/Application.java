package infobip.test.shortener;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import infobip.test.shortener.rest.UserArgumentResolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ReactiveHttpServerFactory;
import org.springframework.boot.context.embedded.RxNettyEmbeddedHttpServerFactory;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.reactive.config.WebReactiveConfiguration;

import java.util.List;

@SpringBootApplication
//@EnableHystrix
public class Application  extends WebReactiveConfiguration {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Module jdk8Module() {
        return new Jdk8Module();
    }
/*
    @Bean
    public ReactiveHttpServerFactory reactiveHttpServerFactory(){
        return new RxNettyEmbeddedHttpServerFactory();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new UserArgumentResolver());
    }
*/
}
