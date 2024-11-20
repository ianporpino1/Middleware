package lifecycle;

import annotation.web.RequestMapping;

import java.util.HashMap;
import java.util.Set;

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
    
    public Class<?> getRoute(String fullRoute){
        Set<String> baseRoutes = routes.keySet();
        for (String baseRoute : baseRoutes) {
            if (fullRoute.startsWith(baseRoute)) {
                return routes.get(baseRoute);
            }
        }
        return null;
    }
}
