package lifecycle;

public abstract class LivecycleStrategy {
    protected final ResourceManagementStrategy resources;

    public abstract Object getServant();
    public abstract void returnServant(Object servant);

    public LivecycleStrategy(ResourceManagementStrategy resources) {
        this.resources = resources;
    }


}
