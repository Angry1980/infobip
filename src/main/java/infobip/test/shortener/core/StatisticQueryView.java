package infobip.test.shortener.core;

import infobip.test.shortener.account.AccountOpenedEvent;
import infobip.test.shortener.account.UrlRegisteredEvent;
import infobip.test.shortener.url.UrlOpenedEvent;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StatisticQueryView {

    private static Logger LOG = LoggerFactory.getLogger(StatisticQueryView.class);

    private Map<String, Map<String, Integer>> data = new ConcurrentHashMap<>();

    public Map<String, Integer> get(String accountId){
        return data.getOrDefault(accountId, Collections.emptyMap());
    }

    @EventHandler
    public void on(AccountOpenedEvent event){
        data.put(event.getData().getAccountId(), new ConcurrentHashMap<>());
    }

    @EventHandler
    public void on(UrlRegisteredEvent event){
        data.get(event.getAccountId()).put(event.getData().getLink(), 0);
    }

    @EventHandler
    public void on(UrlOpenedEvent event){
        LOG.info("{} is opened", event.getLink());
        data.get(event.getAccountId()).put(event.getLink(), event.getCount());
    }

}
