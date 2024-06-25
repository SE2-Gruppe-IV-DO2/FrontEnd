package at.aau.serg.websocketdemoapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import at.aau.serg.websocketdemoapp.R;
import at.aau.serg.websocketdemoapp.dto.PointsResponse;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.helper.JsonParsingException;
import at.aau.serg.websocketdemoapp.networking.StompHandler;

public class GameEndView extends AppCompatActivity {

    private LinearLayout scoreBoard;
    private DataHandler dataHandler;
    private StompHandler stompHandler;
    private HashMap<String, Integer> sumPointsPerPlayer;
    private ObjectMapper objectMapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button button;
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_end_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        stompHandler = StompHandler.getInstance();
        dataHandler = DataHandler.getInstance(this);
        objectMapper = new ObjectMapper();
        button = findViewById(R.id.scoreBoardButton);
        scoreBoard = findViewById(R.id.scoreBoard);
        button.setOnClickListener(v -> restartApp());
        fetchPointsBoard();
    }

    @SuppressLint("DefaultLocale")
    public void setScoreBoard() {
        runOnUiThread(() -> {
            List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(sumPointsPerPlayer.entrySet());
            sortedEntries.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
            scoreBoard.removeAllViews();
            for (Map.Entry<String, Integer> e : sortedEntries) {
                TextView textView = new TextView(this);
                textView.setTextSize(20);
                textView.setPadding(0, 0, 0, 10);
                textView.setText(String.format("%s: %d", e.getKey(), e.getValue()));
                runOnUiThread(() -> scoreBoard.addView(textView));
            }
        });
    }

    public void fetchPointsBoard() {
        new Thread(() -> stompHandler.getPoints(dataHandler.getLobbyCode(), this::processPointData)).start();
    }

    public void processPointData(String data) {
        PointsResponse pointsResponse;
        try {
            pointsResponse = objectMapper.readValue(data, PointsResponse.class);
        } catch (JsonProcessingException e) {
            throw new JsonParsingException("JSON Parse Exception", e);
        }
        sumPointsPerPlayer = new HashMap<>();

        for (String s : pointsResponse.getPlayerPoints().keySet()) {
            sumPointsPerPlayer.put(s, Objects.requireNonNull(pointsResponse.getPlayerPoints().get(s)).values().stream().mapToInt(Integer::intValue).sum());
        }
        setScoreBoard();
    }

    private void restartApp() {
        Intent intent = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}