package lifecycle;

import lifecycle.exceptions.BadConstructorException;

import java.lang.reflect.Constructor;

public class LazyAcquisitionResource implements ResourceStrategy {
    private final Class<?> clazz;
    private final LifecycleManager lifecycleManager;

    public LazyAcquisitionResource(Class<?> clazz, LifecycleManager lifecycleManager) {
        this.clazz = clazz;
        this.lifecycleManager = lifecycleManager;
    }


    @Override
    public Object getServant() throws BadConstructorException {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructors()[0];

            Class<?>[] parameterTypes = constructor.getParameterTypes();
            Object[] parameters = new Object[parameterTypes.length];

            for (int i = 0; i < parameterTypes.length; i++) {
                parameters[i] = lifecycleManager.getRemoteObject(parameterTypes[i]);
            }

            return constructor.newInstance(parameters);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void releaseServant(Object servant) {
    }
}
