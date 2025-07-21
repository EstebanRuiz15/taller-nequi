package co.com.nequi.api;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

@ContextConfiguration(classes = {RouterRest.class, UserHandler.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;
    
    @org.springframework.boot.test.mock.mockito.MockBean
    private co.com.nequi.usecase.user.UserUseCase userUseCase;

    @Test
    void testListenGETUseCase() {
        // Mock: cuando se llama getUserById, retorna un usuario dummy
        co.com.nequi.model.user.User userDummy = co.com.nequi.model.user.User.builder()
                .id(123)
                .firstName("Juan")
                .lastName("Pérez")
                .email("juan@example.com")
                .build();
        org.mockito.Mockito.when(userUseCase.getUserById(org.mockito.ArgumentMatchers.anyInt()))
                .thenReturn(reactor.core.publisher.Mono.just(userDummy));

        webTestClient.get()
                .uri("/users/123")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testListenGETOtherUseCase() {
        // Mock: cuando se llama getAllUsers, retorna una lista dummy
        co.com.nequi.model.user.User user1 = co.com.nequi.model.user.User.builder()
                .id(1)
                .firstName("Ana")
                .lastName("Gómez")
                .email("ana@example.com")
                .build();
        co.com.nequi.model.user.User user2 = co.com.nequi.model.user.User.builder()
                .id(2)
                .firstName("Luis")
                .lastName("Martínez")
                .email("luis@example.com")
                .build();
        org.mockito.Mockito.when(userUseCase.getAllUsers())
                .thenReturn(reactor.core.publisher.Flux.just(user1, user2));

        webTestClient.get()
                .uri("/users")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testListenPOSTUseCase() {
        // Mock: cuando se llama createUserById, retorna un usuario dummy
        co.com.nequi.model.user.User userCreated = co.com.nequi.model.user.User.builder()
                .id(456)
                .firstName("Pedro")
                .lastName("Ramírez")
                .email("pedro@example.com")
                .build();
        org.mockito.Mockito.when(userUseCase.createUserById(org.mockito.ArgumentMatchers.anyInt()))
                .thenReturn(reactor.core.publisher.Mono.just(userCreated));

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/users").queryParam("id", "456").build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue("")
                .exchange()
                .expectStatus().isOk();
    }
}
