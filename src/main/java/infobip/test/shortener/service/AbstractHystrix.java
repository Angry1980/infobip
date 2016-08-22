package infobip.test.shortener.service;

import com.netflix.hystrix.exception.HystrixBadRequestException;
import org.axonframework.commandhandling.CommandExecutionException;
import rx.Observable;

import java.util.Objects;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

public abstract class AbstractHystrix {

    private Predicate<Throwable> ignoreExceptions;

    public AbstractHystrix() {
        this.ignoreExceptions = t -> !(t instanceof TimeoutException);
    }

    protected <T> Observable<T> wrap(Observable<T> o){
        return o.onErrorResumeNext(t -> {
            if(ignoreExceptions.test(t)){
                t = new HystrixBadRequestException("",
                        t instanceof CommandExecutionException ? t.getCause() : t);
            }
            return Observable.error(t);
        });
    }

}
