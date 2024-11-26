package lifecycle;

import lifecycle.exceptions.BadConstructorException;

public abstract class LivecycleStrategy {
    protected final ResourceManagementStrategy resources;

    public abstract Object getServant() throws BadConstructorException;
    public abstract void returnServant(Object servant);

    public LivecycleStrategy(ResourceManagementStrategy resources) {
        this.resources = resources;
    }


}
