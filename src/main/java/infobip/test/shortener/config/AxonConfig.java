package infobip.test.shortener.config;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.CommandGatewayFactoryBean;
import org.axonframework.common.jpa.ContainerManagedEntityManagerProvider;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.SimpleEventBus;
import org.axonframework.eventstore.EventStore;
import org.axonframework.eventstore.jpa.JpaEventStore;
import org.axonframework.unitofwork.SpringTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManager;

@Configuration
public class AxonConfig {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean
    public EventStore eventStore(){
        ContainerManagedEntityManagerProvider entityManagerProvider = new ContainerManagedEntityManagerProvider();
        entityManagerProvider.setEntityManager(entityManager);
        return new JpaEventStore(entityManagerProvider);
    }

    @Bean
    public EventBus eventBus(){
        return new SimpleEventBus();
    }

    @Bean//(destroyMethod = "shutdown")
    public CommandBus commandBus() {
        SimpleCommandBus bus = new SimpleCommandBus();
        bus.setTransactionManager(new SpringTransactionManager(transactionManager));
        return bus;
        //return new AsynchronousCommandBus();
    }

    @Bean
    public CommandGatewayFactoryBean<CommandGateway> commandGatewayFactoryBean() {
        CommandGatewayFactoryBean<CommandGateway> factory = new CommandGatewayFactoryBean<CommandGateway>();
        factory.setCommandBus(commandBus());
        return factory;
    }

}
