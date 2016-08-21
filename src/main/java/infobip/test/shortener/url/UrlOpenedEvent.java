package infobip.test.shortener.url;

import org.immutables.value.Value;

@Value.Immutable
public interface UrlOpenedEvent {

    String getAccountId();
    String getLink();
    int getCount();
}
