package co.com.nequi.dynamodb.adapter;

import co.com.nequi.model.user.User;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import co.com.nequi.dynamodb.template.DynamoDBTemplateAdapter;
import co.com.nequi.dynamodb.adapter.DynamoDBUserRepositoryAdapter;

@Component
public class DynamoDBUserRepositoryAdapter {
    private final DynamoDBTemplateAdapter dynamoDBTemplateAdapter;

    public DynamoDBUserRepositoryAdapter(DynamoDBTemplateAdapter dynamoDBTemplateAdapter) {
        this.dynamoDBTemplateAdapter = dynamoDBTemplateAdapter;
    }

    public Mono<Void> saveUser(User user) {
        return dynamoDBTemplateAdapter.save(user);
    }
}
