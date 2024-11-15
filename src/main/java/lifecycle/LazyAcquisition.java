package lifecycle;

import java.util.*;

public class LazyAcquisition extends ResourceManagementStrategy{
    private final Queue<Object> availableServants;

    public LazyAcquisition(Class<?> clazz) {
        super(clazz);
        availableServants = new LinkedList<>();
    }

    @Override
    public Object getServant() {
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
