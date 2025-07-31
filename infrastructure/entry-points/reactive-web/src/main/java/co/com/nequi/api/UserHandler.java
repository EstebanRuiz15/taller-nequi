package co.com.nequi.api;

import co.com.nequi.api.exceptions.handler.ErrorHandler;
import co.com.nequi.exception.TechnicalException;
import co.com.nequi.enums.TechnicalMessage;
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
            return ErrorHandler.handleError(new TechnicalException(TechnicalMessage.INVALID_PARAMETER), serverRequest);
        }
        Integer id;
        try {
            id = Integer.valueOf(idStr.trim());
        } catch (NumberFormatException e) {
            log.error("Id no numérico en createUserById: {}", idStr);
            return ErrorHandler.handleError(new TechnicalException(TechnicalMessage.INVALID_PARAMETER), serverRequest);
        }
        if (id <= 0) {
            return ErrorHandler.handleError(new TechnicalException(TechnicalMessage.INVALID_PARAMETER), serverRequest);
        }
        return userUseCase.createUserById(id)
            .flatMap(user -> ServerResponse.ok().bodyValue(user))
            .doOnError(e -> log.error("Error en createUserById", e))
            .onErrorResume(e -> ErrorHandler.handleError(e, serverRequest));
    }

    public Mono<ServerResponse> listenGetUserById(ServerRequest serverRequest) {
        String idStr = serverRequest.pathVariable("id");
        if (idStr.trim().isEmpty()) {
            return ErrorHandler.handleError(new TechnicalException(TechnicalMessage.INVALID_PARAMETER), serverRequest);
        }
        Integer id;
        try {
            id = Integer.valueOf(idStr.trim());
        } catch (NumberFormatException e) {
            log.error("Id no numérico en getUserById: {}", e.getMessage());
            return ErrorHandler.handleError(new TechnicalException(TechnicalMessage.INVALID_PARAMETER), serverRequest);
        }
        if (id <= 0) {
            return ErrorHandler.handleError(new TechnicalException(TechnicalMessage.INVALID_PARAMETER), serverRequest);
        }
        return userUseCase.getUserById(id)
            .flatMap(user -> ServerResponse.ok().bodyValue(user))
           .doOnError(e -> log.error("Error en getUserById", e))
            .onErrorResume(e -> ErrorHandler.handleError(e, serverRequest));
    }

    public Mono<ServerResponse> listenGetAllUsers(ServerRequest serverRequest) {
        return userUseCase.getAllUsers()
            .collectList()
            .flatMap(users -> ServerResponse.ok().bodyValue(users))
            .doOnError(e -> log.error("Error en getAllUsers", e))
            .onErrorResume(e -> ErrorHandler.handleError(e, serverRequest));
    }

    public Mono<ServerResponse> listenGetUsersByName(ServerRequest serverRequest) {
        String name = serverRequest.queryParam("name").orElse("");
        
        if (name.trim().isEmpty()) {
            return ErrorHandler.handleError(new TechnicalException(TechnicalMessage.INVALID_PARAMETER), serverRequest);
        }

        if (name.trim().length() < 2) {
            return ErrorHandler.handleError(new TechnicalException(TechnicalMessage.INVALID_PARAMETER), serverRequest);
        }
        
        return userUseCase.getUsersByName(name.trim())
            .collectList()
            .flatMap(users -> ServerResponse.ok().bodyValue(users))
            .doOnError(e -> log.error("Error en getUsersByName", e))
            .onErrorResume(e -> ErrorHandler.handleError(e, serverRequest));
    }
}
