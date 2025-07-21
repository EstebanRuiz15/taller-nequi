package co.com.nequi.config;

import co.com.nequi.model.user.User;
import co.com.nequi.model.user.gateways.UserRepositoryGateway;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfiguration
@ComponentScan(
    basePackages = {"co.com.nequi.web", "co.com.nequi.usecase", "co.com.nequi.r2dbc"}, 
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "co\\.com\\.nequi\\.dynamodb\\..*"),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "co\\.com\\.nequi\\.sqs\\..*"),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "co\\.com\\.nequi\\.redis\\..*"),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "co\\.com\\.nequi\\.web\\.client\\..*")
    }
)
public class TestConfig {

    @Bean
    @Primary
    public UserRepositoryGateway userRepositoryGateway() {
        UserRepositoryGateway mock = mock(UserRepositoryGateway.class);
        
        // Mock responses for the test
        User mockUser = User.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();
        
        when(mock.createUserById(any(User.class))).thenReturn(Mono.just(mockUser));
        when(mock.getUserById(anyInt())).thenReturn(Mono.just(mockUser));
        when(mock.getAllUsers()).thenReturn(Flux.just(mockUser));
        when(mock.getUsersByName(anyString())).thenReturn(Flux.just(mockUser));
        
        return mock;
    }
}
