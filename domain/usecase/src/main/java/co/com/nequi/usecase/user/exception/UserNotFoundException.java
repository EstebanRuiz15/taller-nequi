package co.com.nequi.usecase.user.exception;

public class UserNotFoundException extends RuntimeException {
    private final int statusCode;

    public UserNotFoundException(String message) {
        super(message);
        this.statusCode = 404; 
    }

    public int getStatusCode() {
        return statusCode;
    }
}
