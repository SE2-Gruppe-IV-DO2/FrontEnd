package at.aau.serg.websocketdemoapp.activities;

import android.content.Intent;
import android.os.Bundle;
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
    private static final int ROUNDS = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TableLayout layout;
        Button backButton;
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_points_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        pointsViewService = new PointsViewService();
        backButton = findViewById(R.id.buttonBreak);
        layout = findViewById(R.id.tableLayout);
        backButton.setOnClickListener(v -> backButtonClicked());

        pointViews = new TextView[ROUNDS][pointsViewService.getPlayerPoints().size()];
        sumViews = new TextView[pointsViewService.getPlayerPoints().size()];
        TableLayout tableLayout = createPointTable();
        layout.addView(tableLayout);
        updateUI();
    }

    public void backButtonClicked() {
        Intent intent = new Intent(PointsView.this, ActiveGame.class);
        startActivity(intent);
    }

    public TableLayout createPointTable() {
        TableLayout tableLayout = new TableLayout(this);
        tableLayout.addView(createPlayerRow());
        for(int i = 0; i < ROUNDS; i++) {
            tableLayout.addView(createRoundRow(i));
        }
        tableLayout.addView(createSumRow());
        return tableLayout;
    }

    public TableRow createSumRow() {
        TableRow sumRow = new TableRow(this);
        sumRow.addView(createTextView("Sum"));
        for(int i = 0; i < pointsViewService.getPlayerPoints().size(); i++) {
            TextView t = createTextView("");
            sumRow.addView(t);
            sumViews[i] = t;
        }
        return sumRow;
    }

    public TableRow createRoundRow(int round) {
        TableRow roundRow = new TableRow(this);
        roundRow.addView(createTextView("Round " + (round + 1)));
        for(int i = 0; i < pointsViewService.getPlayerPoints().size(); i++) {
            TextView t = createTextView("");
            roundRow.addView(t);
            pointViews[round][i] = t;
        }
        return roundRow;
    }

    public TableRow createPlayerRow() {
        TableRow playerRow = new TableRow(this);
        playerRow.addView(createTextView(""));
        for (String s : pointsViewService.getPlayerPoints().keySet()) {
            playerRow.addView(createTextView(s));
        }
        return playerRow;
    }

    public void updateUI() {
        setPointViews();
        setSumViews();
    }

    private void setPointViews() {
        Map<String, HashMap<Integer, Integer>> playerPoints = pointsViewService.getPlayerPoints();
        for (int round = 0; round < ROUNDS; round++) {
            int playerIndex = 0;
            for (Entry<String, HashMap<Integer, Integer>> entry : playerPoints.entrySet()) {
                HashMap<Integer, Integer> roundsMap = entry.getValue();
                Integer pointsOrNull = roundsMap.get(round + 1);
                int points = (pointsOrNull != null) ? pointsOrNull : 0;
                pointViews[round][playerIndex].setText(String.valueOf(points));
                playerIndex++;
            }
        }
    }

    private void setSumViews() {
        for(int i = 0; i < pointsViewService.getPlayerPoints().size(); i++) {
            sumViews[i].setText(String.valueOf(pointsViewService.getSumArray()[i]));
        }
    }

    private TextView createTextView(String content) {
        TextView t =  new TextView(this);
        t.setText(content);
        t.setPadding(16, 16, 16, 16);
        return t;
    }
}