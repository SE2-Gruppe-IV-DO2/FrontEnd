package at.aau.serg.websocketdemoapp.networking;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketClient {

    // TODO use correct hostname:port
    /**
     * localhost from the Android emulator is reachable as 10.0.2.2
     * https://developer.android.com/studio/run/emulator-networking
     * "ws://10.0.2.2:8080/websocket-example-handler"
     */
    private final String WEBSOCKET_URI;
    private static final String TAG = "WebSocket";
    private WebSocket webSocket;

    public WebSocketClient(String ip) {

        WEBSOCKET_URI = ip;
    }

    public void connectToServer(WebSocketMessageHandler<String> messageHandler) {
        if (messageHandler == null)
            throw new IllegalArgumentException("messageHandler is required");

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(WEBSOCKET_URI)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                Log.d("Network", "connected");
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                messageHandler.onMessageReceived(text);
            }

            @Override
            public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                // connection closed
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
                // Permission needed to transmit cleartext in the manifest
                // https://stackoverflow.com/questions/45940861/android-8-cleartext-http-traffic-not-permitted
                Log.d("Network", "connection failure");
            }
        });
    }

    public void createLobby(String userID, String userName) {
        if (webSocket != null) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userID", userID);
                jsonObject.put("userName", userName);

                JSONObject requestObject = new JSONObject();
                requestObject.put("command", "create_new_lobby");
                requestObject.put("message", jsonObject);

                webSocket.send(requestObject.toString());
            } catch (Exception e) {
                Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            }
        }
    }

    public void joinLobby(String userID, String userName, String lobbyCode) {
        if (webSocket != null) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userID", userID);
                jsonObject.put("userName", userName);
                jsonObject.put("lobbyCode", lobbyCode);

                JSONObject requestObject = new JSONObject();
                requestObject.put("command", "join_lobby");
                requestObject.put("message", jsonObject);

                webSocket.send(requestObject.toString());
            } catch (Exception e) {
                Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            }
        }
    }

/*
    public void startGame(String lobbyCode) {
        if (webSocket != null) {
        try {

        } catch(Exception e) {
            Log.e(TAG, e.getMessage());
        }
        }
    }
 */

    public void sendMessageToServer(String msg) throws NullPointerException {
        if(webSocket != null) {
            webSocket.send(msg);
        } else {
            throw new NullPointerException();
        }
    }

    public void disconnect() throws Throwable {
        try {
            webSocket.close(1000, "Closing");
        } catch (Exception e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
    }

    public void setWebSocket(WebSocket webSocket) {
        this.webSocket = webSocket;
    }
}
