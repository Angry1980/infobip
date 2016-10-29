package infobip.test.shortener.service;

import com.google.common.hash.Hashing;
import infobip.test.shortener.account.RegisterUrlCommand;
import infobip.test.shortener.axon.Utils;
import infobip.test.shortener.core.StatisticQueryView;
import infobip.test.shortener.core.UnauthorizedException;
import infobip.test.shortener.core.UrlQueryView;
import infobip.test.shortener.core.UserQueryView;
import infobip.test.shortener.model.Url;
import infobip.test.shortener.model.UrlData;
import infobip.test.shortener.model.User;
import infobip.test.shortener.url.AddPathCommand;
import infobip.test.shortener.url.OpenUrlCommand;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

public class UrlServiceAxonImpl implements UrlService{

    private static Logger LOG = LoggerFactory.getLogger(UrlServiceAxonImpl.class);

    private CommandGateway commandGateway;
    private UrlQueryView urlQueryView;
    private StatisticQueryView statisticQueryView;
    private UserQueryView userQueryView;

    public UrlServiceAxonImpl(CommandGateway commandGateway,
                              UrlQueryView urlQueryView,
                              StatisticQueryView statisticQueryView,
                              UserQueryView userQueryView) {
        this.commandGateway = Objects.requireNonNull(commandGateway);
        this.urlQueryView = Objects.requireNonNull(urlQueryView);
        this.statisticQueryView = Objects.requireNonNull(statisticQueryView);
        this.userQueryView = Objects.requireNonNull(userQueryView);
    }

    @Override
    public Observable<Url> findFullUrl(String path) {
        return Observable.create(sub -> {
            Url url = urlQueryView.get(path);
            if(url != null){
                //catching stats, async call
                commandGateway.send(new OpenUrlCommand(path, url));
            }
            sub.onNext(url);
            sub.onCompleted();
        });
    }

    @Override
    public Observable<String> add(User user, UrlData data) {
        return Observable.create(sub -> {
            try{
                String path = generatePath(user, data);
                commandGateway.sendAndWait(new RegisterUrlCommand(user, data, path));
                sub.onNext(path);
                sub.onCompleted();
            }catch(CommandExecutionException e) {
                sub.onError(e.getCause());
                return;
            }
        });
    }

    @Override
    public Observable<Map<String, Integer>> getStats(User user) {
        return Observable.create(sub -> {
            if(!user.equals(userQueryView.get(user.getAccountId()))){
                sub.onError(new UnauthorizedException(user));
                return;
            }
            sub.onNext(statisticQueryView.get(user.getAccountId()));
            sub.onCompleted();
        });
    }

    private String generatePath(User user, UrlData data){
        //are there any reasons to limit the number of attempts?
        int count = 0;
        String value = user.getAccountId() + data.getLink();
        while(true){
            String path = Hashing.murmur3_32().hashString(value, StandardCharsets.UTF_8).toString();
            try {
                commandGateway.sendAndWait(new AddPathCommand(path, data));
                return path;
            } catch(CommandExecutionException e){
                if(!Utils.isKeyDuplication(e)){
                    throw e;
                }
                count++;
                if(count > 5){
                    LOG.warn("There are {} attempts to generate path for {}", count, data);
                    //throw new ApplicationException("There are more than limit of attempts to generate path");
                }
                value += count;
            }

        }
    }
}
