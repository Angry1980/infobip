package infobip.test.shortener.model;

import org.immutables.value.Value;

@Value.Immutable
public interface Url {

    String getAccountId();
    UrlData getData();
}
