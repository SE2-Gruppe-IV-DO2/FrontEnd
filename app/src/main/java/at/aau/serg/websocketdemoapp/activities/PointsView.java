package at.aau.serg.websocketdemoapp.activities;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import at.aau.serg.websocketdemoapp.R;

public class PointsView extends AppCompatActivity {
    private int[][] pointsArray;
    private TextView[][] pointsTextViews;
    private TextView[] sumViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_points_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        pointsArray = new int[5][5];
        pointsTextViews = new TextView[5][4];
        sumViews =  new TextView[4];
        TableLayout tableLayout = findViewById(R.id.tableLayout);

        TableRow headerRow = new TableRow(this);
        headerRow.addView(createTextView(""));
        for (int player = 1; player <= 4; player++) {
            headerRow.addView(createTextView("Player " + player));
        }
        tableLayout.addView(headerRow);

        for (int round = 0; round < 5; round++) {
            TableRow roundRow = new TableRow(this);
            roundRow.addView(createTextView("Round " + (round + 1)));
            for (int player = 0; player < 4; player++) {
                roundRow.addView(createTextView(""));
                pointsTextViews[round][player]  = (TextView) roundRow.getVirtualChildAt(player);
            }
            tableLayout.addView(roundRow);
        }
        TableRow sumRow = new TableRow(this);
        sumRow.addView(createTextView("Sum "));
        for(int player = 0; player < 4; player++) {
            sumRow.addView(createTextView(""));
            sumViews[player] = (TextView) sumRow.getVirtualChildAt(player + 1);
        }

        tableLayout.addView(sumRow);
        setPoints(1, 1, 100);
        setPoints(3, 2, 75);
        calcSum();
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(16, 16, 16, 16);
        return textView;
    }

    private void setPoints(int round, int player, int points) {
        pointsArray[round][player] = points;
        TextView textView = pointsTextViews[round - 1][player];
        textView.setText(String.valueOf(points));
    }

    private void calcSum() {
        for(int player = 0; player < 4; player++) {
            int sum = 0;
            for(int round = 0; round < 5; round++) {
                int points = pointsArray[round][player];
                sum += points;
            }
            TextView t = sumViews[player];
            t.setText(String.valueOf(sum));
        }
    }
}