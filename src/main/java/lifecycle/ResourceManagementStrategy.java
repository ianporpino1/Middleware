package lifecycle;

public abstract class ResourceManagementStrategy {
    protected Class<?> clazz;
    public abstract Object getServant();
    public abstract void create_servant();
    public abstract void returnServant(Object servant);
    public abstract void destroyServant(Object servant);

    public ResourceManagementStrategy(Class<?> clazz) {
        this.clazz = clazz;
    }

    protected Object create(){
        //todo
        return null;
    }
}