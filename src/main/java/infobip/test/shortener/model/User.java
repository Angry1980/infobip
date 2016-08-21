package infobip.test.shortener.model;

import org.immutables.value.Value;

@Value.Immutable
public interface User {
    String getAccountId();
    String getPassword();
}
