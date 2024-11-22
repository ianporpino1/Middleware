package extension;

import extension.interceptors.Interceptor;
import message.HttpResponse;

import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;

public class ExtensionService {
    List<Interceptor> interceptors; //nao eh thread safe
    
    public ExtensionService() {
        interceptors = new ArrayList<>();
    }
    public void addInterceptor(Interceptor interceptor) {
        interceptors.add(interceptor);
    }
    public void removeInterceptor(Interceptor interceptor) {
        interceptors.remove(interceptor);
    }
    public void interceptBefore(HttpRequest message) {
        for (Interceptor interceptor : interceptors) {
            interceptor.verifyBefore(message);
        }
    }
    public void interceptAfter(HttpResponse message) {
        for (Interceptor interceptor : interceptors) {
            interceptor.verifyAfter(message);
        }
    }
}
