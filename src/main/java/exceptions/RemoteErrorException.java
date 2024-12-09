package exceptions;

public class RemoteErrorException extends RuntimeException {

    public RemoteErrorException(String message) {
        super(message);
    }

    public RemoteErrorException(String message, Throwable cause) { super(message, cause); }
}
