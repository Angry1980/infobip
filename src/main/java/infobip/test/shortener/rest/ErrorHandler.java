package infobip.test.shortener.rest;

import infobip.test.shortener.core.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorHandler {

    private static Logger LOG = LoggerFactory.getLogger(ErrorHandler.class);

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity handle(UnauthorizedException e) {
        LOG.error("Attempt to access ", e.getUser().getAccountId());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handle(Exception e) {
        LOG.error("Unknown error", e);
        return ResponseEntity.badRequest().build();
    }

}
