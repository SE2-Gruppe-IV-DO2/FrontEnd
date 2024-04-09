package at.aau.serg.websocketdemoapp.networking;

import android.util.Log;

public class MessageHandler implements WebSocketMessageHandler<String> {
    private String receivedMessage;

    @Override
    public void onMessageReceived(String message) {
        Log.d("Websocket", message);
        // Handle the received message
        this.receivedMessage = message;
    }

    @Override
    public String getValue() {
        Log.d("Message", receivedMessage);
        return receivedMessage;
    }
}
