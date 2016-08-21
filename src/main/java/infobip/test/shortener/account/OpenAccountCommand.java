package infobip.test.shortener.account;

import infobip.test.shortener.model.AccountData;
import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier;
import org.immutables.value.Value;

@Value.Immutable
public interface OpenAccountCommand {

    @Value.Derived
    @TargetAggregateIdentifier
    default String getAccountId(){
        return getData().getAccountId();
    }

    AccountData getData();
    String getPassword();

}
