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
            MiddlewareApplication application = new MiddlewareApplication();//args
            application.start();
        }
    }
    
    
    
    public IServerRequestHandler requestHandler;

    private final Invoker invoker;
    
    private final LookupService lookupService;

    public MiddlewareApplication() {
        this.invoker = new Invoker();
        this.lookupService = new LookupService();
        //talvez criar marshaller aqui
    }

    private void start() {
        scanAndRegisterComponents();
        //por enquanto
        run(8080,"tcp");
    }

    private void scanAndRegisterComponents() {
        //biblioteca p scanear classpath
        Reflections reflections = new Reflections("", Scanners.SubTypes);
        Set<Class<?>> components = reflections.getTypesAnnotatedWith(Component.class);

        for (Class<?> clazz : components) {
            addComponent(clazz);
            System.out.println("Componente registrado: " + clazz.getSimpleName());
        }
    }

    public void run(int port, String protocol) {
        //forma errada
        switch (protocol){
            case "tcp":
                this.requestHandler = new TCP_ServerRequestHandler(port, invoker);
                break;
//            case "udp":
//                this.requestHandler = new UDP_ServerRequestHandler(port, invoker);
        }
        
    }

    public void addComponent(Class<?> component) {
        lookupService.registerRoute(component);
    }
}
