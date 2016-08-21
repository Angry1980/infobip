package infobip.test.shortener.service;

import infobip.test.shortener.model.AccountData;
import rx.Observable;

public interface AccountService {

    Observable<String> open(AccountData data);
}
