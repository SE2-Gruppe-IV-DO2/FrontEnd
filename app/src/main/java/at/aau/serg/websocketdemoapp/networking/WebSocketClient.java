package at.aau.serg.websocketdemoapp.networking;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketClient {

    // TODO use correct hostname:port
    /**
     * localhost from the Android emulator is reachable as 10.0.2.2
     * https://developer.android.com/studio/run/emulator-networking
     */
    private final String WEBSOCKET_URI = "ws://10.0.2.2:8080/websocket-example-handler";

    private WebSocket webSocket;

    public void connectToServer(WebSocketMessageHandler<String> messageHandler) {
        if (messageHandler == null)
            throw new IllegalArgumentException("messageHandler is required");

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(WEBSOCKET_URI)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            /* Derweil noch nicht brauchbar -> wenn Schnittstelle steht

            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d("Network", "connected");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                messageHandler.onMessageReceived(text);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                // connection closed
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                // Permission needed to transmit cleartext in the manifest
                // https://stackoverflow.com/questions/45940861/android-8-cleartext-http-traffic-not-permitted
                Log.d("Network", "connection failure");
            }
            */

        });
    }

    public void sendMessageToServer(String msg) throws NullPointerException {
        if(webSocket != null) {
            webSocket.send(msg);
        } else {
            throw new NullPointerException();
        }

    }

    @Override
    protected void finalize() throws Throwable {
        try {
            webSocket.close(1000, "Closing");
        } finally {
            super.finalize();
        }
    }

    public void setWebSocket(WebSocket webSocket) {
        this.webSocket = webSocket;
    }
}
