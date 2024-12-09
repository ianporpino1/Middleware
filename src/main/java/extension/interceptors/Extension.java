package extension.interceptors;

import message.HttpResponse;

import message.HttpRequest;

public interface Extension {
    void verifyBefore(HttpRequest request, HttpResponse response);
    void verifyAfter(HttpRequest request, HttpResponse response);
    void onError(HttpRequest request, HttpResponse response, Exception e);
}
