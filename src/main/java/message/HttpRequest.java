package message;

import java.util.Map;

public class HttpRequest {
    private String method;
    private String url;
    private Map<String,String> headers;
    private String body;
    
    
    public HttpRequest(String method, String url, Map<String, String> headers,String body) {
        this.method = method;
        this.url = url;
        this.body = body;
        this.headers = headers;
    }
    
    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
