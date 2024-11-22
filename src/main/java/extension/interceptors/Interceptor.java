package extension.interceptors;

import message.HttpResponse;

import message.HttpRequest;

public interface Interceptor {
    boolean verifyBefore(HttpRequest request, HttpResponse response);
    void verifyAfter(HttpRequest request, HttpResponse response);
}
