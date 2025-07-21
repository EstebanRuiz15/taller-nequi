package co.com.nequi.webclient.exception;

public class ExternalServiceException extends RuntimeException {
    private final int statusCode;

    public ExternalServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
