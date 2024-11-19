package lifecycle;

import lifecycle.exceptions.BadConstructorException;
import lifecycle.exceptions.InaccessibleConstructorException;
import lifecycle.exceptions.NoConstructorException;
import lifecycle.exceptions.NotInstanceableConstructorException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class ResourceManagementStrategy {
    protected Class<?> clazz;
    protected Constructor<?> constructor;
    public abstract Object getServant() throws BadConstructorException;
    public abstract void create_servant();
    public abstract void returnServant(Object servant);
    public abstract void destroyServant(Object servant);

    public ResourceManagementStrategy(Class<?> clazz) throws BadConstructorException {
        this.clazz = clazz;

        //todo: maybe later make it see constructors with arguments
        Class<?>[] parameterTypes = {};

        try {
            this.constructor = clazz.getConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new NoConstructorException(clazz, parameterTypes);
        }
    }

    protected Object create() throws BadConstructorException {
        try {
            return constructor.newInstance();
        } catch (InstantiationException e) {
            throw new NotInstanceableConstructorException(constructor);
        } catch (IllegalAccessException e) {
            throw new InaccessibleConstructorException(constructor);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}