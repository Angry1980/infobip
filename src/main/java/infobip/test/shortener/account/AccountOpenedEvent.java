package infobip.test.shortener.account;

import infobip.test.shortener.model.AccountData;
import org.immutables.value.Value;

@Value.Immutable
public interface AccountOpenedEvent {

    AccountData getData();
    String getPassword();
}
