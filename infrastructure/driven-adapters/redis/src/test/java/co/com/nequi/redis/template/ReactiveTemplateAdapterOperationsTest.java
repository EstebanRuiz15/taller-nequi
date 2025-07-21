package co.com.nequi.redis.template;

import co.com.nequi.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

class ReactiveRedisTemplateAdapterOperationsTest {

    @Mock
    private ReactiveRedisConnectionFactory connectionFactory;

    @Mock
    private ObjectMapper objectMapper;

    private ReactiveRedisTemplateAdapter adapter;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = User.builder()
            .id(1)
            .firstName("John")
            .lastName("Doe")
            .email("john.doe@example.com")
            .build();

        when(objectMapper.map(testUser, User.class)).thenReturn(testUser);

        adapter = new ReactiveRedisTemplateAdapter(connectionFactory, objectMapper);
    }

    @Test
    void testSave() {
        StepVerifier.create(adapter.save("key", testUser))
                .expectNext(testUser)
                .verifyComplete();
    }

    @Test
    void testSaveWithExpiration() {

        StepVerifier.create(adapter.save("key", testUser, 2))
                .expectNext(testUser)
                .verifyComplete();
    }

    @Test
    void testFindById() {

        StepVerifier.create(adapter.findById("key"))
                .verifyComplete();
    }

}