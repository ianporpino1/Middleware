package extension.interceptors;

import message.HttpResponse;

import message.HttpRequest;

public interface Interceptor {
    void verifyBefore(HttpRequest request, HttpResponse response, Object handler);
    void verifyAfter(HttpRequest request, HttpResponse response);
}
