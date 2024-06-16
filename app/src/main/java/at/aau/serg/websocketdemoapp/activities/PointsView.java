package at.aau.serg.websocketdemoapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import at.aau.serg.websocketdemoapp.R;
import at.aau.serg.websocketdemoapp.services.PointsViewService;

public class PointsView extends AppCompatActivity {
    private PointsViewService pointsViewService;
    private TextView[][] pointViews;
    private TextView[] sumViews;
    private  Map<String, HashMap<Integer, Integer>> playerPoints;
    private Map<String, Integer> pointSums;
    private TableLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Button backButton;
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_points_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        pointsViewService = new PointsViewService(this, this);
        backButton = findViewById(R.id.buttonBreak);
        layout = findViewById(R.id.tableLayout);
        backButton.setOnClickListener(v -> backButtonClicked());
    }

    public void updateUI() {
        playerPoints = pointsViewService.getPlayerPoints();
        Log.d("PLAYER POINTS", playerPoints.keySet().toString());

        if (playerPoints.isEmpty()) {
            Log.e("updateUI", "No player points data available");
            displayPlayerNamesOnly();
            return;
        }
        int numberOfPlayers = playerPoints.size();
        int rounds = playerPoints.values().stream().findFirst().map(Map::size).orElse(0);

        calcSum();

        pointViews = new TextView[rounds][numberOfPlayers];
        sumViews = new TextView[numberOfPlayers];
        TableLayout tableLayout = createPointTable(playerPoints, rounds);
        layout.removeAllViews();
        layout.addView(tableLayout);

        if (rounds > 0) {
            setPointViews();
        }
    }

    private void displayPlayerNamesOnly() {
        TableLayout tableLayout = new TableLayout(this);
        tableLayout.addView(createPlayerRow(playerPoints));
        layout.addView(tableLayout);
    }

    public void backButtonClicked() {
        finish();
    }

    public TableLayout createPointTable(Map<String, HashMap<Integer, Integer>> playerPoints, int rounds) {
        TableLayout tableLayout = new TableLayout(this);
        tableLayout.addView(createPlayerRow(playerPoints));
        if (rounds > 0) {
            for (int i = 0; i < rounds; i++) {
                tableLayout.addView(createRoundRow(i, playerPoints));
            }
        }
        tableLayout.addView(createSumRow(playerPoints));
        return tableLayout;
    }

    public TableRow createSumRow(Map<String, HashMap<Integer, Integer>> playerPoints) {
        TableRow sumRow = new TableRow(this);
        sumRow.addView(createTextView("Sum"));
        int i = 0;
        for(String player : playerPoints.keySet()) {
            TextView t = createTextView(String.valueOf(pointSums.get(player)));
            sumRow.addView(t);
            sumViews[i++] = t;
        }
        return sumRow;
    }


    public TableRow createRoundRow(int round, Map<String, HashMap<Integer, Integer>> playerPoints) {
        TableRow roundRow = new TableRow(this);
        roundRow.addView(createTextView("Round " + (round + 1)));

        for (int i = 0; i < playerPoints.keySet().size(); i++) {
            TextView t = createTextView("");
            roundRow.addView(t);
            pointViews[round][i] = t;
        }
        return roundRow;
    }

    public TableRow createPlayerRow(Map<String, HashMap<Integer, Integer>> playerPoints) {
        TableRow playerRow = new TableRow(this);
        playerRow.addView(createTextView(""));
        for (String player : playerPoints.keySet()) {
            playerRow.addView(createTextView(player));
        }
        return playerRow;
    }

    private void setPointViews() {
        for (int round = 0; round < pointViews.length; round++) {
            int playerIndex = 0;
            for (Entry<String, HashMap<Integer, Integer>> entry : playerPoints.entrySet()) {
                HashMap<Integer, Integer> roundsMap = entry.getValue();
                Integer pointsOrNull = roundsMap.get(round + 1);
                int points = (pointsOrNull != null) ? pointsOrNull : 0;
                pointViews[round][playerIndex].setText(String.valueOf(points));
                playerIndex++;
            }
        }
        int i = 0;
        for (String player : playerPoints.keySet()) {
            if (i < sumViews.length) {
                sumViews[i].setText(String.valueOf(pointSums.get(player)));
            }
            i++;
        }
    }


    private TextView createTextView(String content) {
        TextView t = new TextView(this);
        t.setText(content);
        t.setPadding(16, 16, 16, 16);
        return t;
    }

    private void calcSum() {
        pointSums = new HashMap<>();
        for (Entry<String, HashMap<Integer, Integer>> entry : playerPoints.entrySet()) {
            int sum = 0;
            for (int points : entry.getValue().values()) {
                sum += points;
            }
            pointSums.put(entry.getKey(), sum);
        }
    }
}