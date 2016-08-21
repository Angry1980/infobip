package infobip.test.shortener.rest;

import infobip.test.shortener.model.User;
import infobip.test.shortener.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import rx.schedulers.Schedulers;

@RestController
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class StatisticController {

    @Autowired
    private UrlService urlService;

    @RequestMapping(value="statistic/{accountId}", method = RequestMethod.GET)
    public DeferredResult<ResponseEntity> stats(User user, @PathVariable String accountId){
        DeferredResult result = new DeferredResult();
        if(user == null || !user.getAccountId().equals(accountId)){
            result.setResult(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
            return result;
        }
        urlService.getStats(user)
                .map(stats -> ResponseEntity.ok(stats))
                .subscribeOn(Schedulers.computation())
                .subscribe(result::setResult, result::setErrorResult);
        return result;
    }
}
