package co.com.nequi.sqs.listener;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;
import co.com.nequi.usecase.sqslistener.MessageListenerUseCase;
import java.util.function.Function;
import com.fasterxml.jackson.databind.ObjectMapper;
import co.com.nequi.model.user.User;

@Log4j2
@Service
@AllArgsConstructor
public class SQSProcessor implements Function<Message, Mono<Void>> {
    private final MessageListenerUseCase messageListenerUseCase;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> apply(Message message) {
        try {
            User user = objectMapper.readValue(message.body(), User.class);
            log.info("Usuario recibido: id={}, email={}, firstName={}, lastName={}", user.getId(), user.getEmail(), user.getFirstName(), user.getLastName());
            return messageListenerUseCase.processMessage(user)
                .doOnError(error -> log.error("Error processing message: {}", error.getMessage(), error))
                .then();
        } catch (Exception e) {
            log.error("Error parseando mensaje: {}", e.getMessage(), e);
            return Mono.error(e);
        }
    }
}
