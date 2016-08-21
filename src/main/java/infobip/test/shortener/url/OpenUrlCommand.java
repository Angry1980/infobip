package infobip.test.shortener.url;

import infobip.test.shortener.model.Url;
import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier;
import org.immutables.value.Value;

@Value.Immutable
public interface OpenUrlCommand {

    @TargetAggregateIdentifier
    String getPath();

    Url getUrl();

}
