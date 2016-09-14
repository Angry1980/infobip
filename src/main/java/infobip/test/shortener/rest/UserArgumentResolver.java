package infobip.test.shortener.rest;

import infobip.test.shortener.model.ImmutableUser;
import infobip.test.shortener.model.User;
import org.springframework.core.MethodParameter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.ui.ModelMap;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(User.class);
    }

    @Override
    public Mono<Object> resolveArgument(MethodParameter parameter, ModelMap model, ServerWebExchange exchange) {
        return Mono.create(sink -> {
            sink.complete(resolveArgument(exchange.getRequest()));
        });
    }

    public Object resolveArgument(ServerHttpRequest request) {
        String[] tokens = extractAndDecodeHeader(request.getHeaders().getFirst("Authorization"));
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
