package co.com.nequi.usecase.user;

import co.com.nequi.model.user.gateways.UserRepositoryGateway;
import co.com.nequi.model.user.gateways.UserEventGateway;
import co.com.nequi.model.user.gateways.UserWebClientGateway;
import co.com.nequi.model.user.gateways.UserCacheGateway;
import co.com.nequi.exception.BusinessException;
import co.com.nequi.exception.TechnicalException;
import co.com.nequi.enums.TechnicalMessage;
import lombok.RequiredArgsConstructor;
import co.com.nequi.model.user.User;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepositoryGateway userRepositoryGateway;
    private final UserWebClientGateway userWebClientGateway;
    private final UserEventGateway userSQSEventGateway;
    private final UserCacheGateway userCacheGateway;

    public Mono<User> createUserById(Integer id) {
        return userRepositoryGateway.getUserById(id)
            .flatMap(existingUser -> Mono.<User>error(new BusinessException("El usuario ya existe", TechnicalMessage.GENERIC_ERROR)))
            .switchIfEmpty(
                userWebClientGateway.getUserById(id)
                    .flatMap(user ->
                        userRepositoryGateway.createUserById(user)
                            .flatMap(createdUser ->
                                userSQSEventGateway.sendUserCreatedEvent(createdUser)
                                    .thenReturn(createdUser)
                            )
                    )
            );
    }

    public Mono<User> getUserById(Integer id) {
        System.out.println("🔍 [CACHE] Consultando usuario con ID: " + id);
        // Intentar obtener del cache primero
        return userCacheGateway.getUserFromCache(id)
            .doOnNext(user -> System.out.println("✅ [CACHE] Usuario encontrado en cache: " + user.getFirstName() + " " + user.getLastName()))
            .switchIfEmpty(
                // Si no está en cache, consultar base de datos
                Mono.fromRunnable(() -> System.out.println("❌ [CACHE] Usuario no encontrado en cache, consultando BD..."))
                    .then(userRepositoryGateway.getUserById(id))
                    .doOnNext(user -> System.out.println("📊 [DATABASE] Usuario obtenido de BD: " + user.getFirstName() + " " + user.getLastName()))
                    .flatMap(user -> {
                        // Solo guardar en cache si el usuario existe
                            return userCacheGateway.saveUserToCache(id, user)
                                .doOnSuccess(v -> System.out.println("💾 [CACHE SAVE] Usuario guardado en cache con TTL de 5 minutos"))
                                .thenReturn(user);
                    })
                    .switchIfEmpty(
                        Mono.fromRunnable(() -> System.out.println("❌ [DATABASE] Usuario no encontrado en BD"))
                            .then(Mono.empty())
                    )
            )
            .switchIfEmpty(Mono.error(new BusinessException("El usuario no existe", TechnicalMessage.USER_NOT_FOUND)));
    }

    public Flux<User> getAllUsers() {
        return userRepositoryGateway.getAllUsers();
    }

    public Flux<User> getUsersByName(String name) {
        System.out.println("🔍 [CACHE] Consultando usuarios con nombre: " + name);
        //Intentar obtener del cache primero
        return userCacheGateway.getUsersByNameFromCache(name)
            .flatMapMany(cachedUsers -> {
                    // Si hay usuarios en cache, retornarlos
                    System.out.println("✅ [CACHE] " + cachedUsers.size() + " usuarios encontrados en cache para nombre: " + name);
                    return Flux.fromIterable(cachedUsers);
            })
            .switchIfEmpty(
                //Si no está en cache, consultar base de datos
                userRepositoryGateway.getUsersByName(name)
                    .doFirst(() -> System.out.println("❌ [CACHE MISS] Usuarios no encontrados en cache, consultando BD..."))
                    .collectList()
                    .doOnNext(users -> System.out.println("📊 [DATABASE] " + users.size() + " usuarios obtenidos de BD para nombre: " + name))
                    .flatMapMany(users -> {
                            return userCacheGateway.saveUsersByNameToCache(name, users)
                                .doOnSuccess(v -> System.out.println("💾 [CACHE SAVE] " + users.size() + " usuarios guardados en cache con ttl de 5 minutos"))
                                .thenMany(Flux.fromIterable(users));
                    })
            )
            .switchIfEmpty(Flux.error(new BusinessException("No se encontraron usuarios con ese nombre", TechnicalMessage.USER_NOT_FOUND)));
    }
}
