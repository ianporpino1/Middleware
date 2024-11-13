import broker.handler.interfaces.IServerRequestHandler;
import broker.handler.tcp.TCP_ServerRequestHandler;
import broker.handler.udp.UDP_ServerRequestHandler;

public class Middleware {
    public IServerRequestHandler requestHandler;

    public Invoker invoker;
    
    public void start(int port, String protocol) {
        this.invoker = new Invoker();
        
        switch (protocol){
            case "tcp":
                this.requestHandler = new TCP_ServerRequestHandler(port, invoker, new TCP_Marshaller());//provavelemente strategy no marshall
                break; 
            case "udp":
                this.requestHandler = new UDP_ServerRequestHandler(port, invoker, new UDP_Marshaller());
        }
        
        //switch case
        System.out.println("Middleware started");
    }
    
    
    public static void main(String[] args) {
        
        Middleware middleware = new Middleware();
        middleware.start(8080, "tcp");
        
    }
}
