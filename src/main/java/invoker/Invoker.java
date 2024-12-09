package invoker;

import annotation.parameters.PathVariable;
import annotation.parameters.RequestBody;
import annotation.parameters.RequestParam;
import annotation.web.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.InvokerException;
import exceptions.MarshallerException;
import extension.ExtensionService;
import lifecycle.LifecycleManager;
import lifecycle.LookupService;
import exceptions.BadConstructorException;
import marshaller.Marshaller;
import message.HttpRequest;
import message.HttpResponse;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Pattern;

public class Invoker {
    private Marshaller marshaller;
    private final LifecycleManager lifecycleManager;
    private final LookupService lookupService;
    private final ExtensionService extensionService;
    private RouteResolver routeResolver;
    private ParamConverter paramConverter;

    public Invoker(LookupService lookupService, ExtensionService extensionService, LifecycleManager lifecycleManager) {
        this.lifecycleManager = lifecycleManager;
        this.extensionService = extensionService;
        this.lookupService = lookupService;
        this.routeResolver = new RouteResolver();
        this.paramConverter = new ParamConverter();
    }

    public HttpResponse invoke(HttpRequest request) throws BadConstructorException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        var response = new HttpResponse();
        String fullRoute = request.getUrl();
        String httpMethod = request.getMethod();

        extensionService.invokeBefore(request, response);

        Class<?> clazz = lookupService.getRoute(fullRoute);
        Method targetMethod = routeResolver.findAnnotatedMethod(clazz, httpMethod, fullRoute);
        Object servant = lifecycleManager.getRemoteObject(clazz);
        
        try {

            Object[] params = targetMethod.getParameterCount() != 0
                    ? resolveParams(targetMethod, clazz, request)
                    : null;

            Object result = (params == null)
                    ? targetMethod.invoke(servant)
                    : targetMethod.invoke(servant, params);

            response.setBody(result != null ? result.toString() : "null");
            response.setStatusCode(result != null ? 200 : 500);
            response.setStatusMessage(result != null ? "OK" : "Internal Server Error");
            extensionService.invokeAfter(request, response);
            return response;

        } catch (Exception e) {
           throw new InvokerException(e.getMessage());
        } finally {
            lifecycleManager.releaseRemoteObject(servant);
        }
    }

    private Object[] resolveParams(Method targetMethod, Class<?> clazz,HttpRequest request) {
        List<Object> params = new ArrayList<>();

        String routeTemplate = getRouteTemplate(clazz,targetMethod);
        Map<String,String> pathVariables = ParamResolver.extractPathVariables(routeTemplate,request.getUrl());
        Map<String,String> queryParams = ParamResolver.extractQueryParams(request.getUrl());

        for (Parameter parameter : targetMethod.getParameters()) {
            if (parameter.isAnnotationPresent(PathVariable.class)) {
                String pathVariableName = parameter.getAnnotation(PathVariable.class).value();
                String pathVariableValue = pathVariables.get(pathVariableName);
                params.add(paramConverter.convertToType(pathVariableValue, parameter.getType()));

            } else if (parameter.isAnnotationPresent(RequestParam.class)) {
                String requestParamName = parameter.getAnnotation(RequestParam.class).value();
                String requestParamValue = queryParams.get(requestParamName);
                params.add(paramConverter.convertToType(requestParamValue, parameter.getType()));

            } else if (parameter.isAnnotationPresent(RequestBody.class)) {
                String requestBody = request.getBody();
                params.add(paramConverter.convertToType(requestBody, parameter.getType()));
            }
        }
        return params.toArray();
    }

    private String getRouteTemplate(Class<?> clazz, Method targetMethod) {
        String classTemplate = clazz.getAnnotation(RequestMapping.class).value();

        String methodTemplate = getMethodTemplate(targetMethod);

        return classTemplate + methodTemplate;
    }

    public String getMethodTemplate(Method targetMethod) {
        var annotations = targetMethod.getAnnotations();
        if (annotations.length != 1) {
            throw new InvokerException("O método deve ter exatamente uma anotação HTTP!");
        }

        var annotation = annotations[0];
        return switch (annotation) {
            case Get get -> get.value();
            case Post post -> post.value();
            case Put put -> put.value();
            case Delete delete -> delete.value();
            default -> throw new InvokerException("Anotação HTTP desconhecida: " + annotation);
        };
    }
}