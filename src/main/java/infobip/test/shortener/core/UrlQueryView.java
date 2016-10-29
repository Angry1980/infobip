package infobip.test.shortener.core;

import infobip.test.shortener.account.UrlRegisteredEvent;
import infobip.test.shortener.model.Url;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UrlQueryView {

    private static Logger LOG = LoggerFactory.getLogger(UrlQueryView.class);

    private Map<String,Url> data = new ConcurrentHashMap<>();

    public Url get(String key) {
        return data.get(key);
    }

    @EventHandler
    public void on(UrlRegisteredEvent event) {
        data.put(event.getPath(), new Url(event.getAccountId(), event.getData()));
        LOG.info("Add {} to {}", event.getPath(), event.getData().getLink());
    }

}
