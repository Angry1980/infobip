package infobip.test.shortener.rest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
@JsonDeserialize(as = ImmutableAccountResult.class)
public interface AccountResult {

    boolean getSuccess();
    String getDescription();
    Optional<String> getPassword();
}
