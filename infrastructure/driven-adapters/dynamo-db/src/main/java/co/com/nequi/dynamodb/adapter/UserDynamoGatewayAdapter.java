package co.com.nequi.dynamodb.adapter;

import co.com.nequi.model.user.User;
import co.com.nequi.model.user.gateways.UserNoSqlGateway;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UserDynamoGatewayAdapter implements UserNoSqlGateway {
    private final DynamoDBUserRepositoryAdapter dynamoDBUserRepositoryAdapter;

    public UserDynamoGatewayAdapter(DynamoDBUserRepositoryAdapter dynamoDBUserRepositoryAdapter) {
        this.dynamoDBUserRepositoryAdapter = dynamoDBUserRepositoryAdapter;
    }

    @Override
    public Mono<Void> saveUser(User user) {
        return dynamoDBUserRepositoryAdapter.saveUser(user);
    }
}
