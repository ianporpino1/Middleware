package handler.tcp;

import exceptions.ServerRequestHandlerException;
import handler.interfaces.IHandler;
import invoker.Invoker;
import exceptions.BadConstructorException;
import marshaller.HttpMarshaller;
import message.HttpRequest;
import message.HttpResponse;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

class TCP_RequestHandler implements Runnable, IHandler {
    private final Socket clientSocket;

    private final Invoker invoker;

    private final HttpMarshaller marshaller;

    TCP_RequestHandler(Socket clientSocket, Invoker invoker) {
        this.clientSocket = clientSocket;
        this.invoker = invoker;
        this.marshaller = new HttpMarshaller();
    }

    @Override
    public void run() {
        handle();
    }

    @Override
    public void handle() {
        HttpRequest request = readRequest();
        if (request == null) {
            sendResponse(null);
            return;
        }

        HttpResponse response;
        try {
            response = invoker.invoke(request);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 BadConstructorException e) {
            throw new ServerRequestHandlerException(e.getMessage());
        }

        sendResponse(response);
    }

    private void sendResponse(HttpResponse response) {
        try {
            if(response == null) {
                response = new HttpResponse();
                response.setStatusCode(404);
                response.setStatusMessage("Not Found");
                response.setBody("erro");
            }
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Content-Length", String.valueOf(response.getBody().getBytes().length));
            response.setHeaders(headers);
            
            
            String httpResponse = marshaller.serialize(response);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
            writer.write(httpResponse);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new ServerRequestHandlerException("Erro ao enviar a resposta HTTP: " + e.getMessage());
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
            
            int contentLength = 0;
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                requestBuilder.append(line).append("\r\n");
                if (line.toLowerCase().startsWith("content-length:")) {
                    contentLength = Integer.parseInt(line.split(":")[1].trim());
                }
            }
            
            if (contentLength > 0) {
                char[] body = new char[contentLength];
                reader.read(body, 0, contentLength);
                requestBuilder.append(body);
            }

            String httpRequest = requestBuilder.toString();
            return marshaller.deserialize(httpRequest);
        } catch (IOException e) {
            throw new ServerRequestHandlerException("Error reading HTTP request" + e.getMessage());
        }
    }
}
