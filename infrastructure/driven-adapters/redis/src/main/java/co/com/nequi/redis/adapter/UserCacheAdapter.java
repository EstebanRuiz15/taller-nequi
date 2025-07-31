package co.com.nequi.redis.adapter;

import co.com.nequi.model.user.User;
import co.com.nequi.model.user.gateways.UserCacheGateway;
import co.com.nequi.redis.exception.CacheException;
import co.com.nequi.redis.template.ReactiveRedisTemplateAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.time.Duration;
import java.util.List;

@Component
public class UserCacheAdapter implements UserCacheGateway {
    
    private final ReactiveRedisTemplateAdapter redisTemplate;
    private final ReactiveRedisTemplate<String, String> stringRedisTemplate;
    private final ObjectMapper objectMapper;
    
    public UserCacheAdapter(ReactiveRedisTemplateAdapter redisTemplate,
                           @Qualifier("reactiveRedisTemplate") ReactiveRedisTemplate<String, String> stringRedisTemplate,
                           ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }
    
    private static final String USER_CACHE_PREFIX = "user:";
    private static final String USER_NAME_CACHE_PREFIX = "user:name:";
    private static final long DEFAULT_EXPIRATION_MS = 300000; // 5 minutos
    
    @Override
    public Mono<User> getUserFromCache(Integer id) {
        String key = USER_CACHE_PREFIX + id;
        return redisTemplate.findById(key);
    }
    
    @Override
    public Mono<User> saveUserToCache(Integer id, User user) {
        String key = USER_CACHE_PREFIX + id;
        return redisTemplate.save(key, user, DEFAULT_EXPIRATION_MS);
    }

    
    @Override
    public Mono<List<User>> getUsersByNameFromCache(String name) {
        String key = USER_NAME_CACHE_PREFIX + name.toLowerCase();
        
        return stringRedisTemplate.opsForValue().get(key)
            .map(json -> {
                try {
                    return objectMapper.readValue(json, new TypeReference<List<User>>() {});
                } catch (Exception e) {
                    throw new CacheException("Error deserializando usuarios del cache", e, 500);
                }
            });
    }
    
    @Override
    public Mono<List<User>> saveUsersByNameToCache(String name, List<User> users) {
        String key = USER_NAME_CACHE_PREFIX + name.toLowerCase();

        return Mono.fromCallable(() -> {
                    try {
                        return objectMapper.writeValueAsString(users);
                    } catch (Exception e) {
                        throw new CacheException("Error serializando usuarios para cache", e, 500);
                    }
                })
                .flatMap(json ->
                        stringRedisTemplate.opsForValue().set(key, json)
                                .then(stringRedisTemplate.expire(key, Duration.ofMillis(DEFAULT_EXPIRATION_MS)))
                                .thenReturn(users)
                );
    }

}
