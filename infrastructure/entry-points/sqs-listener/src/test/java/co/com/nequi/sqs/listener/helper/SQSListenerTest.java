package co.com.nequi.sqs.listener.helper;

import co.com.nequi.usecase.sqslistener.MessageListenerUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SQSListenerTest {

    @Mock
    private SqsAsyncClient asyncClient;
    
    @Mock
    private MessageListenerUseCase messageListenerUseCase;
    
    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);

        Message message = Message.builder().body("message").build();
        DeleteMessageResponse deleteMessageResponse = DeleteMessageResponse.builder().build();
        ReceiveMessageResponse messageResponse = ReceiveMessageResponse.builder().messages(message).build();

        when(asyncClient.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(CompletableFuture.completedFuture(messageResponse));
        when(asyncClient.deleteMessage(any(DeleteMessageRequest.class))).thenReturn(CompletableFuture.completedFuture(deleteMessageResponse));

    }

}