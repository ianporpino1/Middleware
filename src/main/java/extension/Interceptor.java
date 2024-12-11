package extension;

import message.HttpResponse;

import message.HttpRequest;

public interface Interceptor {
    void beforeInvoke(HttpRequest request, HttpResponse response);
    void afterInvoke(HttpRequest request, HttpResponse response);
    void onError(HttpRequest request, HttpResponse response, Exception e);
}
