package co.com.nequi.api.exceptions.handler;

import co.com.nequi.api.exceptions.dto.ErrorResponse;
import co.com.nequi.usecase.user.exception.UserConflictException;
import co.com.nequi.usecase.user.exception.UserNotFoundException;
import co.com.nequi.webclient.exception.EmptyResponseException;
import co.com.nequi.webclient.exception.ExternalServiceException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Arrays;

public class ErrorHandler {

    private ErrorHandler() {
     
    }

    public static Mono<ServerResponse> handleError(Throwable e, ServerRequest request) {
        String path = request.path();
        String stackTrace = getStackTrace(e);
        
        if (e instanceof UserNotFoundException ex) {
            ErrorResponse errorResponse = ErrorResponse.of(
                ex.getMessage(), 
                ex.getStatusCode(), 
                stackTrace, 
                path
            );
            return ServerResponse.status(ex.getStatusCode()).bodyValue(errorResponse);
        }
        
        if (e instanceof UserConflictException ex) {
            ErrorResponse errorResponse = ErrorResponse.of(
                ex.getMessage(), 
                ex.getStatusCode(), 
                stackTrace, 
                path
            );
            return ServerResponse.status(ex.getStatusCode()).bodyValue(errorResponse);
        }
        
        if (e instanceof EmptyResponseException ex) {
            ErrorResponse errorResponse = ErrorResponse.of(
                ex.getMessage(), 
                ex.getStatusCode(), 
                stackTrace, 
                path
            );
            return ServerResponse.status(ex.getStatusCode()).bodyValue(errorResponse);
        }
        
        if (e instanceof ExternalServiceException ex) {
            ErrorResponse errorResponse = ErrorResponse.of(
                ex.getMessage(), 
                ex.getStatusCode(), 
                stackTrace, 
                path
            );
            return ServerResponse.status(ex.getStatusCode()).bodyValue(errorResponse);
        }
        
        ErrorResponse errorResponse = ErrorResponse.of(
            e.getMessage() != null ? e.getMessage() : "Error interno del servidor", 
            500, 
            stackTrace, 
            path
        );
        return ServerResponse.status(500).bodyValue(errorResponse);
    }
    
    public static Mono<ServerResponse> handleValidationError(String message, ServerRequest request) {
        String path = request.path();
        ErrorResponse errorResponse = ErrorResponse.of(message, 400, path);
        return ServerResponse.status(400).bodyValue(errorResponse);
    }
    
    private static String getStackTrace(Throwable e) {
        return Arrays.stream(e.getStackTrace())
                .limit(5) 
                .map(StackTraceElement::toString)
                .reduce((s1, s2) -> s1 + "\n" + s2)
                .orElse("No stack trace available");
    }
}
