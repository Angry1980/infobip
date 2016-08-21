package infobip.test.shortener.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableAccountData.class)
public interface AccountData {

    String getAccountId();
}
