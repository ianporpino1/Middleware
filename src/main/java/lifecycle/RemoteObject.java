package lifecycle;

public class RemoteObject {
    Class<?> clazz;
    LivecycleStrategy livecycleStrategy;
    ResourceManegemantStrategy resourceManegemantStrategy;

    public RemoteObject(Class<?> clazz) {
        this.clazz = clazz;
    }

    public RemoteObject getServant() {
        //todo
    }

    public void returnServant(Object servant) {
        //todo
    }
}
