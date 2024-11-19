package lifecycle;

import lifecycle.exceptions.BadConstructorException;

public class RemoteObject {
    Class<?> clazz;
    LivecycleStrategy livecycleStrategy;
    ResourceManagementStrategy resourceManagementStrategy;

    public RemoteObject(Class<?> clazz) {
        this.clazz = clazz;

        //todo: instantiate the strategies
    }

    public Object getServant() throws BadConstructorException {
        return livecycleStrategy.getServant();
    }

    public void returnServant(Object servant) {
        livecycleStrategy.returnServant(servant);
    }
}
