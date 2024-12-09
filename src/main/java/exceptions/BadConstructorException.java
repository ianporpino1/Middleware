package exceptions;

import java.util.Arrays;

public class BadConstructorException extends Exception{
    protected Class<?> clazz;
    protected Class<?>[] parameterTypes;

    protected BadConstructorException(Class<?> clazz, Class<?>[] parameterTypes, String message) {
        super(message);
        this.clazz = clazz;
        this.parameterTypes = parameterTypes;
    }



    protected static String constructorInfo(Class<?> clazz, Class<?>[] parameterTypes) {
        return "constructor with parameters: " + Arrays.toString(parameterTypes) + " from the class " + clazz.getName();
    }

}
