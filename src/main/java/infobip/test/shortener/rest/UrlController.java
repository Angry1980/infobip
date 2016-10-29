package infobip.test.shortener.rest;

import infobip.test.shortener.model.UrlData;
import infobip.test.shortener.model.User;
import infobip.test.shortener.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.net.URI;
import java.util.Objects;

@RestController
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class UrlController {

    @Autowired
    private UrlService urlService;

    @RequestMapping(value="register", method = RequestMethod.POST)
    public DeferredResult<ResponseEntity> register(User user, @RequestBody UrlData data){
        DeferredResult<ResponseEntity> result = new DeferredResult<>();
        if(user == null){
            //using of spring-security is better solution for real applications
            result.setResult(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
            return result;
        }
        UrlResponseBuilder responseBuilder = UrlResponseBuilder.create();
        urlService.add(user, data)
                .map(responseBuilder)
                .subscribeOn(Schedulers.computation())
                .subscribe(result::setResult, t -> result.setErrorResult(t.getCause()));
        return result;

    }

    private static class UrlResponseBuilder implements Func1<String, ResponseEntity> {

        private ServletUriComponentsBuilder urlBuilder;

        public static UrlResponseBuilder create(){
            return new UrlResponseBuilder(ServletUriComponentsBuilder.fromCurrentContextPath());
        }

        private UrlResponseBuilder(ServletUriComponentsBuilder urlBuilder) {
            this.urlBuilder = Objects.requireNonNull(urlBuilder);
        }

        @Override
        public ResponseEntity call(String shortUrl) {
            URI uri = urlBuilder.path("r/" + shortUrl).build().toUri();
            return ResponseEntity
                    .created(uri)
                    .body(new UrlResult(uri.toString()));
        }
    }

}