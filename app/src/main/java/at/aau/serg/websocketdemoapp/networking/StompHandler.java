package at.aau.serg.websocketdemoapp.networking;

import android.util.Log;

import com.google.gson.Gson;

import java.util.HashMap;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class StompHandler {

    // TODO use correct hostname:port
    /**
     * localhost from the Android emulator is reachable as 10.0.2.2
     * https://developer.android.com/studio/run/emulator-networking
     * "ws://10.0.2.2:8080/websocket-example-handler"
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

    public void createLobby(String userID, String userName) {
        HashMap<String, String> payload = new HashMap<>();
        payload.put("userID", userID);
        payload.put("userName", userName);
        String jsonPayload = gson.toJson(payload);

        stompClient.topic("/topic/lobby-created").subscribe(topicMessage -> {
            Log.d("Received", topicMessage.getPayload());
        });

        stompClient.send("/app/create_new_lobby", userID).subscribe();


    }

    public Flowable<String> joinLobby(String lobbyCode, String userID, String userName) {
        HashMap<String, String> payload = new HashMap<>();
        payload.put("lobbyCode", lobbyCode);
        payload.put("userID", userID);
        payload.put("userName", userName);
        String jsonPayload = gson.toJson(payload);

        Disposable disposable = stompClient.send("/join_lobby", jsonPayload).subscribe();

        return stompClient.topic("topic/lobby-joined")
                .map(topicMessage -> {
                    Log.d("Response", topicMessage.toString());
                    return topicMessage.getPayload();
                })
                .subscribeOn(Schedulers.io());
    }

    public Flowable<String> helloMessage(String message) {
        Log.d("Network", "helloMessage called");
        return stompClient.topic("/topic/hello-response")
                .map(topicMessage -> {
                    Log.d("Network", "Received: " + topicMessage.getPayload());
                    return topicMessage.getPayload();
                })
                .doOnSubscribe(disposable -> stompClient.send("/app/hello", message).subscribe())
                .subscribeOn(Schedulers.io());
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
