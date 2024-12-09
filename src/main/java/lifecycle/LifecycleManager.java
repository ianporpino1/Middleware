package lifecycle;

import annotation.Component;
import annotation.scope.Scope;
import annotation.scope.ScopeType;
import annotation.strategy.CreationStrategy;
import annotation.strategy.CreationStrategyType;
import lifecycle.exceptions.BadConstructorException;

import java.rmi.Remote;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/*
* Classe responsável por gerenciar o ciclo de vida dos objetos remotos.
*
* A ideia é que para cada classe de domínio eu tenho um conjunto de objetos remotos, quando o
* Invoker precisa de um obj remoto, o Lifecycle devolve um servant.
*
* */
public class LifecycleManager {
    private final ConcurrentHashMap<Class<?>,RemoteObject> remoteObjects;

    public LifecycleManager() {
        remoteObjects = new ConcurrentHashMap<>();
    }

    public synchronized Object getRemoteObject(Class<?> clazz) throws BadConstructorException {
        remoteObjects.computeIfAbsent(clazz, key -> {
            try {
                return createRemoteObject(clazz);
            } catch (BadConstructorException e) {
                throw new RuntimeException("Failed to create remote object: " + clazz.getName(), e);
            }
        });
        return remoteObjects.get(clazz).getServant();
    }

    private RemoteObject createRemoteObject(Class<?> clazz) throws BadConstructorException {
        if (!clazz.isAnnotationPresent(Component.class)) {
            throw new RuntimeException(clazz.getName() + " is not annotated with @Component");
        }

        Scope scope = clazz.getAnnotation(Scope.class);
        CreationStrategy creationStrategy = clazz.getAnnotation(CreationStrategy.class);

        LifecycleStrategy strategy = (scope != null && scope.value() == ScopeType.PER_REQUEST) ?
                        new PerRequestInstance() :
                        new StaticInstance();

        ResourceStrategy resource =
                (creationStrategy != null && creationStrategy.value() == CreationStrategyType.POOLING) ?
                        new PoolingResource(clazz, this) :
                        new LazyAcquisitionResource(clazz, this);

        return new RemoteObject(clazz, strategy, resource);
    }

    public void releaseRemoteObject(Object obj) {
        RemoteObject remoteObject = remoteObjects.get(obj.getClass());
        if (remoteObject != null) {
            if (remoteObject.getStrategy().getClass() == PerRequestInstance.class &&
                    remoteObject.getResource().getClass() == LazyAcquisitionResource.class) {
                remoteObjects.remove(obj.getClass());
            } else if (remoteObject.getResource().getClass() == PoolingResource.class) {
                remoteObject.releaseServant(obj);
            }
        }
    }
}
