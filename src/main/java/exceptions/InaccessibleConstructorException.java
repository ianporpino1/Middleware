package exceptions;

import java.lang.reflect.Constructor;

public class InaccessibleConstructorException extends BadConstructorException{
    public InaccessibleConstructorException(Class<?> clazz, Class<?>[] parameterTypes) {
        super(clazz, parameterTypes, "The " + constructorInfo(clazz, parameterTypes) + " is not accessible");
    }
    public InaccessibleConstructorException(Constructor<?> constructor) {
        super(constructor.getClass(), constructor.getParameterTypes(), "The " + constructorInfo(constructor.getClass(), constructor.getParameterTypes()) + " is not accessible");
    }
}
