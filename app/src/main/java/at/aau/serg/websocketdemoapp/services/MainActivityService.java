package at.aau.serg.websocketdemoapp.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import at.aau.serg.websocketdemoapp.activities.MainActivity;
import at.aau.serg.websocketdemoapp.networking.StompHandler;

public class MainActivityService {

    SharedPreferences sharedPreferences;
    MainActivity mainActivity;

    StompHandler stompHandler;

    public MainActivityService(Context context, MainActivity activity) {
        sharedPreferences = context.getSharedPreferences("druids_data", Context.MODE_PRIVATE);
        this.mainActivity = activity;
    }

    public void createGameService(EditText editText, TextView textView) {
        String playerName = editText.getText().toString();
        if (nameIsValid(playerName)) {
            textView.setVisibility(View.INVISIBLE);
            savePlayerName(playerName);
            mainActivity.changeToCreateActivity();
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

    public void tutorialService (EditText editText, TextView textView){
        mainActivity.changeToTutorialActivity();
    }

    private boolean nameIsValid(String name) {
        return name.matches("^[a-zA-Z]+$");
    }

    private void savePlayerName(String playerName) {
        if (nameIsValid(playerName)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("playerName", playerName);
            editor.apply();
        }
    }
}