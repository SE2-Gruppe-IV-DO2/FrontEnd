package at.aau.serg.websocketdemoapp;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.util.Log;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import at.aau.serg.websocketdemoapp.networking.WebSocketClient;
import at.aau.serg.websocketdemoapp.networking.WebSocketMessageHandler;
import okhttp3.Call;
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
    @Mock
    Log log;
    @Mock
    Call call;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        wClient = new WebSocketClient("ws://10.0.2.2:8080/websocket-example-handler");
        wClient.setWebSocket(webSocket);
    }

    @AfterEach
    void tearDown() {
        wClient = null;
    }

    @Test
    void testConnectToServerFailure() {
        assertThrows(IllegalArgumentException.class, () -> wClient.connectToServer(null));
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
        assertThrows(NullPointerException.class, () -> wClient.sendMessageToServer("Test"));
    }
/*
    @Test
    void testCreateLobby() {
        String userID = "123";
        String userName = "John";



        wClient.createLobby(userID, userName);

        verify(webSocket).send(anyString());
    }

    @Test
    void testJoinLobby() {
        String userID = "456";
        String userName = "Jane";
        String lobbyCode = "ABC123";

        wClient.joinLobby(userID, userName, lobbyCode);

        verify(webSocket).send(anyString());
    }
*/
    @Test
    void testSendMessageToServerSuccess() {
        String message = "Hello Server";

        wClient.sendMessageToServer(message);

        verify(webSocket).send(eq(message));
    }

    @Test
    void testSendMessageToServerFailure() {
        wClient.setWebSocket(null);

        assertThrows(NullPointerException.class, () -> wClient.sendMessageToServer("Test"));
    }

    @Test
    void testDisconnect() {
        Throwable throwable = mock(Throwable.class);

        assertDoesNotThrow(() -> wClient.disconnect());
    }
}