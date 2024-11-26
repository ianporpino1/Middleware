package lifecycle;

import annotation.RequestMapping;
import lifecycle.exceptions.BadConstructorException;

import java.util.HashMap;

public class LifecycleManager {
    public HashMap<Class<?>, RemoteObject> remoteObjects;

    public LifecycleManager() {
        remoteObjects = new HashMap<>();
    }

    public void registerObject(Class<?> clazz) throws BadConstructorException {
        RemoteObject remoteObject = new RemoteObject(clazz);
        remoteObjects.put(clazz, remoteObject);
    }

    public Object getRemoteObject(Class<?> clazz) throws BadConstructorException {
        RemoteObject remoteObject = remoteObjects.get(clazz);
        return remoteObject.getServant();
    }

    public void returnRemoteObject(Class<?> clazz, Object servant){
        RemoteObject remoteObject = remoteObjects.get(clazz);
        remoteObject.returnServant(servant);
    }
}
