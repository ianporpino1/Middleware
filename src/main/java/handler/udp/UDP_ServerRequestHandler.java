package handler.udp;

import exceptions.ServerRequestHandlerException;
import handler.interfaces.IServerRequestHandler;
import invoker.Invoker;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDP_ServerRequestHandler implements IServerRequestHandler {
    private DatagramSocket socket;
    
    private final ExecutorService executorService;
    
    public UDP_ServerRequestHandler(int port, Invoker invoker){
        start(port);

        this.executorService = Executors.newCachedThreadPool();

        while (!Thread.currentThread().isInterrupted()) {
            try {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                executorService.execute(new UDP_RequestHandler(packet, socket,invoker));
            } catch (IOException e) {
                if (socket.isClosed()) {
                    break;
                }
            }
        }
    }

    @Override
    public void start(int port) {
        try {
            this.socket = new DatagramSocket(port);
        } catch (SocketException e) {
            throw new ServerRequestHandlerException(e.getMessage());
        }
        
    }
}
