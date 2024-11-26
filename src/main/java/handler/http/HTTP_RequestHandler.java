package handler.http;

import handler.interfaces.IHandler;
import invoker.Invoker;
import message.HTTPMessage;
import message.HttpRequest;
import message.HttpResponse;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HTTP_RequestHandler implements Runnable, IHandler {
    private final Socket clientSocket;

    private final Invoker invoker;

    public HTTP_RequestHandler(Socket clientSocket, Invoker invoker) {
        this.clientSocket = clientSocket;
        this.invoker = invoker;
    }

    @Override
    public void run() {
        handle(clientSocket);
    }

    @Override
    public void handle(Socket clientSocket) {
//        HTTPMessage httpMessage;

        try {
            invoker.invoke(clientSocket);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //interceptors

//        sendResponse(httpMessage);

        try {
            clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    private void sendResponse(HTTPMessage response) {
//        try {
//            if(response == null) {
////                response = new HTTPMessage(
////
////                        response.statusCode(500);
////                response.statusMessage("Internal Error.");
////                response.body("erro");
////                );
//            }
//
//
//            writer.write(response.body().toString());
//            writer.flush();
//        } catch (IOException e) {
//            throw new RuntimeException("Erro ao enviar a resposta HTTP", e);
//        }
//    }
}
