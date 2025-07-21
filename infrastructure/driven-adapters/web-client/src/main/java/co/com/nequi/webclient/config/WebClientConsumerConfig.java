package co.com.nequi.webclient.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConsumerConfig{
  @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://reqres.in/api/users")
                .defaultHeader("x-api-key", "reqres-free-v1")
                .build();
    }
}
