package infobip.test.shortener.core;

import infobip.test.shortener.account.ImmutableAccountOpenedEvent;
import infobip.test.shortener.model.ImmutableUser;
import infobip.test.shortener.model.User;
import org.axonframework.eventhandling.annotation.EventHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserQueryView {

    private Map<String, User> users = new ConcurrentHashMap<>();

    public User get(String accountId) {
        return users.get(accountId);
    }

    @EventHandler
    public void on(ImmutableAccountOpenedEvent event) {
        users.put(event.getData().getAccountId(),
                ImmutableUser.builder()
                        .password(event.getPassword())
                        .accountId(event.getData().getAccountId())
                        .build()
        );
    }

}