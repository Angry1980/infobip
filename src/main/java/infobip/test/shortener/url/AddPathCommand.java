package infobip.test.shortener.url;

import infobip.test.shortener.model.UrlData;
import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier;
import org.immutables.value.Value;

@Value.Immutable
public interface AddPathCommand {

    @TargetAggregateIdentifier
    String getPath();
    UrlData getData();
}
