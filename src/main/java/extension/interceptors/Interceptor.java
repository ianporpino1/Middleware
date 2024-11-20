package extension.interceptors;

import message.HTTPMessage;

public interface Interceptor {
    void verifyBefore(HTTPMessage message) throws SecurityException;
    void verifyAfter(HTTPMessage message) throws SecurityException;
}
