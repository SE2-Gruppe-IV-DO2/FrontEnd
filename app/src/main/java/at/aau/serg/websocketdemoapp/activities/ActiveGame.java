package at.aau.serg.websocketdemoapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;

import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import at.aau.serg.websocketdemoapp.R;
import at.aau.serg.websocketdemoapp.dto.GameData;
import at.aau.serg.websocketdemoapp.fragments.CardFragment;
import at.aau.serg.websocketdemoapp.services.ActiveGameService;

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
        ActiveGameService activeGameService;
        activeGameService = new ActiveGameService(this, ActiveGame.this);
        activeGameService.getData();
        Button pointView = findViewById(R.id.pointsView);
        pointView.setOnClickListener(v -> pointViewClicked());
    }

    public void refreshActiveGame(String gameData) {
        this.displayCardsInHand(gameData);
        this.displayCardsPlayed(gameData);
    }

    public void displayCardsInHand(String gameDataString) {

        // todo: handle gameData and remove manual creation of card list

        // temporary adding of cards
        List<String> cards = new LinkedList<>();
        for (int i = 1; i <= 15; i++) {
            cards.add(getRandomCardName());
        }
        cards = cards.stream().sorted().collect(Collectors.toList());

//        GameData gameData = new Gson().fromJson(gameDataString, GameData.class);
//        cards = gameData.getCardList().stream().map(Object::toString).collect(Collectors.toList());

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        FrameLayout container = findViewById(R.id.cardsInHand);
        int overlapPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                (-2 * cards.size() + 65), getResources().getDisplayMetrics());

        int cardWidthPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                120, getResources().getDisplayMetrics());
        int midPoint = getDeviceWidthPx() / 2 - cardWidthPx / 2;

        // display cards
        for (int c = 1; c <= cards.size(); c++) {
            // Implement this method to get random color
            int marginLeft = midPoint + (c - Math.round(cards.size() / 2f)) * overlapPx;
            float rotation = (c - Math.round(cards.size() / 2f)) * 0.75f;
            CardFragment cardFragment = CardFragment
                    .newInstance(cards.get(c-1), cardWidthPx, marginLeft, rotation);
            transaction.add(container.getId(), cardFragment, "card_" + (c));
        }
        transaction.commit();
    }

    @SuppressLint("DiscouragedApi")
    public void displayCardsPlayed(String gameDataString) {
        //GameData gameData = new Gson().fromJson(gameDataString, GameData.class);

        // sample code to display played cards

        /*// find playerPosition
        int playerPosition = gameData.getCardsPlayed().indexOf(
                gameData.getCardsPlayed().stream().filter(c -> c.getPlayer().equals(me)).findFirst()
        );
        if (playerPosition == -1){
            playerPosition = gameData.getCardsPlayed().size() + 1;
        }*/

        // played Card ids
        int[] imageViewIds = {R.id.playedCardPlayerX, R.id.playedCardPlayer1,
                R.id.playedCardPlayer2, R.id.playedCardPlayer3, R.id.playedCardPlayer4};

        /*// set card data to imageview
        for (int i = 1; i <= gameData.getCardsPlayed().size(); i++) {
            Card card = gameData.getCardsPlayed().get(i - 1);
            ImageView iv = findViewById(imageViewIds[(playerPosition - i) % imageViewIds.length]);
            iv.setImageResource(getResources().getIdentifier(card.getName(), "drawable"));
        }*/

    }

    //test method
    private String getRandomCardName() {
        SecureRandom secureRandom = new SecureRandom();
        String[] colors = new String[]{"card_blue", "card_red", "card_yellow", "card_green", "card_purple"};
        int i = secureRandom.nextInt(colors.length);
        int number = secureRandom.nextInt(11) + 1;
        return colors[i] + number;
    }

    private int getDeviceWidthPx() {
        WindowManager windowManager = getWindowManager();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    /*public void onCardClicked(String color, int value) {
        // Handle card click
    }
    */
    public void pointViewClicked() {
        Intent intent = new Intent(ActiveGame.this, PointsView.class);
        startActivity(intent);
    }

    public void updateActivePlayerInformation(String activePlayerName) {
        Toast toast = Toast.makeText(this, getString(R.string.active_player) + activePlayerName, Toast.LENGTH_LONG);
        toast.show();
    }
}
