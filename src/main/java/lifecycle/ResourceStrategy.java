package lifecycle;

import lifecycle.exceptions.BadConstructorException;

public interface ResourceStrategy {
    Object createServant() throws BadConstructorException;
    void releaseServant(Object servant);
}