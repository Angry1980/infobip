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
    public UrlAggregate(AddPathCommand command) {
        apply(new PathAddedEvent(command.getPath(), command.getData()));
    }

    @CommandHandler
    public void handle(OpenUrlCommand command) {
        apply(new UrlOpenedEvent(command.getUrl().getAccountId(), command.getUrl().getData().getLink(), count + 1));
    }

    @EventSourcingHandler
    public void on(PathAddedEvent event) {
        this.path = event.getPath();
        this.url = event.getData().getLink();
        this.redirectType = event.getData().getRedirectType();
    }

    @EventSourcingHandler
    public void on(UrlOpenedEvent event) {
        this.count = event.getCount();
    }
}