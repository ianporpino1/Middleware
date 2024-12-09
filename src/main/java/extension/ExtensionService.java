package extension;

import message.HttpResponse;

import message.HttpRequest;
import java.util.ArrayList;
import java.util.List;

public class ExtensionService {
    private final List<Extension> extensions; //nao eh thread safe
    
    public ExtensionService() {
        extensions = new ArrayList<>();
    }

    public void registerExtension(Extension extension) {
        extensions.add(extension);
    }

    public List<Extension> getExtensions() {
        return extensions;
    }

    public void invokeBefore(HttpRequest request, HttpResponse response) {
        for (Extension extension : extensions) {
            extension.beforeInvoke(request, response);
        }
    }

    public void invokeAfter(HttpRequest request, HttpResponse response) {
        for (Extension extension : extensions) {
            extension.afterInvoke(request, response);
        }
    }

    public void invokeOnError(HttpRequest request, HttpResponse response, Exception e) {
        for (Extension extension : extensions) {
            extension.onError(request, response, e);
        }
    }
}
