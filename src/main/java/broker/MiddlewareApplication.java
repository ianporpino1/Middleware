package broker;

import annotation.Component;
import handler.interfaces.IServerRequestHandler;
import handler.tcp.TCP_ServerRequestHandler;
import invoker.Invoker;
import lifecycle.LookupService;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.scanners.SubTypesScanner;
import java.util.Set;

public class MiddlewareApplication {
    public static void run(Class<?> appClass, String[] args) {
        if (appClass.isAnnotationPresent(annotation.MiddlewareApplication.class)) {
            String basePackage = appClass.getPackageName();
            MiddlewareApplication application = new MiddlewareApplication(basePackage);//args
            application.start();
        }
    }

    private final String basePackage;
    
    public IServerRequestHandler requestHandler;

    private final Invoker invoker;
    
    private final LookupService lookupService;

    public MiddlewareApplication(String basePackage) {
        this.basePackage = basePackage;
        this.invoker = new Invoker();
        this.lookupService = new LookupService();
        //talvez criar marshaller aqui
    }

    private void start() {
        scanAndRegisterComponents();
        //por enquanto
        run(8080);
    }

    private void scanAndRegisterComponents() {
        //biblioteca p scanear classpath
        Reflections reflections = new Reflections(basePackage, Scanners.TypesAnnotated);
        Set<Class<?>> components = reflections.getTypesAnnotatedWith(Component.class);

        for (Class<?> clazz : components) {
            addComponent(clazz);
            System.out.println("Componente registrado: " + clazz.getSimpleName());
        }
    }

    public void run(int port) {
        this.requestHandler = new TCP_ServerRequestHandler(port, invoker);
    }

    public void addComponent(Class<?> component) {
        lookupService.registerRoute(component);
    }
}
