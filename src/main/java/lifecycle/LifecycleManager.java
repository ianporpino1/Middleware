package lifecycle;

import annotation.Component;
import annotation.scope.Scope;
import annotation.scope.ScopeType;
import annotation.strategy.CreationStrategy;
import annotation.strategy.CreationStrategyType;
import lifecycle.exceptions.BadConstructorException;

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
    private final ConcurrentHashMap<Class<?>, Set<RemoteObject>> remoteObjects;

    public LifecycleManager() {
        remoteObjects = new ConcurrentHashMap<>();
    }

    public synchronized Object getRemoteObject(Class<?> clazz) throws BadConstructorException {
        remoteObjects.putIfAbsent(clazz, new CopyOnWriteArraySet<>());
        Set<RemoteObject> objects = remoteObjects.get(clazz);
        RemoteObject remoteObject = objects.stream().findFirst().orElseGet(() -> {
           try {
               return createRemoteObject(clazz);
           } catch (BadConstructorException e) {
               throw new RuntimeException("Failed to create remote object: " + clazz.getName(), e);
           }
        });
        return remoteObject.getServant();
    }

    private RemoteObject createRemoteObject(Class<?> clazz) throws BadConstructorException {
        if (!clazz.isAnnotationPresent(Component.class)) {
            throw new RuntimeException(clazz.getName() + " is not annotated with @Component");
        }

        Scope scope = clazz.getAnnotation(Scope.class);
        CreationStrategy creationStrategy = clazz.getAnnotation(CreationStrategy.class);

        LifecycleStrategy strategy = (scope != null &&scope.value() == ScopeType.PER_REQUEST) ?
                        new PerRequestInstance() :
                        new StaticInstance();

        ResourceStrategy resource =
                (creationStrategy != null && creationStrategy.value() == CreationStrategyType.POOLING) ?
                        new PoolingResource(clazz) :
                        new LazyAcquisitionResource(clazz);

        RemoteObject remoteObject = new RemoteObject(clazz, strategy, resource);
        this.remoteObjects.get(clazz).add(remoteObject);
        return remoteObject;
    }

    // criado para testes
    public void listAllRemoteObjectsByClass(Class<?> clazz) {
        Set<RemoteObject> objects = remoteObjects.get(clazz);
        for (RemoteObject remoteObject : objects) {
            System.out.println(remoteObject);
        }
    }

    public void releaseRemoteObject(RemoteObject servant) {
        Set<RemoteObject> objects = remoteObjects.get(servant.getClazz());

        // se for per request e for lazy, eu devo remover ele do set, dado que o contexto dele acabou
        if (servant.getStrategy().getClass() == PerRequestInstance.class &&
            servant.getResource().getClass() == LazyAcquisitionResource.class) {

            objects.remove(servant);
        } else if (servant.getResource().getClass() == PoolingResource.class) {
            servant.releaseServant(servant);
        }
    }
}
