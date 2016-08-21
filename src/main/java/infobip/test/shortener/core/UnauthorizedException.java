package infobip.test.shortener.core;

import infobip.test.shortener.model.User;

public class UnauthorizedException extends ApplicationException{

    private final User user;

    public UnauthorizedException(User user) {
        super("Password is not correct");
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
