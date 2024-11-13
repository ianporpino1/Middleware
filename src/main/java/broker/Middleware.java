package broker;

import handler.interfaces.IServerRequestHandler;
import handler.tcp.TCP_ServerRequestHandler;
import handler.udp.UDP_ServerRequestHandler;
import invoker.Invoker;
import lifecycle.LifecycleManager;
import lifecycle.LookupService;

public class Middleware {
    public IServerRequestHandler requestHandler;

    private final Invoker invoker;
    
    private final LookupService lookupService;

    public Middleware() {
        this.invoker = new Invoker();
        this.lookupService = new LookupService();
    }

    public void start(int port, String protocol) {
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
