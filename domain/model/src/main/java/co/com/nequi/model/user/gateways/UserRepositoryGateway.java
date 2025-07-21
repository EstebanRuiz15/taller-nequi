package co.com.nequi.model.user.gateways;

import reactor.core.publisher.Mono;
import co.com.nequi.model.user.User;
import reactor.core.publisher.Flux;

public interface UserRepositoryGateway {
    Mono<User> createUserById(User user);
    Mono<User> getUserById(Integer id);
    Flux<User> getAllUsers();
    Flux<User> getUsersByName(String name);
}
