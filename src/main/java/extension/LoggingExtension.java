package extension;

import message.HttpRequest;
import message.HttpResponse;

import java.util.logging.Logger;

public class LoggingExtension implements Extension {
    private static final Logger logger = Logger.getLogger(LoggingExtension.class.getName());

    @Override
    public void beforeInvoke(HttpRequest request, HttpResponse response) {
        logger.info("Recebida requisição: " + request.getMethod() + " " + request.getUrl());
    }

    @Override
    public void afterInvoke(HttpRequest request, HttpResponse response) {
        logger.info("Resposta enviada: " + response.getStatusCode() + " - " + response.getBody());
    }

    @Override
    public void onError(HttpRequest request, HttpResponse response, Exception e) {
        logger.severe("Erro durante o processamento da requisição: " + request.getMethod() + " " +
                              request.getUrl() + " - Exceção: " + e.getMessage());
    }
}
