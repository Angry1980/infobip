package infobip.test.shortener.url;

import infobip.test.shortener.model.UrlData;
import org.immutables.value.Value;

@Value.Immutable
public interface PathAddedEvent {

    String getPath();
    UrlData getData();

}
