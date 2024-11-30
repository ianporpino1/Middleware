package lifecycle;

import lifecycle.exceptions.BadConstructorException;

public class StaticInstance implements LifecycleStrategy {
    private Object instance;

    @Override
    public synchronized Object getServant(ResourceStrategy resource) throws BadConstructorException {
        if (instance == null) {
            instance = resource.createServant();
        }
        return instance;
    }

    @Override
    public void releaseServant(ResourceStrategy resource, Object servant) {
        // Nada a fazer porque o objeto vive durante toda aplicação
    }
}
