package exceptions;

public class ConfigurationException extends RemoteErrorException {

    public ConfigurationException(String message) {
        super(message);
    }
}
