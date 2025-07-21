package co.com.nequi.sqs.sender;

import co.com.nequi.model.user.User;
import co.com.nequi.model.user.gateways.UserEventGateway;
import co.com.nequi.sqs.exception.SQSException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class SQSSenderAdapter implements UserEventGateway {
    private final SQSSender sqsSender;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> sendUserCreatedEvent(User user) {
        try {
            String message = objectMapper.writeValueAsString(user);
            return sqsSender.send(message).then();
        } catch (Exception e) {
            return Mono.error(new SQSException("Error enviando mensaje a SQS", e, 500));
        }
    }
}
