package lifecycle;

import annotation.web.RequestMapping;
import exceptions.LookupException;

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
        return routes.keySet().stream()
                .filter(fullRoute::startsWith)
                .map(routes::get)
                .findFirst()
                .orElseThrow(() -> new LookupException(fullRoute));
    }
}