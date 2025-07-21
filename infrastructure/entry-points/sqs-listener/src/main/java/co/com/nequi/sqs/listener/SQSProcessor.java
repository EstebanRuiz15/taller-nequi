package co.com.nequi.sqs.listener;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;
import co.com.nequi.usecase.sqslistener.MessageListenerUseCase;
import java.util.function.Function;
import com.fasterxml.jackson.databind.ObjectMapper;
import co.com.nequi.model.user.User;

@Service
@AllArgsConstructor
public class SQSProcessor implements Function<Message, Mono<Void>> {
    private final MessageListenerUseCase messageListenerUseCase;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> apply(Message message) {
        try {
            User user = objectMapper.readValue(message.body(), User.class);
            System.out.println("Usuario recibido: id=" + user.getId() + ", email=" + user.getEmail() + ", firstName=" + user.getFirstName() + ", lastName=" + user.getLastName());
            return messageListenerUseCase.processMessage(user)
                .doOnError(error -> System.err.println("Error processing message: " + error.getMessage()))
                .then();
        } catch (Exception e) {
            System.err.println("Error parseando mensaje: " + e.getMessage());
            return Mono.error(e);
        }
    }
}
