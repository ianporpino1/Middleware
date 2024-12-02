package lifecycle;

import lifecycle.exceptions.BadConstructorException;

public class PerRequestInstance implements LifecycleStrategy {

    @Override
    public Object getServant(ResourceStrategy resource) throws BadConstructorException {
        return resource.getServant();
    }

    @Override
    public void releaseServant(ResourceStrategy resource, Object servant) {
        resource.releaseServant(servant);
    }
}
