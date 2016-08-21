package infobip.test.shortener.rest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUrlResult.class)
public interface UrlResult {

    String getShortUrl();
}
