package broker.handler.tcp;

import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import broker.handler.interfaces.IServerRequestHandler;

public class TCP_ServerRequestHandler implements IServerRequestHandler {
    private ServerSocket socket;

    private ExecutorService threadExecutor;
    
    public TCP_ServerRequestHandler(int port, Invoker invoker, Marshaller marshaller){
        start(port);

        this.threadExecutor = Executors.newFixedThreadPool(10);

        try {
            while (true) {
                threadExecutor.execute(new TCP_RequestHandler(this.socket.accept(), invoker, marshaller));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    public void start(int port) {
        
    }
}
