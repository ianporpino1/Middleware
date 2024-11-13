package broker;

import handler.interfaces.IServerRequestHandler;
import handler.tcp.TCP_ServerRequestHandler;
import handler.udp.UDP_ServerRequestHandler;
import lifecycle.LifecycleManager;

public class Middleware {
    public IServerRequestHandler requestHandler;

    public Invoker invoker;

    private Marshaller marshaller;

    public LifecycleManager lifecycleManager;
    
    public void start(int port, String protocol) {
        this.lifecycleManager = new LifecycleManager();

        this.invoker = new Invoker(lifecycleManager);


        this.marshaller = new Marshaller();

        //forma errada
        switch (protocol){
            case "tcp":
                this.requestHandler = new TCP_ServerRequestHandler(port, invoker,new Marshaller());
                break;
            case "udp":
                this.requestHandler = new UDP_ServerRequestHandler(port, invoker,new Marshaller());
        }
        
    }

    public void addComponent(Object component) {
        lookupService.addComponent();
    }
}
