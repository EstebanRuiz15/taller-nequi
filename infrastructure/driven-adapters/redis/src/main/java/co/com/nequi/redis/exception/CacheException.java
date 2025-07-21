package co.com.nequi.redis.exception;

public class CacheException extends RuntimeException {
    private final int statusCode;

    public CacheException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public CacheException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
