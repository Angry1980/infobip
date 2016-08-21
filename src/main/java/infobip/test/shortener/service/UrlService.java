package infobip.test.shortener.service;

import infobip.test.shortener.model.Url;
import infobip.test.shortener.model.UrlData;
import infobip.test.shortener.model.User;
import rx.Observable;

import java.util.Map;

public interface UrlService {

    Observable<String> add(User user, UrlData data);

    Observable<Url> findFullUrl(String path);

    Observable<Map<String, Integer>> getStats(User user);
}
