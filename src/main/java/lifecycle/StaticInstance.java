package lifecycle;

import exceptions.BadConstructorException;

public class StaticInstance implements LifecycleStrategy {
    private Object instance;

    @Override
    public synchronized Object getServant(ResourceStrategy resource) throws BadConstructorException {
        if (instance == null || resource.getClass().getName().equals(PoolingResource.class.getName())) {
            instance = resource.getServant();
        }
        return instance;
    }

    @Override
    public void releaseServant(ResourceStrategy resource, Object servant) {
        resource.releaseServant(servant);
    }
}
