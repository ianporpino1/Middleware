package extension.interceptors;

import message.HttpResponse;
import message.HttpRequest;


public class SecurityInterceptor implements Interceptor {

    @Override
    public boolean verifyBefore(HttpRequest request, HttpResponse response) {
        //Como usar isso de maneira que faca sentido no projeto final?
        
        //if(handler.getClass().isAnnotationPresent(Secured.class))
        if (!request.getHeaders().containsKey("Authorization")) return false;
        
        String token = request.getHeaders().get("Authorization");
        System.out.println(token);
        if (!token.contains("Test")) {
            response.setStatusCode(401);
            response.setStatusMessage("Unauthorized");
            response.setBody("Unauthorized");
            return false;
        }
        
        return true;
    }

    @Override
    public void verifyAfter(HttpRequest request, HttpResponse response) {

    }
}
