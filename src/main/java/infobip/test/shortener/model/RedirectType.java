package infobip.test.shortener.model;

import org.springframework.http.HttpStatus;

public enum RedirectType {
    RT_301(HttpStatus.MOVED_PERMANENTLY),
    RT_302(HttpStatus.FOUND),
    ;

    HttpStatus status;

    RedirectType(HttpStatus status) {
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
