package co.com.nequi.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(UserHandler handler) {
        return route(POST("/users"), handler::listenCreateUser)
                .andRoute(GET("/users/search"), handler::listenGetUsersByName)
                .andRoute(GET("/users/{id}"), handler::listenGetUserById)
                .andRoute(GET("/users"), handler::listenGetAllUsers);
    }
}
