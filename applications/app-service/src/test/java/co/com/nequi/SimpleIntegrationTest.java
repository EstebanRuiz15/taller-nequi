package co.com.nequi;

import co.com.nequi.api.UserHandler;
import co.com.nequi.api.RouterRest;
import co.com.nequi.model.user.User;
import co.com.nequi.usecase.user.UserUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {UserHandler.class, RouterRest.class})
class SimpleIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserUseCase userUseCase;

    @Test
    void createUser_ShouldReturnCreatedUser_IntegrationTest() {
        // Given
        User mockUser = User.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();
        
        when(userUseCase.createUserById(anyInt())).thenReturn(Mono.just(mockUser));

        // When & Then
        webTestClient.post()
                .uri("/users?id=1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.firstName").isEqualTo("John")
                .jsonPath("$.lastName").isEqualTo("Doe")
                .jsonPath("$.email").isEqualTo("john@example.com");
    }

    @Test
    void getAllUsers_ShouldReturnUsersList_IntegrationTest() {
        // Given
        User mockUser = User.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();
        
        when(userUseCase.getAllUsers()).thenReturn(Flux.just(mockUser));

        // When & Then
        webTestClient.get()
                .uri("/users")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(User.class)
                .hasSize(1);
    }

    @Test
    void getUserById_ShouldReturnUser_WhenExists_IntegrationTest() {
        // Given
        User mockUser = User.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();
        
        when(userUseCase.getUserById(1)).thenReturn(Mono.just(mockUser));

        // When & Then
        webTestClient.get()
                .uri("/users/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.firstName").isEqualTo("John");
    }

    @Test
    void searchUsersByName_ShouldReturnMatchingUsers_IntegrationTest() {
        // Given
        User mockUser = User.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();
        
        when(userUseCase.getUsersByName("John")).thenReturn(Flux.just(mockUser));

        // When & Then
        webTestClient.get()
                .uri("/users/search?name=John")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(User.class)
                .hasSize(1);
    }
}
