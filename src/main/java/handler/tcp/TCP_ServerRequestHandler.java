package handler.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import handler.interfaces.IServerRequestHandler;
import invoker.Invoker;

public class TCP_ServerRequestHandler implements IServerRequestHandler {
    private ServerSocket serverSocket;

    private final ExecutorService executorService;
    
    public TCP_ServerRequestHandler(int port, Invoker invoker){
        start(port);

        this.executorService = Executors.newCachedThreadPool();

        while (!Thread.currentThread().isInterrupted()) {
            try {
                Socket clientSocket = serverSocket.accept();
                executorService.execute(new TCP_RequestHandler(clientSocket, invoker));
            } catch (IOException e) {
                if (serverSocket.isClosed()) {
                    break;
                }
            }
        }
    }
    
    public void start(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
