package lifecycle;

import lifecycle.exceptions.BadConstructorException;

import java.util.LinkedList;
import java.util.Queue;

public class LazyAcquisitionResource implements ResourceStrategy {
    private final Class<?> clazz;

    public LazyAcquisitionResource(Class<?> clazz) {
        this.clazz = clazz;
    }


    @Override
    public Object createServant() throws BadConstructorException {
        try {
            return clazz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void releaseServant(Object servant) {
        // Nada a fazer
    }
}
