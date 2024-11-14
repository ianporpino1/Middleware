package lifecycle;

import annotation.RequestMapping;

import java.util.HashMap;

public class LifecycleManager {
    public HashMap<Class<?>, RemoteObject> remoteObjects;

    public LifecycleManager() {
        remoteObjects = new HashMap<>();
    }

    public void registerObject(Class<?> clazz){
        RemoteObject remoteObject = new RemoteObject(clazz);
        remoteObjects.put(clazz, remoteObject);
    }

    public RemoteObject getRemoteObject(Class<?> clazz){
        RemoteObject remoteObject = remoteObjects.get(clazz);
        return remoteObject.getServant();
    }

    public void returnRemoteObject(Class<?> clazz, Object servant){
        RemoteObject remoteObject = remoteObjects.get(clazz);
        remoteObject.returnServant(servant);
    }
}
