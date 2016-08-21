package infobip.test.shortener.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUrlData.class)
public interface UrlData {

    String getLink();

    @Value.Default
    default RedirectType getRedirectType(){
        return RedirectType.RT_302;
    }
}
