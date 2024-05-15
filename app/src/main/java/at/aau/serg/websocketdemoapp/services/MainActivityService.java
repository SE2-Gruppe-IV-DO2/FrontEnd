package at.aau.serg.websocketdemoapp.services;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import at.aau.serg.websocketdemoapp.activities.MainActivity;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.networking.StompHandler;

public class MainActivityService {

    private final DataHandler dataHandler;
    private final MainActivity mainActivity;
    private static final String TAG = "Stomp";

    StompHandler stompHandler;

    public MainActivityService(Context context, MainActivity activity) {
        stompHandler = StompHandler.getInstance();
        dataHandler = DataHandler.getInstance(context);
        this.mainActivity = activity;
    }

    public void createGameService(EditText editText, TextView textView) {
        String playerName = editText.getText().toString();
        if (nameIsValid(playerName)) {
            textView.setVisibility(View.INVISIBLE);
            savePlayerName(playerName);
            new Thread(() -> stompHandler.createLobby(dataHandler.getPlayerID(), dataHandler.getPlayerName(), callback -> {
                dataHandler.setLobbyCode(callback);
                Log.d(TAG, callback);

                Log.d("LobbyCode", dataHandler.getLobbyCode());
                mainActivity.changeToCreateActivity();
            })).start();
            //Log.d("LobbyCode", dataHandler.getLobbyCode());
            //mainActivity.changeToCreateActivity();
        } else {
            textView.setVisibility(View.VISIBLE);
        }
    }

    public void joinGameService (EditText editText, TextView textView){
        String playerName = editText.getText().toString();
        if (nameIsValid(playerName)) {
            textView.setVisibility(View.INVISIBLE);
            savePlayerName(playerName);
            mainActivity.changeToJoinActivity();
        } else {
            textView.setVisibility(View.VISIBLE);
        }
    }

    public void tutorialService (){
        mainActivity.changeToTutorialActivity();
    }

    private boolean nameIsValid(String name) {
        return name.matches("^[a-zA-Z]+$");
    }

    private void savePlayerName(String playerName) {
        if (nameIsValid(playerName)) {
            dataHandler.setPlayerName(playerName);
            dataHandler.setPlayerID(String.valueOf(System.currentTimeMillis() / 1000));
        }
    }
}