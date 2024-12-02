package lifecycle;

import lifecycle.exceptions.BadConstructorException;

public interface ResourceStrategy {
    Object getServant() throws BadConstructorException;
    void releaseServant(Object servant);
}