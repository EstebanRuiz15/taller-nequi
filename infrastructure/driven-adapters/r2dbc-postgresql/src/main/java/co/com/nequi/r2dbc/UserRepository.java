package co.com.nequi.r2dbc;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import co.com.nequi.r2dbc.entity.UserEntity;
import reactor.core.publisher.Flux;

public interface UserRepository extends ReactiveCrudRepository<UserEntity, String>{
    Flux<UserEntity> findByFirstNameIgnoreCase(String firstName);
}
