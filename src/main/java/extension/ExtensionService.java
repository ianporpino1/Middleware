package extension;

import extension.interceptors.Interceptor;
import message.HttpResponse;

import message.HttpRequest;
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
    public boolean interceptBefore(HttpRequest request, HttpResponse response) {
        boolean continues;
        for (Interceptor interceptor : interceptors) {
            continues = interceptor.verifyBefore(request,response);
            if (!continues) {
                return false;
            }
        }
        return true;
    }
    public void interceptAfter(HttpRequest request, HttpResponse response) {
        for (Interceptor interceptor : interceptors) {
            interceptor.verifyAfter(request,response);
        }
    }
}
