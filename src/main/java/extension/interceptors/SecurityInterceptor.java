package extension.interceptors;

import message.HttpResponse;
import message.HttpRequest;


public class SecurityInterceptor implements Interceptor {

    @Override
    public boolean verifyBefore(HttpRequest request, HttpResponse response) {
        //if(handler.getClass().isAnnotationPresent(Secured.class))
        String token = request.getHeaders().get("Authorization");
        if (!isValidToken(token)) {
            setUnauthorizedResponse(response);
            return false;
        }
        return true;
    }

    @Override
    public void verifyAfter(HttpRequest request, HttpResponse response) {

    }

    private boolean isValidToken(String token) {
        return token != null && token.startsWith("Bearer ") && token.contains("Test");
    }
    
    private void setUnauthorizedResponse(HttpResponse response) {
        response.setStatusCode(401);
        response.setStatusMessage("Unauthorized");
        response.setBody("Unauthorized");
    }
}
