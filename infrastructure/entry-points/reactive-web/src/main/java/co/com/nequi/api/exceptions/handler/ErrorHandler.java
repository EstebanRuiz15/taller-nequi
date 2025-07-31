package co.com.nequi.api.exceptions.handler;

import co.com.nequi.api.exceptions.dto.ErrorResponse;
import co.com.nequi.exception.BusinessException;
import co.com.nequi.exception.TechnicalException;
import co.com.nequi.webclient.exception.EmptyResponseException;
import co.com.nequi.webclient.exception.ExternalServiceException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ErrorHandler {

    private ErrorHandler() {
     
    }

    public static Mono<ServerResponse> handleError(Throwable e, ServerRequest request) {
        String path = request.path();
        String stackTrace = getStackTrace(e);

        if (e instanceof TechnicalException ex) {
            var tm = ex.getTechnicalMessage();
            log.error("TechnicalException: {} | code: {} | path: {}\n{}", tm.getMessage(), tm.getCode(), path, stackTrace);
            ErrorResponse errorResponse = ErrorResponse.of(
                tm.getMessage(),
                400,
                stackTrace,
                path
            );
            return ServerResponse.status(400).bodyValue(errorResponse);
        }

        if (e instanceof BusinessException ex) {
            var tm = ex.getTechnicalMessage();
            log.warn("BusinessException: {} | code: {} | path: {}\n{}", ex.getMessage(), tm.getCode(), path, stackTrace);
            ErrorResponse errorResponse = ErrorResponse.of(
                ex.getMessage(),
                Integer.parseInt(tm.getCode()),
                stackTrace,
                path
            );
            return ServerResponse.status(422).bodyValue(errorResponse);
        }

       

        if (e instanceof EmptyResponseException ex) {
            log.error("EmptyResponseException: {} | path: {} | status: {}\n{}", ex.getMessage(), path, ex.getStatusCode(), stackTrace);
            ErrorResponse errorResponse = ErrorResponse.of(
                ex.getMessage(), 
                ex.getStatusCode(), 
                stackTrace, 
                path
            );
            return ServerResponse.status(ex.getStatusCode()).bodyValue(errorResponse);
        }

        if (e instanceof ExternalServiceException ex) {
            log.error("ExternalServiceException: {} | path: {} | status: {}\n{}", ex.getMessage(), path, ex.getStatusCode(), stackTrace);
            ErrorResponse errorResponse = ErrorResponse.of(
                ex.getMessage(), 
                ex.getStatusCode(), 
                stackTrace, 
                path
            );
            return ServerResponse.status(ex.getStatusCode()).bodyValue(errorResponse);
        }

        log.error("Unhandled exception: {} | path: {}\n{}", e.getMessage(), path, stackTrace);
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
