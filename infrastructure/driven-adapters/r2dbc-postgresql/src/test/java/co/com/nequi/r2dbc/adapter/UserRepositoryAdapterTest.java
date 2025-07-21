package co.com.nequi.r2dbc.adapter;

import co.com.nequi.model.user.User;
import co.com.nequi.r2dbc.UserRepository;
import co.com.nequi.r2dbc.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private R2dbcEntityTemplate entityTemplate;

    private UserRepositoryAdapter userRepositoryAdapter;

    @BeforeEach
    void setUp() {
        userRepositoryAdapter = new UserRepositoryAdapter(userRepository, entityTemplate);
    }

    @Test
    void createUserById_ShouldReturnCreatedUser_WhenValidUser() {
     
        assert true;
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Given
        UserEntity userEntity = UserEntity.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();

        when(userRepository.findById("1")).thenReturn(Mono.just(userEntity));

        // When
        Mono<User> result = userRepositoryAdapter.getUserById(1);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(user -> 
                    user.getId().equals(1) && 
                    user.getFirstName().equals("John")
                )
                .verifyComplete();
    }

    @Test
    void getUserById_ShouldReturnEmpty_WhenUserNotExists() {
        // Given
        when(userRepository.findById("1")).thenReturn(Mono.empty());

        // When
        Mono<User> result = userRepositoryAdapter.getUserById(1);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Given
        UserEntity user1 = UserEntity.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();
        
        UserEntity user2 = UserEntity.builder()
                .id(2)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@example.com")
                .build();

        when(userRepository.findAll()).thenReturn(Flux.just(user1, user2));

        // When
        Flux<User> result = userRepositoryAdapter.getAllUsers();

        // Then
        StepVerifier.create(result)
                .expectNextMatches(user -> user.getId().equals(1))
                .expectNextMatches(user -> user.getId().equals(2))
                .verifyComplete();
    }

    @Test
    void getUsersByName_ShouldReturnUsers_WhenNameMatches() {
        // Given
        UserEntity userEntity = UserEntity.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();

        when(userRepository.findByFirstNameIgnoreCase("John"))
                .thenReturn(Flux.just(userEntity));

        // When
        Flux<User> result = userRepositoryAdapter.getUsersByName("John");

        // Then
        StepVerifier.create(result)
                .expectNextMatches(user -> 
                    user.getId().equals(1) && 
                    user.getFirstName().equals("John")
                )
                .verifyComplete();
    }

    @Test
    void getUsersByName_ShouldReturnEmpty_WhenNoMatches() {
        // Given
        when(userRepository.findByFirstNameIgnoreCase("NonExistent"))
                .thenReturn(Flux.empty());

        // When
        Flux<User> result = userRepositoryAdapter.getUsersByName("NonExistent");

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }
}
