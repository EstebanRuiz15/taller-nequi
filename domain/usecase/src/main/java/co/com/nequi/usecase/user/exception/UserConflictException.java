package co.com.nequi.usecase.user.exception;

public class UserConflictException extends RuntimeException {
    private final int statusCode;

    public UserConflictException(String message) {
        super(message);
        this.statusCode = 409; 
    }

    public int getStatusCode() {
        return statusCode;
    }
}
