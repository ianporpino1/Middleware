package lifecycle.exceptions;

import java.lang.reflect.Constructor;

public class NoConstructorException extends BadConstructorException{
    public NoConstructorException(Class<?> clazz, Class<?>[] parameterTypes) {
        super(clazz, parameterTypes, "The " + constructorInfo(clazz, parameterTypes) + " does not exist");
    }
}
