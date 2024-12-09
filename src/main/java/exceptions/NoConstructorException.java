package exceptions;

public class NoConstructorException extends BadConstructorException{
    public NoConstructorException(Class<?> clazz, Class<?>[] parameterTypes) {
        super(clazz, parameterTypes, "The " + constructorInfo(clazz, parameterTypes) + " does not exist");
    }
}
