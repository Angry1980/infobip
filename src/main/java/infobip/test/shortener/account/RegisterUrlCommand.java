package infobip.test.shortener.account;

import infobip.test.shortener.model.UrlData;
import infobip.test.shortener.model.User;
import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier;
import org.immutables.value.Value;

@Value.Immutable
public interface RegisterUrlCommand {

    @TargetAggregateIdentifier
    @Value.Derived
    default String getAccountId(){
        return getUser().getAccountId();
    }

    User getUser();
    UrlData getData();
    String getPath();

}
