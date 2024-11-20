package extension;

import extension.interceptors.Interceptor;
import message.HTTPMessage;

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
    public void interceptBefore(HTTPMessage message) throws SecurityException {
        for (Interceptor interceptor : interceptors) {
            interceptor.verifyBefore(message);
        }
    }
    public void interceptAfter(HTTPMessage message) throws SecurityException {
        for (Interceptor interceptor : interceptors) {
            interceptor.verifyAfter(message);
        }
    }
}
