package lifecycle;

public class StaticInstance extends LivecycleStrategy{

    public StaticInstance(ResourceManagementStrategy resources) {
        super(resources);
        resources.create_servant();
    }

    @Override
    public Object getServant() {
        return resources.getServant();
    }

    @Override
    public void returnServant(Object servant) {
        resources.returnServant(servant);
    }
}
