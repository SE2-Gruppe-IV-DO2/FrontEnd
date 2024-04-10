package at.aau.serg.websocketdemoapp.networking;

import android.util.Log;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.function.Consumer;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class StompHandler {

    // TODO use correct hostname:port
    /**
     * localhost from the Android emulator is reachable as 10.0.2.2
     * https://developer.android.com/studio/run/emulator-networking
     * "ws://10.0.2.2:8080/websocket-example-broker"
     */
    private StompClient stompClient;
    private Gson gson = new Gson();

    public StompHandler(String ip) {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, ip);
    }


    private void setLifecycle() {
        stompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {

                case OPENED:
                    Log.d("Network", "Stomp connection opened");
                    break;

                case ERROR:
                    Log.e("Network", "Error", lifecycleEvent.getException());
                    break;

                case CLOSED:
                    Log.d("Network", "Stomp connection closed");
                    break;
            }
        });
    }


    public void connectToServer() {
        if (stompClient != null) {
            try {
                stompClient.connect();
                setLifecycle();
                Log.d("Network", "Connected");
            } catch (Exception e) {
                Log.e("Network", e.getMessage());
            }
        }
    }

    public void createLobby(String userID, String userName, Consumer<String> lobbyCodeCallback) {
        HashMap<String, String> payload = new HashMap<>();
        payload.put("userID", userID);
        payload.put("userName", userName);
        String jsonPayload = gson.toJson(payload);

        stompClient.topic("/topic/lobby-created").subscribe(topicMessage -> {
            Log.d("Received", topicMessage.getPayload());
            String lobbyCode = extractData(topicMessage.getPayload());
            lobbyCodeCallback.accept(lobbyCode);
        });

        stompClient.send("/app/create_new_lobby", jsonPayload).subscribe();
    }

    private String extractData(String message) {
        return message;
    }

    public void joinLobby(String lobbyCode, String userID, String userName, Consumer<String> dataCallback) {
        HashMap<String, String> payload = new HashMap<>();
        payload.put("lobbyCode", lobbyCode);
        payload.put("userID", userID);
        payload.put("userName", userName);
        String jsonPayload = gson.toJson(payload);

        stompClient.topic("/topic/lobby-joined").subscribe(topicMessage -> {
            Log.d("Received", topicMessage.getPayload());
            String data = extractData(topicMessage.getPayload());
            dataCallback.accept(data);
        });

        stompClient.send("/app/join_lobby", jsonPayload).subscribe();
    }

    public void helloMessage(String message) {
        stompClient.topic("/topic/hello-response").subscribe(topicMessage -> {
            Log.d("Received", topicMessage.getPayload());
        });

        stompClient.send("/app/hello", message).subscribe();
    }

    public void disconnect() {
        if (stompClient != null) {
            try {
                stompClient.disconnect();
            } catch (Exception e) {
                Log.e("Network", e.getMessage());
            }
        }
    }
}
