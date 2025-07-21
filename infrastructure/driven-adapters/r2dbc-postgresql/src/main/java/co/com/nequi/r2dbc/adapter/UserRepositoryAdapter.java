package co.com.nequi.r2dbc.adapter;

import co.com.nequi.r2dbc.UserRepository;
import co.com.nequi.model.user.gateways.UserRepositoryGateway;
import co.com.nequi.model.user.User;
import co.com.nequi.r2dbc.entity.UserEntity;
import co.com.nequi.r2dbc.mapper.UserEntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Component
public class UserRepositoryAdapter implements UserRepositoryGateway {
    private static final Logger log = LogManager.getLogger(UserRepositoryAdapter.class);
    private final UserRepository userRepository;
    private final R2dbcEntityTemplate entityTemplate;

    @Autowired
    public UserRepositoryAdapter(UserRepository userRepository, R2dbcEntityTemplate entityTemplate) {
        this.userRepository = userRepository;
        this.entityTemplate = entityTemplate;
    }

    @Override
    public Mono<User> createUserById(User user) {
        log.info("Creating user: {}", user);
        UserEntity entity = UserEntityMapper.toEntity(user);
        return entityTemplate.insert(UserEntity.class)
            .using(entity)
            .doOnError(e -> log.error("Error creating user: {}", user, e))
            .map(UserEntityMapper::toModel);
    }

    @Override
    public Mono<User> getUserById(Integer id) {
        return userRepository.findById(id.toString())
                .map(UserEntityMapper::toModel);
    }

    @Override
    public Flux<User> getAllUsers() {
        return userRepository.findAll()
                .map(UserEntityMapper::toModel);
    }

    @Override
    public Flux<User> getUsersByName(String name) {
        return userRepository.findByFirstNameIgnoreCase(name)
                .map(UserEntityMapper::toModel);
    }
}
