package infobip.test.shortener.config;

import infobip.test.shortener.account.AccountAggregate;
import infobip.test.shortener.core.StatisticQueryView;
import infobip.test.shortener.core.UrlQueryView;
import infobip.test.shortener.core.UserQueryView;
import infobip.test.shortener.service.*;
import infobip.test.shortener.url.UrlAggregate;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.annotation.AggregateAnnotationCommandHandler;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.annotation.AnnotationEventListenerAdapter;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventstore.EventStore;
import org.axonframework.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeoutException;

@Configuration
public class CoreConfig {

    @Autowired
    private EventBus eventBus;
    @Autowired
    private EventStore eventStore;
    @Autowired
    private CommandBus commandBus;

    @Autowired
    private CommandGateway commandGateway;

    @Bean
    public AccountService accountService(){
        return new AccountServiceHystrixImpl(
                new AccountServiceAxonImpl(8, commandGateway)
        );
    }

    @Bean
    public UrlService urlService(){
        return new UrlServiceHystrixImpl(
                new UrlServiceAxonImpl(commandGateway, urlQueryView(), statisticQueryView(), userQueryView())
        );
    }

    @Bean
    public Repository<AccountAggregate> accountRepository(){
        EventSourcingRepository<AccountAggregate> repository = new EventSourcingRepository<>(AccountAggregate.class, eventStore);
        repository.setEventBus(eventBus);
        AggregateAnnotationCommandHandler.subscribe(AccountAggregate.class, repository, commandBus);
        AnnotationEventListenerAdapter.subscribe(urlQueryView(), eventBus);
        AnnotationEventListenerAdapter.subscribe(statisticQueryView(), eventBus);
        AnnotationEventListenerAdapter.subscribe(userQueryView(), eventBus);
        return repository;
    }

    @Bean
    public Repository<UrlAggregate> urlRepository(){
        EventSourcingRepository<UrlAggregate> repository = new EventSourcingRepository<>(UrlAggregate.class, eventStore);
        repository.setEventBus(eventBus);
        AggregateAnnotationCommandHandler.subscribe(UrlAggregate.class, repository, commandBus);
        AnnotationEventListenerAdapter.subscribe(statisticQueryView(), eventBus);
        return repository;
    }

    @Bean
    public UrlQueryView urlQueryView(){
        return new UrlQueryView();
    }

    @Bean
    public StatisticQueryView statisticQueryView(){
        return new StatisticQueryView();
    }

    @Bean
    public UserQueryView userQueryView(){
        return new UserQueryView();
    }

}
