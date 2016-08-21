package infobip.test.shortener.account;

import infobip.test.shortener.model.UrlData;
import org.immutables.value.Value;

@Value.Immutable
public interface UrlRegisteredEvent {

    String getAccountId();
    UrlData getData();
    String getPath();
}
