package infobip.test.shortener.rest;

import infobip.test.shortener.model.UrlData;
import infobip.test.shortener.service.UrlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;
import rx.schedulers.Schedulers;

@Controller
@RequestMapping("r")
public class RedirectController {

    private static Logger LOG = LoggerFactory.getLogger(RedirectController.class);

    @Autowired
    private UrlService urlService;

    @RequestMapping(value = "{path}", method = RequestMethod.GET)
    public DeferredResult<ResponseEntity> handle(@PathVariable String path){
        DeferredResult<ResponseEntity> result = new DeferredResult<>();
        urlService.findFullUrl(path)
                .map(url -> result(path, url.getData()))
                .subscribeOn(Schedulers.computation())
                .subscribe(result::setResult, t -> result.setErrorResult(t.getCause()));
        return result;
    }


    private ResponseEntity result(String path, UrlData data){
        if(data == null){
            LOG.info("There is nothing for {}", path);
            return ResponseEntity.notFound().build();
        }
        MultiValueMap headers = new LinkedMultiValueMap<>();
        headers.add("Location", data.getLink());
        return new ResponseEntity(headers, data.getRedirectType().getStatus());
    }
}
