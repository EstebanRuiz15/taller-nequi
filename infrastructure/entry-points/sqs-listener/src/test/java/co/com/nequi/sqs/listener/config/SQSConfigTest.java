package co.com.nequi.sqs.listener.config;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

public class SQSConfigTest {

    @Mock
    private SqsAsyncClient asyncClient;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

}