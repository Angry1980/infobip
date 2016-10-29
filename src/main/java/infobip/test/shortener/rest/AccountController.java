package infobip.test.shortener.rest;

import infobip.test.shortener.model.AccountData;
import infobip.test.shortener.service.AccountService;
import infobip.test.shortener.core.ApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.util.Optional;

@RestController
@RequestMapping(value = "account", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountController {

    private static AccountResult successResult = new AccountResult(true, "Your account is opened", Optional.empty());

    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ResponseEntity get(@PathVariable String id){
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }


    @RequestMapping(method = RequestMethod.POST)
    public DeferredResult<ResponseEntity> create(@RequestBody AccountData accountData){
        ServletUriComponentsBuilder urlBuilder = ServletUriComponentsBuilder.fromCurrentRequest();
        DeferredResult<ResponseEntity> result = new DeferredResult<>();
        ErrorHandler errorHandler = new ErrorHandler(result);
        accountService.open(accountData)
                .map(password -> ResponseEntity
                        .created(urlBuilder.path("/{id}").buildAndExpand(accountData.getAccountId()).toUri())
                        .body(successResult.copy(successResult.getSuccess(), successResult.getDescription(), Optional.ofNullable(password)))
                ).subscribeOn(Schedulers.computation())
                .subscribe(result::setResult, errorHandler);
        return result;
    }

    private class ErrorHandler implements Action1<Throwable>{

        private DeferredResult<ResponseEntity> result;

        public ErrorHandler(DeferredResult<ResponseEntity> result) {
            this.result = result;
        }

        @Override
        public void call(Throwable throwable) {
            if(throwable.getCause() instanceof ApplicationException){
                result.setResult(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body(new AccountResult(false, throwable.getCause().getMessage(), Optional.empty()))
                );
                return;
            }
            result.setErrorResult(throwable.getCause());
        }
    };

}
