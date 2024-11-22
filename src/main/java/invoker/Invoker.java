package invoker;

import annotation.web.*;
import lifecycle.LifecycleManager;
import lifecycle.LookupService;
import message.HttpRequest;
import message.HttpResponse;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Invoker {
    //private Marshaller marshaller;
    
    private final LifecycleManager lifecycleManager;
    
    private final LookupService lookupService;
    
    
    public Invoker(LookupService lookupService) {
        lifecycleManager = new LifecycleManager();
        this.lookupService = lookupService;
    }
    
    public HttpResponse invoke(HttpRequest request) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String fullRoute = request.getUrl();
        String httpMethod = request.getMethod();
        
        Class<?> clazz = lookupService.getRoute(fullRoute);

        Method targetMethod = findAnnotatedMethod(clazz, httpMethod, fullRoute);

        Object servant = clazz.getConstructor().newInstance();
                //lifecycleManager.getInstance(clazz);
        try {
            assert targetMethod != null;
            //TODO: adicionar checagem de parametros do metodo
            var result = targetMethod.invoke(servant);

            var response = new HttpResponse();
            response.setBody(result.toString());
            response.setStatusCode(200);
            response.setStatusMessage("OK");
            
            return response;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Method findAnnotatedMethod(Class<?> clazz, String httpMethod, String fullRoute) {
        String baseRoute = clazz.getAnnotation(RequestMapping.class).value();
        String methodRoute = fullRoute.substring(baseRoute.length());
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
