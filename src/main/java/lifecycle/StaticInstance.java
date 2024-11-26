package lifecycle;

import lifecycle.exceptions.BadConstructorException;

public class StaticInstance extends LivecycleStrategy {

    public StaticInstance(ResourceManagementStrategy resources) {
        super(resources);
        resources.create_servant();
    }

    @Override
    public Object getServant() throws BadConstructorException {
        return resources.getServant();
    }

    @Override
    public void returnServant(Object servant) {
        resources.returnServant(servant);
    }
}
