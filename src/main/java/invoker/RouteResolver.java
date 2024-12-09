package invoker;

import annotation.web.*;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class RouteResolver {
    public Method findAnnotatedMethod(Class<?> clazz, String httpMethod, String fullRoute) {
        String baseRoute = clazz.getAnnotation(RequestMapping.class).value();

        if (baseRoute.endsWith("/") && fullRoute.startsWith("/")) {
            fullRoute = fullRoute.substring(1);
        }

        String methodRoute = fullRoute.substring(baseRoute.length());
        methodRoute = methodRoute.split("\\?")[0];

        for (Method method : clazz.getDeclaredMethods()) {
            if (matchesAnnotation(method, httpMethod, methodRoute)) {
                return method;
            }
        }
        return null;
    }

    private boolean matchesAnnotation(Method method, String httpMethod, String route) {
        return switch (httpMethod) {
            case "GET" -> method.isAnnotationPresent(Get.class) &&
                    matchesRoute(route, method.getAnnotation(Get.class).value());
            case "POST" -> method.isAnnotationPresent(Post.class) &&
                    matchesRoute(route, method.getAnnotation(Post.class).value());
            case "PUT" -> method.isAnnotationPresent(Put.class) &&
                    matchesRoute(route, method.getAnnotation(Put.class).value());
            case "DELETE" -> method.isAnnotationPresent(Delete.class) &&
                    matchesRoute(route, method.getAnnotation(Delete.class).value());
            default -> false;
        };
    }

    private boolean matchesRoute(String route, String routeTemplate) {
        String regex = routeTemplate
                .replace("{", "(?<")
                .replace("}", ">[a-zA-Z0-9]+)")
                .replace("/", "\\/")
                + "$";

        Pattern pattern = Pattern.compile(regex);

        return pattern.matcher(route).matches();
    }

}
