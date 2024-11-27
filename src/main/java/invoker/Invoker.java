package invoker;

import annotation.parameters.PathVariable;
import annotation.parameters.RequestBody;
import annotation.parameters.RequestParam;
import annotation.web.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import extension.ExtensionService;
import invoker.resolver.ParamResolver;
import lifecycle.LifecycleManager;
import lifecycle.LookupService;
import lifecycle.exceptions.BadConstructorException;
import marshaller.Marshaller;
import message.HttpRequest;
import message.HttpResponse;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Invoker {
    private Marshaller marshaller;

    private final LifecycleManager lifecycleManager;

    private final LookupService lookupService;

    private final ExtensionService extensionService;


    public Invoker(LookupService lookupService, ExtensionService extensionService, LifecycleManager lifecycleManager) {
        this.lifecycleManager = lifecycleManager;
        this.extensionService = extensionService;
        this.lookupService = lookupService;
    }

    public HttpResponse invoke(HttpRequest request) throws BadConstructorException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String fullRoute = request.getUrl();
        String httpMethod = request.getMethod();

        Class<?> clazz = lookupService.getRoute(fullRoute);

        Method targetMethod = findAnnotatedMethod(clazz, httpMethod, fullRoute);

        lifecycleManager.registerObject(clazz);

        Object servant = lifecycleManager.getRemoteObject(clazz);
        
        try {
            var response = new HttpResponse();

            //interceptors
            boolean test = extensionService.interceptBefore(request, response);
            if (!test) {
                return response;
            }

            Object[] params = null;
            if(targetMethod.getParameterCount() != 0) {
                params = resolveParams(targetMethod, clazz,request);
            }

            Object result;
            if (params == null) {
                result = targetMethod.invoke(servant);
            } else {
                result = targetMethod.invoke(servant, params);
            }
            //interceptors
            //extensionService.interceptAfter(request, response)


            response.setBody(result != null ? result.toString() : "null");
            response.setStatusCode(200);
            response.setStatusMessage("OK");

            return response;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object[] resolveParams(Method targetMethod, Class<?> clazz,HttpRequest request) {
        List<Object> params = new ArrayList<>();

        String routeTemplate = getRouteTemplate(clazz,targetMethod);
        Map<String,String> pathVariables = ParamResolver.extractPathVariables(routeTemplate,request.getUrl());
        System.out.println(pathVariables.get("userId"));
        Map<String,String> queryParams = ParamResolver.extractQueryParams(request.getUrl());

        for (Parameter parameter : targetMethod.getParameters()) {
            if (parameter.isAnnotationPresent(PathVariable.class)) {
                String pathVariableName = parameter.getAnnotation(PathVariable.class).value();
                String pathVariableValue = pathVariables.get(pathVariableName);
                params.add(convertToType(pathVariableValue, parameter.getType()));

            } else if (parameter.isAnnotationPresent(RequestParam.class)) {
                String requestParamName = parameter.getAnnotation(RequestParam.class).value();
                String requestParamValue = queryParams.get(requestParamName);
                params.add(convertToType(requestParamValue, parameter.getType()));

            } else if (parameter.isAnnotationPresent(RequestBody.class)) {
                String requestBody = request.getBody();
                params.add(convertToType(requestBody, parameter.getType()));
            }
        }
        return params.toArray();
    }

    private String getRouteTemplate(Class<?> clazz, Method targetMethod) {
        System.out.println(clazz.getName());
        String classTemplate = clazz.getAnnotation(RequestMapping.class).value();

        String methodTemplate = getMethodTemplate(targetMethod);

        return classTemplate + methodTemplate;
    }

    public String getMethodTemplate(Method targetMethod) {
        var annotations = targetMethod.getAnnotations();
        if (annotations.length != 1) {
            throw new IllegalArgumentException("O método deve ter exatamente uma anotação HTTP!");
        }

        var annotation = annotations[0];
        return switch (annotation) {
            case Get get -> get.value();
            case Post post -> post.value();
            case Put put -> put.value();
            case Delete delete -> delete.value();
            default -> throw new IllegalStateException("Anotação HTTP desconhecida: " + annotation);
        };
    }

    private Object convertToType(String value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        return switch (targetType.getName()) {
            case "java.lang.String" -> value;
            case "java.lang.Integer" -> Integer.parseInt(value);
            case "java.lang.Long" -> Long.parseLong(value);
            case "java.lang.Boolean" -> Boolean.parseBoolean(value);
            default -> convertJson(value, targetType);
        };
    }
    
    private Object convertJson(String value, Class<?> targetType){
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(value, targetType);
        } catch (IOException e) {
            throw new IllegalArgumentException("Erro ao deserializar o valor para " + targetType.getName(), e);
        }
    }


    private Method findAnnotatedMethod(Class<?> clazz, String httpMethod, String fullRoute) {
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
        switch (httpMethod) {
            case "GET":
                if (method.isAnnotationPresent(Get.class)) {
                    String routeTemplate = method.getAnnotation(Get.class).value();
                    return matchesRoute(route, routeTemplate);
                }
                break;
            case "POST":
                if (method.isAnnotationPresent(Post.class)) {
                    String routeTemplate = method.getAnnotation(Post.class).value();
                    return matchesRoute(route, routeTemplate);
                }
                break;
            // Adicione mais casos para PUT, DELETE, etc.
        }
        return false;
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