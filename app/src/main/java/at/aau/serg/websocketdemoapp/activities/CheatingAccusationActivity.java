package at.aau.serg.websocketdemoapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

import at.aau.serg.websocketdemoapp.R;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.services.CheatingAccusationService;


public class CheatingAccusationActivity extends AppCompatActivity {

    private CheatingAccusationService cheatingAccusationService;
    private DataHandler dataHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cheating_accusation_view);

        dataHandler = DataHandler.getInstance(this);

        cheatingAccusationService = new CheatingAccusationService(this, CheatingAccusationActivity.this);
        cheatingAccusationService.getPlayers();
    }

    public void updateUI() {
        Map<String, String> players = cheatingAccusationService.getPlayerNamesAndIds();

        LinearLayout layout = findViewById(R.id.cheatingAccusationLayout);

        layout.removeAllViews();

        for (String playerId : players.keySet()) {
            if (playerId.equals(dataHandler.getPlayerID())) {
                continue;
            }
            Button button = new Button(this);
            button.setText(players.get(playerId));
            button.setOnClickListener(v -> cheatingAccusationService.onCheatingAccusationButtonClicked(playerId));
            layout.addView(button);
        }
        Button noneButton = new Button(this);
        noneButton.setText(R.string.nobodyCheated);
        noneButton.setOnClickListener(v -> cheatingAccusationService.onCheatingAccusationButtonClicked(""));
        layout.addView(noneButton);
    }

    public void showCheatingAccusationResult(boolean result) {
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), result ? "Correct" : "Not Correct", Toast.LENGTH_SHORT).show());
        finish();
    }

}
