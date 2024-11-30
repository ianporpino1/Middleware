package lifecycle;

import lifecycle.exceptions.BadConstructorException;

public interface LifecycleStrategy {
    Object getServant(ResourceStrategy resource) throws BadConstructorException;
    void releaseServant(ResourceStrategy resource, Object servant);
}
