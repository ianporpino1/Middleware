package extension.interceptors;

import message.HttpResponse;

import java.net.http.HttpRequest;

public interface Interceptor {
    void verifyBefore(HttpRequest message);
    void verifyAfter(HttpResponse message);
}
