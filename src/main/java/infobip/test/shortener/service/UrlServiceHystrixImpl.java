package infobip.test.shortener.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import infobip.test.shortener.model.AccountData;
import infobip.test.shortener.model.Url;
import infobip.test.shortener.model.UrlData;
import infobip.test.shortener.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

public class UrlServiceHystrixImpl extends AbstractHystrix implements UrlService{

    private static Logger LOG = LoggerFactory.getLogger(UrlServiceHystrixImpl.class);

    private UrlService urlService;

    public UrlServiceHystrixImpl(UrlService urlService, Predicate<Throwable> ignoreExceptions) {
        super(ignoreExceptions);
        this.urlService = Objects.requireNonNull(urlService);
    }

    @Override
    @HystrixCommand(groupKey = "url-service", fallbackMethod = "fallbackAdd")
    public Observable<String> add(User user, UrlData data) {
        return wrap(urlService.add(user, data));
    }

    public Observable<String> fallbackAdd(User user, UrlData data, Throwable t) {
        LOG.error("Error {} while trying to add url {}", t, data);
        return Observable.error(new HystrixBadRequestException("Error while trying to add url", t));
    }

    @Override
    @HystrixCommand(groupKey = "url-service", fallbackMethod = "fallbackFindFullUrl")
    public Observable<Url> findFullUrl(String path) {
        return wrap(urlService.findFullUrl(path));
    }

    public Observable<Url> fallbackFindFullUrl(String path, Throwable t) {
        //may be some page about error
        LOG.error("Error {} while trying to find full url {}", t, path);
        return Observable.error(new HystrixBadRequestException("Error while trying to find full url", t));
    }

    @Override
    @HystrixCommand(groupKey = "url-service", fallbackMethod = "fallbackGetStats")
    public Observable<Map<String, Integer>> getStats(User user) {
        return wrap(urlService.getStats(user));
    }

    public Observable<Map<String, Integer>> fallbackGetStats(User user, Throwable t) {
        LOG.error("Error {} while trying to get stats for {}", t, user.getAccountId());
        return Observable.error(new HystrixBadRequestException("Error while trying to get stats", t));
    }
}
