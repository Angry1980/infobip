package infobip.test.shortener.account;

import infobip.test.shortener.core.*;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class AccountAggregate extends AbstractAnnotatedAggregateRoot {

    private static Logger LOG = LoggerFactory.getLogger(AccountAggregate.class);

    @AggregateIdentifier
    private String id;
    private String password;
    private Set<String> urls = new HashSet<>();


    private AccountAggregate() {
        //used by axon
    }

    @CommandHandler
    public AccountAggregate(OpenAccountCommand command){
        apply(new AccountOpenedEvent(command.getData(), command.getPassword()));
    }

    @CommandHandler
    public void handle(RegisterUrlCommand command){
        if(!password.equals(command.getUser().getPassword())){
            throw new UnauthorizedException(command.getUser());
        }
        if(urls.contains(command.getData().getLink())){
            throw new UrlAlreadyRegisteredException(id, command.getData());
        }
        apply(new UrlRegisteredEvent(command.getAccountId(), command.getData(), command.getPath()));
    }

    @EventSourcingHandler
    public void on(AccountOpenedEvent event){
        this.id = event.getData().getAccountId();
        this.password = event.getPassword();
    }

    @EventSourcingHandler
    public void on(UrlRegisteredEvent event) {
        this.urls.add(event.getData().getLink());
    }

}
