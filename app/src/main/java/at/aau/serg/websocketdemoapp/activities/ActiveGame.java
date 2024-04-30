package at.aau.serg.websocketdemoapp.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import at.aau.serg.websocketdemoapp.R;
import at.aau.serg.websocketdemoapp.fragments.CardFragment;

public class ActiveGame extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cards_in_hand);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        displayCardsInHand(); // request cards from service
    }

    private void displayCardsInHand() {
        // Obtain the WindowManager
        WindowManager windowManager = getWindowManager();

        // Create DisplayMetrics object to hold display information
        DisplayMetrics displayMetrics = new DisplayMetrics();

        // Get display metrics from the default display
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);


        List<String> cards = new LinkedList<>();
        for (int i = 1; i <= 15; i++) {
            cards.add(getRandomCardName());
        }
        cards = cards.stream().sorted().collect(Collectors.toList());


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        FrameLayout container = findViewById(R.id.cardsInHand);
        int overlapPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (-2 * cards.size() + 65), getResources().getDisplayMetrics());

        int cardWidthPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, getResources().getDisplayMetrics());
        // Calculate the display width divided by 2
        int displayWidthDividedBy2 = displayMetrics.widthPixels / 2 - cardWidthPx / 2;

        for (int c = 1; c <= cards.size(); c++) {
            // Implement this method to get random color
            int marginLeft = displayWidthDividedBy2 + (c - Math.round(cards.size() / 2f)) * overlapPx;
            float rotation = (c - Math.round(cards.size() / 2f)) * 0.75f;
            CardFragment cardFragment = CardFragment.newInstance(cards.get(c-1), cardWidthPx, marginLeft, rotation);
            transaction.add(container.getId(), cardFragment, "card_" + (c));
        }
        transaction.commit();
    }

    //test method
    private String getRandomCardName() {
        String[] colors = new String[]{"card_blue", "card_red", "card_yellow", "card_green", "card_purple"};
        int i = (int) Math.round(Math.random() * 4);
        return colors[i] + Math.round(Math.random() * 11 + 1);
    }

    public void onCardClicked(String color, int value) {
        // Handle card click
    }
}
