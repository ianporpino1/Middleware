package lifecycle;

import lifecycle.exceptions.BadConstructorException;
import lifecycle.exceptions.InaccessibleConstructorException;
import lifecycle.exceptions.NoConstructorException;
import lifecycle.exceptions.NotInstanceableConstructorException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public interface ResourceStrategy {
    Object createServant() throws BadConstructorException;
    void releaseServant(Object servant);
}