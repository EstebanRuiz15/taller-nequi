package co.com.nequi.webclient.adapter;

import co.com.nequi.model.user.gateways.UserWebClientGateway;
import co.com.nequi.model.user.User;
import co.com.nequi.webclient.response.UserResponse;
import co.com.nequi.webclient.mapper.UserResponseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import co.com.nequi.webclient.exception.ExternalServiceException;
import co.com.nequi.webclient.exception.EmptyResponseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Component
public class WebClientAdapter implements UserWebClientGateway {
    private static final Logger log = LogManager.getLogger(WebClientAdapter.class);

    private final WebClient webClient;

    @Autowired
    public WebClientAdapter(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<User> getUserById(Integer id) {
        return webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                    clientResponse -> {
                        if (clientResponse.statusCode().value() == 404) {
                            return Mono.error(new EmptyResponseException("El usuario no existe", 404));
                        }
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new ExternalServiceException("Error en la petición: " + errorBody, clientResponse.rawStatusCode())));
                    })
                .bodyToMono(UserResponse.class)
                .doOnNext(response -> log.info("Respuesta cruda del servicio externo: {}", response))
                .switchIfEmpty(Mono.error(new EmptyResponseException("Respuesta vacía del servicio externo", 404)))
                .map(response -> {
                    if (response == null || response.getData() == null) {
                        log.warn("Usuario no encontrado o respuesta inválida: {}", response);
                        throw new EmptyResponseException("Usuario no encontrado o respuesta inválida", 404);
                    }
                    User user = UserResponseMapper.toUser(response.getData());
                    log.info("Usuario mapeado: {}", user);
                    return user;
                });
    }
}