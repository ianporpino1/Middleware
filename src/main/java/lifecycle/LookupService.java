package lifecycle;

import annotation.RequestMapping;

import java.util.HashMap;

public class LookupService {
    public HashMap<String, Class<?>> routes;
    
    public LookupService() {
        routes = new HashMap<>();
    }
    
    public void registerRoute(Class<?> clazz){

        if(clazz.isAnnotationPresent(RequestMapping.class)){
            RequestMapping annotation = clazz.getAnnotation(RequestMapping.class);
            String route = annotation.value();
            routes.put(route, clazz);
        }
    }
    
    public Class<?> getRoute(String route){
        return routes.get(route);
    }
}
