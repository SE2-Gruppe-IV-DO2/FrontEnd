package at.aau.serg.websocketdemoapp.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import at.aau.serg.websocketdemoapp.R;

public class ActiveGame extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_active_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        populateCards();
    }
    private void populateCards() {

        //int [] cardResourceIds wurde nur provisorisch eingerichtet, sollte jedoch durch eine Methode in der ActiveGameService Klasse ausgetauscht werden

        int[] cardResourceIds = {
                R.drawable.gaia, R.drawable.blau1, R.drawable.blau2,
                R.drawable.gruen1, R.drawable.gruen3,R.drawable.gruen4,
                R.drawable.gelb2,R.drawable.gelb4,R.drawable.gelb10,
                R.drawable.violett2,R.drawable.violett3,R.drawable.violett5,
                R.drawable.rot2,R.drawable.rot4,R.drawable.rot5,

        };

        LinearLayout firstRow = findViewById(R.id.first_row);
        LinearLayout secondRow = findViewById(R.id.second_row);
        LinearLayout thirdRow = findViewById(R.id.third_row);
        LinearLayout[] rows = new LinearLayout[]{firstRow, secondRow, thirdRow};

        for (int i = 0; i < cardResourceIds.length; i++) {
            ImageView cardView = new ImageView(this);
            cardView.setImageResource(cardResourceIds[i]);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, convertDpToPx(8), 0);
            cardView.setLayoutParams(layoutParams);

            // Karte der entsprechenden Zeile hinzufügen
            rows[i / 5].addView(cardView);
        }
    }

    private int convertDpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
