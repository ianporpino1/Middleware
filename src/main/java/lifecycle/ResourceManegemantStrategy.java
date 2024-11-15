package lifecycle;

public abstract class ResourceManegemantStrategy {
    public abstract Object getServant();
    public abstract void returnServant(Object servant);
    public abstract void destroyServant(Object servant);
}
