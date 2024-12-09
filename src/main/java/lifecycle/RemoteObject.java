package lifecycle;

import exceptions.BadConstructorException;

public class RemoteObject {
    private final Class<?> clazz;
    private final LifecycleStrategy strategy;
    private final ResourceStrategy resource;

    public RemoteObject(Class<?> clazz, LifecycleStrategy strategy, ResourceStrategy resource) {
        this.clazz = clazz;
        this.strategy = strategy;
        this.resource = resource;
    }

    // pega um servant (instancia) desse objeto remoto
    public Object getServant() throws BadConstructorException {
        return strategy.getServant(resource);
    }

    // devolve servant (funcao chamada quando nao se precisa mais do servant)
    public void releaseServant(Object servant) {
        strategy.releaseServant(resource, servant);
    }


    @Override
    public String toString() {
        return "RemoteObject {" +
                "clazz=" + clazz +
                ", strategy=" + strategy +
                ", resource=" + resource +
                '}';
    }

    public Class<?> getClazz() { return this.clazz; }

    public LifecycleStrategy getStrategy() {
        return strategy;
    }

    public ResourceStrategy getResource() {
        return resource;
    }
}

