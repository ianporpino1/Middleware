package extension;

import message.HttpResponse;

import message.HttpRequest;
import java.util.ArrayList;
import java.util.List;

public class ExtensionService {
    private final List<Interceptor> extensions; //nao eh thread safe
    
    public ExtensionService() {
        extensions = new ArrayList<>();
    }

    public void registerExtension(Interceptor extension) {
        extensions.add(extension);
    }

    public List<Interceptor> getExtensions() {
        return extensions;
    }

    public void invokeBefore(HttpRequest request, HttpResponse response) {
        for (Interceptor extension : extensions) {
            extension.beforeInvoke(request, response);
        }
    }

    public void invokeAfter(HttpRequest request, HttpResponse response) {
        for (Interceptor extension : extensions) {
            extension.afterInvoke(request, response);
        }
    }

    public void invokeOnError(HttpRequest request, HttpResponse response, Exception e) {
        for (Interceptor extension : extensions) {
            extension.onError(request, response, e);
        }
    }
}
