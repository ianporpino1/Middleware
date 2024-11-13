package handler.tcp;

import invoker.Invoker;
import message.HTTPMessage;

import java.io.*;
import java.net.Socket;

class TCP_RequestHandler implements Runnable {
    private Socket clientSocket;

    private Invoker invoker;

    private Marshaller marshaller;

    TCP_RequestHandler(Socket clientSocket, Invoker invoker) {
        this.clientSocket = clientSocket;
        this.invoker = invoker;
        this.marshaller = new Marshaller();
    }

    @Override
    public void run() {
        //recebe a request
        HTTPMessage httpMessage = readRequest();

        //faz o unmarshall ou ja chama o invoker?
        HTTPMessage response = invoker.invoke(httpMessage);

        //faz o marshall da resposta
        sendResponse(response);
    }

    private void sendResponse(HTTPMessage response) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {
            //n sei se eh a melhor abordagem, pq writer vai ter que usar write()
            //dentro do marshaller
            //outra opcao seria mandar pro marshaller o outputstream/inpustream
            marshaller.serialize(writer, response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HTTPMessage readRequest() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            
            return marshaller.desserialize(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
