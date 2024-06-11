package at.aau.serg.websocketdemoapp.networking;

import android.util.Log;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.function.Consumer;

import at.aau.serg.websocketdemoapp.activities.LobbyRoom;
import at.aau.serg.websocketdemoapp.dto.DealRoundRequest;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class StompHandler {
    /**
     * localhost from the Android emulator is reachable as 10.0.2.2
     * <a href="https://developer.android.com/studio/run/emulator-networking">...</a>
     * "ws://10.0.2.2:8080/websocket-example-broker"
     */
    private final StompClient stompClient;
    private final Gson gson = new Gson();
    private static StompHandler instance;
    private static final String TAG_NETWORK = "Network";
    private static final String TAG_RECEIVED = "Received";
    private static final String actualServerUrl = "ws://unified-officially-snake.ngrok-free.app/websocket-example-broker";
    //private static final String actualServerUrl = "ws://10.0.2.2:8080/websocket-example-broker";

    public StompHandler() {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, actualServerUrl);
        connectToServer();
    }

    public static StompHandler getInstance() {
        if(instance == null) {
            instance = new StompHandler();
        }
        return instance;
    }

    public static void setInstance(StompHandler instance) {
        StompHandler.instance = instance;
    }

    private void setLifecycle() {
        stompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {

                case OPENED:
                    Log.d(TAG_NETWORK, "Stomp connection opened");
                    break;

                case ERROR:
                    Log.e(TAG_NETWORK, "Error", lifecycleEvent.getException());
                    break;

                case CLOSED:
                    Log.d(TAG_NETWORK, "Stomp connection closed");
                    break;
                    
                case FAILED_SERVER_HEARTBEAT:
                    Log.d(TAG_NETWORK, "Stomp server heartbeat");
                    break;
            }
        });
    }

    public void connectToServer() {
        if (stompClient != null) {
            try {
                stompClient.connect();
                setLifecycle();
            } catch (Exception e) {
                Log.e(TAG_NETWORK, e.getMessage());
            }
        }
    }

    public void createLobby(String userID, String userName, Consumer<String> lobbyCodeCallback) {
        HashMap<String, String> payload = new HashMap<>();
        payload.put("userID", userID);
        payload.put("userName", userName);
        String jsonPayload = gson.toJson(payload);

        stompClient.topic("/topic/lobby-created").subscribe(topicMessage -> {
            Log.d(TAG_RECEIVED, topicMessage.getPayload());
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
            Log.d(TAG_RECEIVED, topicMessage.getPayload());
            String data = extractData(topicMessage.getPayload());
            dataCallback.accept(data);
        });

        stompClient.send("/app/join_lobby", jsonPayload).subscribe();
    }

    public void dealNewRound(String lobbyCode, String userID, Consumer<String> dataCallback) {
        DealRoundRequest dealRoundRequest = new DealRoundRequest();
        dealRoundRequest.setLobbyCode(lobbyCode);
        dealRoundRequest.setUserID(userID);

        stompClient.topic("/topic/new-round-dealt").subscribe(topicMessage -> {
            Log.d(TAG_RECEIVED, topicMessage.getPayload());
            String data = extractData(topicMessage.getPayload());
            Log.d("DealRound", data);
            dataCallback.accept(data);
        });

        stompClient.send("/app/deal_new_round", gson.toJson(dealRoundRequest)).subscribe();
    }

    public void initGameStartSubscription(LobbyRoom roomActivity) {
        stompClient.topic("/topic/game_for_lobby_started").subscribe(topicMessage -> {
            String data = extractData(topicMessage.getPayload());

            roomActivity.changeToGameActivity();
        });
    }

    public void startGameForLobby(String lobbyCode) {
        HashMap<String, String> payload = new HashMap<>();
        payload.put("lobbyCode", lobbyCode);
        String jsonPayload = gson.toJson(payload);

        stompClient.send("/app/start_game_for_lobby", jsonPayload).subscribe();
    }

    public void subscribeForPlayerChangedEvent(Consumer<String> dataCallback) {
        stompClient.topic("/topic/active_player_changed").subscribe(topicMessage -> {
            String data = extractData(topicMessage.getPayload());
            dataCallback.accept(data);
        });
    }

    public void subscribeForPlayCard(Consumer<String> dataCallback) {
        stompClient.topic("/topic/card_played").subscribe(topicMessage ->{
           String data = extractData(topicMessage.getPayload());
           Log.d("PLAY CARD RESPONSE", data);
           dataCallback.accept(data);
        });
    }

    public void playCard(String jsonPayload) {
        stompClient.send("/app/play_card", jsonPayload).subscribe();
    }

    public void subscribeForPlayerJoinedLobbyEvent(Consumer<String> dataCallback) {
        stompClient.topic("/topic/player_joined_lobby").subscribe(topicMessage -> {
            String data = extractData(topicMessage.getPayload());
            dataCallback.accept(data);
        });
    }

    public void subscribeForPlayerWonTrickEvent(Consumer<String> dataCallback) {
        stompClient.topic("/topic/trick_won").subscribe(topicMessage -> {
            String data = extractData(topicMessage.getPayload());
            dataCallback.accept(data);
        });
    }

    public void helloMessage(String message) {
        stompClient.topic("/topic/hello-response").subscribe(topicMessage ->
                Log.d(TAG_RECEIVED, topicMessage.getPayload())
        );

        stompClient.send("/app/hello", message).subscribe();
    }

    public void disconnect() {
        if (stompClient != null) {
            try {
                stompClient.disconnect();
            } catch (Exception e) {
                Log.e(TAG_NETWORK, e.getMessage());
            }
        }
    }
}
