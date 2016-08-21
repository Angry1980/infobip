package infobip.test.shortener.account;

import infobip.test.shortener.core.ApplicationException;
import infobip.test.shortener.model.UrlData;

public class UrlAlreadyRegisteredException extends ApplicationException {

    public UrlAlreadyRegisteredException(String accountId, UrlData data) {
        super("Url has been already registered " + data);
    }
}
