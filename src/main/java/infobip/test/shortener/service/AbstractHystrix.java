package infobip.test.shortener.service;

import com.netflix.hystrix.exception.HystrixBadRequestException;
import rx.Observable;

import java.util.Objects;
import java.util.function.Predicate;

public abstract class AbstractHystrix {

    private Predicate<Throwable> ignoreExceptions;

    public AbstractHystrix(Predicate<Throwable> ignoreExceptions) {
        this.ignoreExceptions = Objects.requireNonNull(ignoreExceptions);
    }

    protected <T> Observable<T> wrap(Observable<T> o){
        return o.onErrorResumeNext(t -> {
            if(ignoreExceptions.test(t)){
                t = new HystrixBadRequestException("", t.getCause());
            }
            return Observable.error(t);
        });
    }

}
