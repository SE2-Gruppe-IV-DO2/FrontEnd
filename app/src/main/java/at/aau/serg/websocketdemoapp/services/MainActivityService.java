package at.aau.serg.websocketdemoapp.services;

import android.view.View;

import at.aau.serg.websocketdemoapp.R;

public class MainActivityService {

    public void createGameService(View view) {
        if (nameIsValid(view.findViewById(R.id.playerName).toString())) {
            view.findViewById(R.id.labelError).setVisibility(View.INVISIBLE);
        } else {
            view.findViewById(R.id.labelError).setVisibility(View.VISIBLE);
        }
    }

    public void joinGameService (View view){
        if (nameIsValid(view.findViewById(R.id.playerName).toString())) {
            view.findViewById(R.id.labelError).setVisibility(View.INVISIBLE);
        } else {
            view.findViewById(R.id.labelError).setVisibility(View.VISIBLE);
        }
    }

    public void tutorialService (View view){
        if (nameIsValid(view.findViewById(R.id.playerName).toString())) {
            view.findViewById(R.id.labelError).setVisibility(View.INVISIBLE);
        } else {
            view.findViewById(R.id.labelError).setVisibility(View.VISIBLE);
        }
    }

    public boolean nameIsValid(String name) {
        return name.matches("\\d");
    }
}