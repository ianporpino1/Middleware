package lifecycle;

import exceptions.BadConstructorException;

public interface ResourceStrategy {
    Object getServant() throws BadConstructorException;
    void releaseServant(Object servant);
}