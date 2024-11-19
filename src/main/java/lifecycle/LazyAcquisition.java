package lifecycle;

import lifecycle.exceptions.BadConstructorException;

import java.util.*;

public class LazyAcquisition extends ResourceManagementStrategy{
    private final Queue<Object> availableServants;

    public LazyAcquisition(Class<?> clazz) throws BadConstructorException {
        super(clazz);
        availableServants = new LinkedList<>();
    }

    @Override
    public Object getServant() throws BadConstructorException {
        Object lazyServant = availableServants.poll();
        if(lazyServant == null){
            lazyServant = create();
        }
        return lazyServant;
    }

    @Override
    public void create_servant() {
        availableServants.add(null);
    }

    @Override
    public void returnServant(Object servant) {
        availableServants.offer(servant);
    }

    @Override
    public void destroyServant(Object servant) {
        // do nothing
    }
}
