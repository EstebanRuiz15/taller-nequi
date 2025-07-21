package co.com.nequi.dynamodb.helper;

import co.com.nequi.dynamodb.entity.ModelEntity;
import co.com.nequi.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivecommons.utils.ObjectMapper;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class TemplateAdapterOperationsTest {

    @Mock
    private DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private DynamoDbAsyncTable<ModelEntity> customerTable;

    private ModelEntity modelEntity;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(dynamoDbEnhancedAsyncClient.table("table_name", TableSchema.fromBean(ModelEntity.class)))
                .thenReturn(customerTable);

        modelEntity = new ModelEntity();
        modelEntity.setId(1);
        modelEntity.setFirstName("John");
        modelEntity.setLastName("Doe");
        modelEntity.setEmail("john.doe@example.com");

        testUser = User.builder()
            .id(1)
            .firstName("John")
            .lastName("Doe")
            .email("john.doe@example.com")
            .build();
    }

    @Test
    void modelEntityPropertiesMustNotBeNull() {
        ModelEntity modelEntityUnderTest = new ModelEntity(1, "John", "Doe", "john.doe@example.com");

        assertNotNull(modelEntityUnderTest.getId());
        assertNotNull(modelEntityUnderTest.getFirstName());
        assertNotNull(modelEntityUnderTest.getLastName());
        assertNotNull(modelEntityUnderTest.getEmail());
    }

}