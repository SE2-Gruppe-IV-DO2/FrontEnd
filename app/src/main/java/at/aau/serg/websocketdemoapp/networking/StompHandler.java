package at.aau.serg.websocketdemoapp.networking;

import android.util.Log;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.function.Consumer;

import at.aau.serg.websocketdemoapp.dto.CheatingAccusationRequest;
import at.aau.serg.websocketdemoapp.dto.DealRoundRequest;
import at.aau.serg.websocketdemoapp.dto.GetPlayerNamesRequest;
import at.aau.serg.websocketdemoapp.dto.GetPlayersInLobbyRequest;
import at.aau.serg.websocketdemoapp.dto.PointsRequest;
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
    //private static final String actualServerUrl = "ws://unified-officially-snake.ngrok-free.app/websocket-example-broker";
    private static final String actualServerUrl = "ws://10.0.2.2:8080/websocket-example-broker";

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

        stompClient.topic("/topic/lobby-created/" + userID).subscribe(topicMessage -> {
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

        stompClient.topic("/topic/lobby-joined/" + lobbyCode + "/" + userID).subscribe(topicMessage -> {
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

        stompClient.topic("/topic/new-round-dealt/" + lobbyCode + "/" + userID).subscribe(topicMessage -> {
            Log.d(TAG_RECEIVED, topicMessage.getPayload());
            String data = extractData(topicMessage.getPayload());
            dataCallback.accept(data);
        });

        stompClient.send("/app/deal_new_round", gson.toJson(dealRoundRequest)).subscribe();
    }

    public void getPlayersInLobbyMessage(String lobbyCode, Consumer<String> dataCallback) {
        stompClient.topic("/topic/players_in_lobby/" + lobbyCode).subscribe(topicMessage -> {
            String data = extractData(topicMessage.getPayload());
            dataCallback.accept(data);
        });

        GetPlayersInLobbyRequest playersInLobbyRequest = new GetPlayersInLobbyRequest();
        playersInLobbyRequest.setLobbyCode(lobbyCode);
        stompClient.send("/app/get_players_in_lobby", gson.toJson(playersInLobbyRequest)).subscribe();
    }

    public void initGameStartSubscription(String lobbyCode, Consumer<String> dataCallback) {
        stompClient.topic("/topic/game_for_lobby_started/" + lobbyCode).subscribe(topicMessage -> {
            String data = extractData(topicMessage.getPayload());
            dataCallback.accept(data);
        });
    }

    public void startGameForLobby(String lobbyCode) {
        HashMap<String, String> payload = new HashMap<>();
        payload.put("lobbyCode", lobbyCode);
        String jsonPayload = gson.toJson(payload);

        stompClient.send("/app/start_game_for_lobby", jsonPayload).subscribe();
    }

    public void subscribeForPlayerChangedEvent(String lobbyCode, Consumer<String> dataCallback) {
        stompClient.topic("/topic/active_player_changed/" + lobbyCode).subscribe(topicMessage -> {
            String data = extractData(topicMessage.getPayload());
            dataCallback.accept(data);
        });
    }

    public void subscribeForPlayCard(String lobbyCode, Consumer<String> dataCallback) {
        stompClient.topic("/topic/card_played/" + lobbyCode).subscribe(topicMessage ->{
           String data = extractData(topicMessage.getPayload());
           Log.d("PLAY CARD RESPONSE", data);
           dataCallback.accept(data);
        });
    }

    public void playCard(String jsonPayload) {
        stompClient.send("/app/play_card", jsonPayload).subscribe();
    }

    public void subscribeForPlayerJoinedLobbyEvent(String lobbyCode, Consumer<String> dataCallback) {
        stompClient.topic("/topic/player_joined_lobby/" + lobbyCode).subscribe(topicMessage -> {
            String data = extractData(topicMessage.getPayload());
            dataCallback.accept(data);
        });
    }

    public void getPoints (String lobbyCode, Consumer<String> dataCallback) {
        PointsRequest pointsRequest = new PointsRequest();
        pointsRequest.setLobbyCode(lobbyCode);

        stompClient.topic("/topic/points/" + lobbyCode).subscribe( topicMessage -> {
           String data = extractData(topicMessage.getPayload());
           dataCallback.accept(data);
        });

        String jsonPayload = gson.toJson(pointsRequest);
        stompClient.send("/app/get_points", jsonPayload).subscribe();
    }

    public void subscribeToRoundEndEvent(String lobbyCode, Consumer<String> dataCallback) {
        stompClient.topic("/topic/round_ended/" + lobbyCode).subscribe(topicMessage -> {
           String data = extractData(topicMessage.getPayload());
           dataCallback.accept(data);
        });
    }

    public void sendCheatingAccusation(String lobbyCode, String playerId, String accusedPlayerId, Consumer<String> dataCallback) {
        CheatingAccusationRequest cheatingAccusationRequest = new CheatingAccusationRequest();
        cheatingAccusationRequest.setLobbyCode(lobbyCode);
        cheatingAccusationRequest.setUserID(playerId);
        cheatingAccusationRequest.setAccusedUserId(accusedPlayerId);

        stompClient.topic("/topic/accusation_result").subscribe(topicMessage -> {
            String data = extractData(topicMessage.getPayload());
            dataCallback.accept(data);
        });

        String jsonPayLoad = gson.toJson(cheatingAccusationRequest);
        stompClient.send("/app/accuse_player_of_cheating", jsonPayLoad).subscribe();
    }

    public void subscribeForPlayerWonTrickEvent(String lobbyCode, Consumer<String> dataCallback) {
        stompClient.topic("/topic/trick_won/" + lobbyCode).subscribe(topicMessage -> {
            String data = extractData(topicMessage.getPayload());
            dataCallback.accept(data);
        });
    }

    public void getPlayerTricks(String lobbyCode, Consumer<String> dataCallback) {
        stompClient.topic("/topic/player_tricks/" + lobbyCode).subscribe(topicMessage -> {
            String data = extractData(topicMessage.getPayload());
            dataCallback.accept(data);
        });

        stompClient.send("/app/get-player-tricks", lobbyCode).subscribe();
    }

    public void getPlayerNames(String lobbyCode, Consumer<String> dataCallback) {
        stompClient.topic("/topic/player_names/" + lobbyCode).subscribe(topicMessage -> {
            String data = extractData(topicMessage.getPayload());
            dataCallback.accept(data);
        });

        GetPlayerNamesRequest getPlayerNamesRequest = new GetPlayerNamesRequest();
        getPlayerNamesRequest.setLobbyCode(lobbyCode);

        String jsonPayload = gson.toJson(getPlayerNamesRequest);

        stompClient.send("/app/get-player-names", jsonPayload).subscribe();
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
