package invoker;

import annotation.parameters.PathVariable;
import annotation.parameters.RequestBody;
import annotation.parameters.RequestParam;
import annotation.web.*;
import extension.ExtensionService;
import invoker.resolver.ParamResolver;
import lifecycle.LifecycleManager;
import lifecycle.LookupService;
import marshaller.HttpMarshaller;
import marshaller.Marshaller;
import message.HTTPMessage;
import org.json.JSONObject;
import utils.JsonUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Invoker {
    private Marshaller marshaller;

    private final LifecycleManager lifecycleManager;

    private final LookupService lookupService;

    private final ExtensionService extensionService;


    public Invoker(LookupService lookupService, ExtensionService extensionService, LifecycleManager lifecycleManager) {
        this.marshaller = new HttpMarshaller();
        this.lifecycleManager = lifecycleManager;
        this.extensionService = extensionService;
        this.lookupService = lookupService;
    }

    public void invoke(Socket clientSocket) throws Exception {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        HTTPMessage httpMessage = marshaller.deserialize(bufferedReader);

        Class<?> clazz = lookupService.getRoute(httpMessage.resource());

        lifecycleManager.registerObject(clazz);

        Object remoteObject = lifecycleManager.getRemoteObject(clazz);

        Method targetMethod = findAnnotatedMethod(clazz, httpMessage.httpMethod(), httpMessage.resource());

        Object[] params = null;
        if (targetMethod.getParameterCount() != 0) {
            params = resolveParams(targetMethod, clazz, httpMessage);
        }

        Object result;
        if (params == null) {
            result = targetMethod.invoke(remoteObject);
        } else {
            result = targetMethod.invoke(remoteObject, params);
        }

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

        marshaller.serialize(writer, httpMessage);
    }

    private Object[] resolveParams(Method targetMethod, Class<?> clazz, HTTPMessage message) {
        List<Object> params = new ArrayList<>();

        String routeTemplate = getRouteTemplate(clazz, targetMethod);
        Map<String, String> pathVariables = ParamResolver.extractPathVariables(routeTemplate, message.resource());
        Map<String, String> queryParams = ParamResolver.extractQueryParams(message.resource());

        for (Parameter parameter : targetMethod.getParameters()) { // TODO: dando erro de null pointer
            if (parameter.isAnnotationPresent(PathVariable.class)) {
                String pathVariableName = parameter.getAnnotation(PathVariable.class).value();
                JSONObject pathVariableValue = new JSONObject(pathVariables.get(pathVariableName));

                params.add(JsonUtil.fromJson(pathVariableValue, parameter.getType()));
            } else if (parameter.isAnnotationPresent(RequestParam.class)) {
                String requestParamName = parameter.getAnnotation(RequestParam.class).value();
                JSONObject requestParamValue = queryParams.get(requestParamName) == null?
                        new JSONObject(queryParams.get(requestParamName)) : null;

                params.add(JsonUtil.fromJson(requestParamValue, parameter.getType()));
            } else if (parameter.isAnnotationPresent(RequestBody.class)) {
                params.add(JsonUtil.fromJson(message.body(), parameter.getType()));
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
