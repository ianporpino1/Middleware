package lifecycle;

public class RemoteObject {
    Class<?> clazz;
    LivecycleStrategy livecycleStrategy;
    ResourceManegemantStrategy resourceManegemantStrategy;

    public RemoteObject(Class<?> clazz) {
        this.clazz = clazz;

        //todo: instanciate the strategys
    }

    public Object getServant() {
        return livecycleStrategy.getServant();
    }

    public void returnServant(Object servant) {
        livecycleStrategy.returnServant(servant);
    }
}
