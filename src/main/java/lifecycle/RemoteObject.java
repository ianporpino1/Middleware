package lifecycle;

import annotation.scope.Scope;
import annotation.scope.ScopeType;
import annotation.strategy.CreationStrategy;
import annotation.strategy.CreationStrategyType;
import lifecycle.exceptions.BadConstructorException;

public class RemoteObject {
    Class<?> clazz;
    LivecycleStrategy livecycleStrategy;
    ResourceManagementStrategy resourceManagementStrategy;

    public RemoteObject(Class<?> clazz) throws BadConstructorException {
        this.clazz = clazz;

        // the scope and creation type are set to a default

        ScopeType scopeType = ScopeType.STATIC_INSTANCE;
        CreationStrategyType creationStrategyType = CreationStrategyType.LAZY_ACQUISITION;

        // we get the annotation's value

        if (clazz.isAnnotationPresent(Scope.class)) {
            // Get the annotation instance
            Scope scope = clazz.getAnnotation(Scope.class);
            scopeType = scope.value();
        }
        if (clazz.isAnnotationPresent(CreationStrategy.class)) {
            // Get the annotation instance
            CreationStrategy creationStrategy = clazz.getAnnotation(CreationStrategy.class);
            creationStrategyType = creationStrategy.value();
        }

        // the strategies are created using the annotations or default values

        switch (creationStrategyType) {
            case POOLING:
                this.resourceManagementStrategy = null;
                break;
            case LAZY_ACQUISITION:
            default:
                this.resourceManagementStrategy = new LazyAcquisition(clazz);
                break;
        }

        switch (scopeType) {
            case PER_REQUEST:
                this.livecycleStrategy = null;
            case STATIC_INSTANCE:
            default:
                this.livecycleStrategy = new StaticInstance(this.resourceManagementStrategy);
                break;
        }

    }

    public Object getServant() throws BadConstructorException {
        return livecycleStrategy.getServant();
    }

    public void returnServant(Object servant) {
        livecycleStrategy.returnServant(servant);
    }
}
