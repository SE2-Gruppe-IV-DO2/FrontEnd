package at.aau.serg.websocketdemoapp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import at.aau.serg.websocketdemoapp.networking.WebSocketClient;
import at.aau.serg.websocketdemoapp.networking.WebSocketMessageHandler;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
class WebSocketClientTest {
    private WebSocketClient wClient;
    @Mock
    WebSocket webSocket;
    @Mock
    OkHttpClient okHttpClient;
    @Mock
    WebSocketMessageHandler<String> webSocketMessageHandler;
    @Mock
    Response response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        wClient = new WebSocketClient();
        wClient.setWebSocket(webSocket);
    }

    @AfterEach
    void tearDown() {
        wClient = null;
    }

    @Test
    void testConnectToServerFailure() {
        Assert.assertThrows(IllegalArgumentException.class, () -> wClient.connectToServer(null));
    }

    @Test
    void testConnectionToServerSuccess() {
        doAnswer(invocation -> {
            WebSocketListener listener = invocation.getArgument(1);
            listener.onOpen(webSocket, response);
            return webSocket;
        }).when(okHttpClient).newWebSocket(any(Request.class), any(WebSocketListener.class));

        wClient.connectToServer(webSocketMessageHandler);

        verify(webSocketMessageHandler, times(0)).onMessageReceived(anyString());
    }

    @Test
    void testSendMessageSuccess() {
        wClient.sendMessageToServer("Test");
        verify(webSocket).send("Test");
    }

    @Test
    void testSendMessageFailure() {
        wClient.setWebSocket(null);
        Assert.assertThrows(NullPointerException.class, () -> wClient.sendMessageToServer("Test"));
    }
}