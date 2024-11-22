package extension.interceptors;

import message.HttpResponse;
import message.HttpRequest;


public class SecurityInterceptor implements Interceptor {

    @Override
    public void verifyBefore(HttpRequest request, HttpResponse response, Object handler) {
        //if(handler.getClass().isAnnotationPresent(Secured.class))
        if (request.getHeaders().containsKey("Authorization")) {
            
        }
    }

    @Override
    public void verifyAfter(HttpRequest request, HttpResponse response) {

    }
}
