package handler.tcp;

import invoker.Invoker;
import lifecycle.exceptions.BadConstructorException;
import marshaller.HttpMarshaller;
import message.HTTPMessage;
import message.HttpRequest;
import message.HttpResponse;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

class TCP_RequestHandler implements Runnable {
    private Socket clientSocket;

    private Invoker invoker;

    private HttpMarshaller marshaller;

    TCP_RequestHandler(Socket clientSocket, Invoker invoker) {
        this.clientSocket = clientSocket;
        this.invoker = invoker;
        this.marshaller = new HttpMarshaller();
    }

    @Override
    public void run() {
        HttpRequest request = readRequest();
        if(request == null) {
            sendResponse(null);
            return;
        }
        //interceptors

        HttpResponse response;
        try {
            System.out.println(request);
            response = invoker.invoke(request);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 BadConstructorException e) {
            throw new RuntimeException(e);
        }

        //interceptors

        //faz o marshall da resposta
        sendResponse(response);
    }

    private void sendResponse(HttpResponse response) {
        try {
            if(response == null) {
                response = new HttpResponse();
                response.setStatusCode(404);
                response.setStatusMessage("Not Found");
                response.setBody("erro");
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Content-Length", String.valueOf(response.getBody().getBytes(StandardCharsets.UTF_8).length));
                response.setHeaders(headers);
            } 
            
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
            String httpResponse = marshaller.serialize(response);
            System.out.println(httpResponse);
            writer.write(httpResponse);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpRequest readRequest() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            StringBuilder requestBuilder = new StringBuilder();
            String inputLine = reader.readLine();
            if (inputLine == null || inputLine.isEmpty()) {
                return null;
            }
            requestBuilder.append(inputLine).append("\r\n");
            String line;
            while ((line = reader.readLine()) != null) {
                requestBuilder.append(line).append("\r\n");
                if (line.isEmpty()) {
                    break;
                }
            }

            while ((line = reader.readLine()) != null) {
                requestBuilder.append(line).append("\r\n");
            }

            String httpRequest = requestBuilder.toString();
            return marshaller.deserialize(httpRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
