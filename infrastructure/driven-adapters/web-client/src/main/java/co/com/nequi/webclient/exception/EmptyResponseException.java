package co.com.nequi.webclient.exception;

public class EmptyResponseException extends RuntimeException {
    private final Integer statusCode;
    public EmptyResponseException(String message, Integer statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
