package exceptions;

public class MarshallerException extends RemoteErrorException{
    public MarshallerException(String message, Throwable cause) {
        super(message, cause);
    }
}
