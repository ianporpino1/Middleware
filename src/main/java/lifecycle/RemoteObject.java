package lifecycle;

import lifecycle.exceptions.BadConstructorException;

public class RemoteObject {
    private final Class<?> clazz;
    private final LifecycleStrategy strategy;
    private final ResourceStrategy resource;

    public RemoteObject(Class<?> clazz, LifecycleStrategy strategy, ResourceStrategy resource) {
        this.clazz = clazz;
        this.strategy = strategy;
        this.resource = resource;
    }

//    public RemoteObject(Class<?> clazz) throws BadConstructorException {
//        this.clazz = clazz;
//
//        // default
//        ScopeType scopeType = ScopeType.STATIC_INSTANCE;
//        CreationStrategyType creationStrategyType = CreationStrategyType.LAZY_ACQUISITION;
//
//        // pega anotacoes do bean
//        if (clazz.isAnnotationPresent(Scope.class)) {
//            ScopeType scope = clazz.getAnnotation(Scope.class).value();
//            switch (scope) {
//
//            }
//        }
//
//        if (clazz.isAnnotationPresent(CreationStrategy.class)) {
//            CreationStrategy creationStrategy = clazz.getAnnotation(CreationStrategy.class);
//            creationStrategyType = creationStrategy.value();
//        }
//
//        switch (creationStrategyType) {
//            case POOLING:
//                this.resourceManagementStrategy = null;
//                break;
//            case LAZY_ACQUISITION:
//            default:
//                this.resourceManagementStrategy = new LazyAcquisition(clazz);
//                break;
//        }
//
//        switch (scopeType) {
//            case PER_REQUEST:
//                this.livecycleStrategy = null;
//            case STATIC_INSTANCE:
//            default:
//                this.livecycleStrategy = new StaticInstance(this.resourceManagementStrategy);
//                break;
//        }
//    }

    // pega um servant (instancia) desse objeto remoto
    public Object getServant() throws BadConstructorException {
        return strategy.getServant(resource);
    }

    // devolve servant (método chamado quando nao se precisa mais do servant)
    public void releaseServant(Object servant) {
        strategy.releaseServant(resource, servant);
    }
}
