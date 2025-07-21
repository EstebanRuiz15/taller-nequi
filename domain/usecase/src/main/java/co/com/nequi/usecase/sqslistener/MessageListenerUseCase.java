
package co.com.nequi.usecase.sqslistener;

import co.com.nequi.model.user.User;
import co.com.nequi.model.user.gateways.UserNoSqlGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class MessageListenerUseCase {
    private final UserNoSqlGateway userDynamoGateway;

    public Mono<Void> processMessage(User user) {
        User upperUser = User.builder()
            .id(user.getId())
            .firstName(user.getFirstName() != null ? user.getFirstName().toUpperCase() : null)
            .lastName(user.getLastName() != null ? user.getLastName().toUpperCase() : null)
            .email(user.getEmail() != null ? user.getEmail().toUpperCase() : null)
            .build();
        return userDynamoGateway.saveUser(upperUser);
    }
}
