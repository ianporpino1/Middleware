package lifecycle;

import lifecycle.exceptions.BadConstructorException;

public class LazyAcquisitionResource implements ResourceStrategy {
    private final Class<?> clazz;

    public LazyAcquisitionResource(Class<?> clazz) {
        this.clazz = clazz;
    }


    @Override
    public Object getServant() throws BadConstructorException {
        try {
            return clazz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void releaseServant(Object servant) {
    }
}
