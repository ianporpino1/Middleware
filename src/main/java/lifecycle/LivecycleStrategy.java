package lifecycle;

public abstract class LivecycleStrategy {
    public abstract Object getServant();
    public abstract void returnServant(Object servant);
}
