package co.com.nequi.api;

import co.com.nequi.model.user.User;
import co.com.nequi.usecase.user.UserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserHandlerTest {

    @Mock
    private UserUseCase userUseCase;

    private UserHandler userHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userHandler = new UserHandler(userUseCase);
    }

    @Test
    void listenCreateUser_ShouldReturnCreatedUser_WhenValidId() {
        // Given
        String userId = "1";
        User mockUser = User.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();

        ServerRequest request = MockServerRequest.builder()
                .queryParam("id", userId)
                .build();

        when(userUseCase.createUserById(anyInt())).thenReturn(Mono.just(mockUser));

        // When
        Mono<ServerResponse> response = userHandler.listenCreateUser(request);

        // Then
        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void listenCreateUser_ShouldReturnBadRequest_WhenMissingId() {
        // Given
        ServerRequest request = MockServerRequest.builder().build();

        // When
        Mono<ServerResponse> response = userHandler.listenCreateUser(request);

        // Then
        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void listenCreateUser_ShouldReturnBadRequest_WhenInvalidId() {
        // Given
        ServerRequest request = MockServerRequest.builder()
                .queryParam("id", "invalid")
                .build();

        // When
        Mono<ServerResponse> response = userHandler.listenCreateUser(request);

        // Then
        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void listenGetUserById_ShouldReturnUser_WhenValidId() {
        // Given
        String userId = "1";
        User mockUser = User.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();

        ServerRequest request = MockServerRequest.builder()
                .pathVariable("id", userId)
                .build();

        when(userUseCase.getUserById(anyInt())).thenReturn(Mono.just(mockUser));

        // When
        Mono<ServerResponse> response = userHandler.listenGetUserById(request);

        // Then
        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void listenGetAllUsers_ShouldReturnUsersList() {
        // Given
        List<User> mockUsers = Arrays.asList(
                User.builder().id(1).firstName("John").lastName("Doe").email("john@example.com").build(),
                User.builder().id(2).firstName("Jane").lastName("Smith").email("jane@example.com").build()
        );

        ServerRequest request = MockServerRequest.builder().build();

        when(userUseCase.getAllUsers()).thenReturn(Flux.fromIterable(mockUsers));

        // When
        Mono<ServerResponse> response = userHandler.listenGetAllUsers(request);

        // Then
        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void listenGetUsersByName_ShouldReturnUsers_WhenValidName() {
        // Given
        String name = "John";
        List<User> mockUsers = Arrays.asList(
                User.builder().id(1).firstName("John").lastName("Doe").email("john@example.com").build()
        );

        ServerRequest request = MockServerRequest.builder()
                .queryParam("name", name)
                .build();

        when(userUseCase.getUsersByName(anyString())).thenReturn(Flux.fromIterable(mockUsers));

        // When
        Mono<ServerResponse> response = userHandler.listenGetUsersByName(request);

        // Then
        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void listenGetUsersByName_ShouldReturnBadRequest_WhenMissingName() {
        // Given
        ServerRequest request = MockServerRequest.builder().build();

        // When
        Mono<ServerResponse> response = userHandler.listenGetUsersByName(request);

        // Then
        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }
}
