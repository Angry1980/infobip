package infobip.test.shortener.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import infobip.test.shortener.model.AccountData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.util.Objects;

public class AccountServiceHystrixImpl extends AbstractHystrix implements AccountService{

    private static Logger LOG = LoggerFactory.getLogger(AccountServiceHystrixImpl.class);

    private AccountService prototype;


    public AccountServiceHystrixImpl(AccountService prototype) {
        this.prototype = Objects.requireNonNull(prototype);
    }

    @Override
    @HystrixCommand(groupKey = "account-service", fallbackMethod = "fallbackOpen")
    public Observable<String> open(AccountData data) {
        return wrap(prototype.open(data));
    }

    public Observable<String> fallbackOpen(AccountData data, Throwable t) {
        LOG.error("Error {} while trying to open account {}", t, data);
        return Observable.error(new HystrixBadRequestException("Error while trying to open account", t));
    }


}
