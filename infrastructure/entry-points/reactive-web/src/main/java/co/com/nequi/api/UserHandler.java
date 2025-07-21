package co.com.nequi.api;

import co.com.nequi.api.exceptions.handler.ErrorHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import co.com.nequi.usecase.user.UserUseCase;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserHandler {
    private static final Logger log = LogManager.getLogger(UserHandler.class);
    private final UserUseCase userUseCase;

    // POST /users
    public Mono<ServerResponse> listenCreateUser(ServerRequest serverRequest) {
        String idStr = serverRequest.queryParam("id").orElse("");
        
        if (idStr.trim().isEmpty()) {
            return ErrorHandler.handleValidationError("El id es requerido", serverRequest);
        }
        
        try {
            Integer id = Integer.valueOf(idStr.trim());
            
            if (id <= 0) {
                return ErrorHandler.handleValidationError("El id debe ser un número positivo", serverRequest);
            }
            
            return userUseCase.createUserById(id)
                .flatMap(user -> ServerResponse.ok().bodyValue(user))
                .onErrorResume(e -> {
                    log.error("Error en createUserById: {}", e.getMessage(), e);
                    return ErrorHandler.handleError(e, serverRequest);
                });
        } catch (NumberFormatException e) {
            log.error("Id no numérico en createUserById: {}", idStr);
            return ErrorHandler.handleValidationError("El id debe ser un número válido", serverRequest);
        }
    }

    public Mono<ServerResponse> listenGetUserById(ServerRequest serverRequest) {
        try {
            String idStr = serverRequest.pathVariable("id");
            
            if (idStr.trim().isEmpty()) {
                return ErrorHandler.handleValidationError("El id es requerido", serverRequest);
            }
            
            Integer id = Integer.valueOf(idStr.trim());
            
            if (id <= 0) {
                return ErrorHandler.handleValidationError("El id debe ser un número positivo", serverRequest);
            }
            
            return userUseCase.getUserById(id)
                .flatMap(user -> ServerResponse.ok().bodyValue(user))
                .onErrorResume(e -> {
                    log.error("Error en getUserById: {}", e.getMessage(), e);
                    return ErrorHandler.handleError(e, serverRequest);
                });
        } catch (NumberFormatException e) {
            log.error("Id no numérico en getUserById: {}", e.getMessage());
            return ErrorHandler.handleValidationError("El id debe ser un número válido", serverRequest);
        } catch (Exception e) {
            log.error("Error obteniendo path variable id: {}", e.getMessage());
            return ErrorHandler.handleValidationError("El id es requerido", serverRequest);
        }
    }

    public Mono<ServerResponse> listenGetAllUsers(ServerRequest serverRequest) {
        return userUseCase.getAllUsers()
            .collectList()
            .flatMap(users -> ServerResponse.ok().bodyValue(users))
            .onErrorResume(e -> {
                log.error("Error en getAllUsers: {}", e.getMessage(), e);
                return ErrorHandler.handleError(e, serverRequest);
            });
    }

    public Mono<ServerResponse> listenGetUsersByName(ServerRequest serverRequest) {
        String name = serverRequest.queryParam("name").orElse("");
        
        if (name.trim().isEmpty()) {
            return ErrorHandler.handleValidationError("El nombre es requerido", serverRequest);
        }
        
        if (name.trim().length() < 2) {
            return ErrorHandler.handleValidationError("El nombre debe tener al menos 2 caracteres", serverRequest);
        }
        
        return userUseCase.getUsersByName(name.trim())
            .collectList()
            .flatMap(users -> ServerResponse.ok().bodyValue(users))
            .onErrorResume(e -> {
                log.error("Error en getUsersByName: {}", e.getMessage(), e);
                return ErrorHandler.handleError(e, serverRequest);
            });
    }
}
