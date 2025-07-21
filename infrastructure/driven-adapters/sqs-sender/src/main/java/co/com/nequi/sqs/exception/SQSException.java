package co.com.nequi.sqs.exception;

public class SQSException extends RuntimeException {
    private final int statusCode;

    public SQSException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public SQSException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
