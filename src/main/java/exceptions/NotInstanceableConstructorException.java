package exceptions;

import java.lang.reflect.Constructor;

public class NotInstanceableConstructorException extends BadConstructorException{
    public NotInstanceableConstructorException(Class<?> clazz, Class<?>[] parameterTypes) {
        super(clazz, parameterTypes, "The " + constructorInfo(clazz, parameterTypes) + " cannot be instantiated");
    }
    public NotInstanceableConstructorException(Constructor<?> constructor) {
        super(constructor.getClass(), constructor.getParameterTypes(), "The " + constructorInfo(constructor.getClass(), constructor.getParameterTypes()) + " cannot be instantiated");
    }
}
