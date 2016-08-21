package infobip.test.shortener.rest;

import infobip.test.shortener.model.ImmutableUser;
import infobip.test.shortener.model.User;
import org.springframework.core.MethodParameter;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(User.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest request, WebDataBinderFactory binderFactory) throws Exception {
        String[] tokens = extractAndDecodeHeader(request.getHeader("Authorization"));
        if(tokens == null || tokens.length != 2){
            return null;
        }
        return ImmutableUser.builder().accountId(tokens[0]).password(tokens[1]).build();
    }

    /*
        simple version of org/springframework/security/web/authentication/www/BasicAuthenticationFilter.java
     */
    private String[] extractAndDecodeHeader(String header){
        if (header == null || !header.startsWith("Basic ")) {
            return null;
        }
        try {
            return new String(Base64.decode(header.substring(6).getBytes("UTF-8"))).split(":");
        }
        catch (Exception e) {
            return null;
        }
    }


}
