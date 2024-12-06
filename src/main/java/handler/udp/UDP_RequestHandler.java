package handler.udp;

import handler.interfaces.IHandler;
import invoker.Invoker;
import lifecycle.exceptions.BadConstructorException;
import marshaller.HttpMarshaller;
import message.HttpRequest;
import message.HttpResponse;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

class UDP_RequestHandler implements Runnable, IHandler {
    private final DatagramPacket packet;
    private final DatagramSocket socket;

    private Invoker invoker;

    private HttpMarshaller marshaller;
    
    public UDP_RequestHandler(DatagramPacket packet, DatagramSocket socket, Invoker invoker) {
        this.packet = packet;
        this.invoker = invoker;
        this.socket = socket;
        this.marshaller = new HttpMarshaller();
    }

    @Override
    public void run() {
        handle();
    }

    @Override
    public void handle() {
        HttpRequest request = readRequest();
        
        HttpResponse response;
        try {
            response = invoker.invoke(request);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 BadConstructorException e) {
            throw new RuntimeException(e);
        }

        sendResponse(response);
        
    }

    private void sendResponse(HttpResponse response) {
        InetSocketAddress clientAddress = new InetSocketAddress(packet.getAddress(), packet.getPort());
        String responseStr = response.toString();
        byte[] responseData = responseStr.getBytes();
        DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, clientAddress);
        try{
            socket.send(responsePacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HttpRequest readRequest() {
        String httpString = new String(packet.getData(), 0, packet.getLength());
        try{
            return marshaller.deserialize(httpString);
        } catch (IOException e) {
            throw new RuntimeException("Error reading HTTP request", e);
        }
    }


}
