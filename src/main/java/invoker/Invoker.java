package invoker;

import annotation.Delete;
import annotation.Get;
import annotation.Post;
import annotation.Put;
import lifecycle.LifecycleManager;
import lifecycle.LookupService;
import message.HTTPMessage;
import org.json.JSONObject;

import java.lang.reflect.Method;

public class Invoker {
    private Marshaller marshaller;
    
    private LifecycleManager lifecycleManager;
    
    private LookupService lookupService;
    
    
    public Invoker() {
        lifecycleManager = new LifecycleManager();
        lookupService = new LookupService();
    }
    
    public HTTPMessage invoke(HTTPMessage request){
        String route = request.resource();
        String httpMethod = request.httpMethod();
        
        Class<?> clazz = lookupService.getRoute(route);

        Method targetMethod = findAnnotatedMethod(clazz, httpMethod, route);

        Object servant = lifecycleManager.getInstance(clazz);
        try {
            assert targetMethod != null;
            var result = (JSONObject) targetMethod.invoke(servant, request.body());

            return new HTTPMessage(request.httpMethod(), request.resource(), result);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Method findAnnotatedMethod(Class<?> clazz, String httpMethod, String methodRoute) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (matchesAnnotation(method, httpMethod, methodRoute)) {
                return method;
            }
        }
        return null;
    }

    private boolean matchesAnnotation(Method method, String httpMethod, String methodRoute) {
        switch (httpMethod) {
            case "GET":
                if (method.isAnnotationPresent(Get.class)) {
                    return method.getAnnotation(Get.class).value().equals(methodRoute);
                }
                break;
            case "POST":
                if (method.isAnnotationPresent(Post.class)) {
                    return method.getAnnotation(Post.class).value().equals(methodRoute);
                }
                break;
            case "PUT":
                if (method.isAnnotationPresent(Put.class)) {
                    return method.getAnnotation(Put.class).value().equals(methodRoute);
                }
                break;
            case "DELETE":
                if (method.isAnnotationPresent(Delete.class)) {
                    return method.getAnnotation(Delete.class).value().equals(methodRoute);
                }
                break;
        }
        return false;
    }
}
