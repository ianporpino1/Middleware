package extension;

import message.HttpRequest;
import message.HttpResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LoggingInterceptor implements Interceptor {
    private static final Logger logger = Logger.getLogger(LoggingInterceptor.class.getName());

    static {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new CustomLogFormatter());
        logger.setUseParentHandlers(false);
        logger.addHandler(handler);
    }

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

    private static class CustomLogFormatter extends Formatter {
        private static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";

        @Override
        public String format(LogRecord record) {
            String level = "[" + record.getLevel().getName() + "]";
            String timestamp = new SimpleDateFormat(DATE_FORMAT).format(new Date(record.getMillis()));
            String message = formatMessage(record);

            return String.format("%s -- %s : %s%n", level, timestamp, message);
        }
    }
}
