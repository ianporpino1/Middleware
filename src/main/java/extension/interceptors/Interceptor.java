package extension.interceptors;

import message.HttpResponse;

import java.net.http.HttpRequest;

public interface Interceptor {
    void verifyBefore(HttpRequest message) throws SecurityException;
    void verifyAfter(HttpResponse message) throws SecurityException;
}
