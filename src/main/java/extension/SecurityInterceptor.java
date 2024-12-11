package extension;

import message.HttpRequest;
import message.HttpResponse;

import java.security.SecureRandom;
import java.util.Base64;

public class SecurityInterceptor implements Interceptor{
    private final String generatedToken;

    public SecurityInterceptor() {
        this.generatedToken = generateRandomToken();
    }

    @Override
    public void beforeInvoke(HttpRequest request, HttpResponse response) {
        String authHeader = request.getHeaders().get("Authorization");
        if (authHeader == null || authHeader.isEmpty()) {
            response.mountResponse(401, "Unauthorized", "");
            return;
        }

        String token = authHeader.substring(7);
        if (!generatedToken.equals(token)) {
            response.mountResponse(403, "Forbidden", "");
        }
    }

    public String getToken() {
        return generatedToken;
    }

    @Override
    public void afterInvoke(HttpRequest request, HttpResponse response) {

    }

    @Override
    public void onError(HttpRequest request, HttpResponse response, Exception e) {

    }

    private String generateRandomToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
