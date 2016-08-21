package infobip.test.shortener.url;

import infobip.test.shortener.model.RedirectType;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;

public class UrlAggregate extends AbstractAnnotatedAggregateRoot {

    @AggregateIdentifier
    private String path;
    private String url;
    private RedirectType redirectType;
    private int count;

    private UrlAggregate() {
    }

    @CommandHandler
    public UrlAggregate(ImmutableAddPathCommand command) {
        apply(ImmutablePathAddedEvent.builder().path(command.getPath()).data(command.getData()).build());
    }

    @CommandHandler
    public void handle(ImmutableOpenUrlCommand command) {
        apply(ImmutableUrlOpenedEvent.builder()
                .accountId(command.getUrl().getAccountId())
                .count(count + 1)
                .link(command.getUrl().getData().getLink())
                .build()
        );
    }

    @EventSourcingHandler
    public void on(ImmutablePathAddedEvent event) {
        this.path = event.getPath();
        this.url = event.getData().getLink();
        this.redirectType = event.getData().getRedirectType();
    }

    @EventSourcingHandler
    public void on(ImmutableUrlOpenedEvent event) {
        this.count = event.getCount();
    }
}