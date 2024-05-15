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

import java.security.SecureRandom;

import at.aau.serg.websocketdemoapp.R;
import at.aau.serg.websocketdemoapp.dto.GameData;
import at.aau.serg.websocketdemoapp.fragments.CardFragment;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.services.ActiveGameService;

public class ActiveGame extends AppCompatActivity {
    private GameData gameData;
    private final DataHandler dataHandler = DataHandler.getInstance(this);

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
        gameData = new GameData();
        ActiveGameService activeGameService;
        activeGameService = new ActiveGameService(this, ActiveGame.this);
        activeGameService.getData();
        Button pointView = findViewById(R.id.pointsView);
        pointView.setOnClickListener(v -> pointViewClicked());
    }

    public void refreshActiveGame() {
        this.displayCardsInHand();
        this.displayCardsPlayed();
    }

    public void displayCardsInHand() {

        // todo: handle gameData and remove manual creation of card list

        // temporary adding of cards
        /*
        List<String> cards = new LinkedList<>();
        for (int i = 1; i <= 15; i++) {
            cards.add(getRandomCardName());
        }
        cards = cards.stream().sorted().collect(Collectors.toList());
         */

        gameData.parseJsonString(dataHandler.getGameData());

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        FrameLayout container = findViewById(R.id.cardsInHand);
        int overlapPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                (-2 * gameData.getCardList().size() + 65), getResources().getDisplayMetrics());

        int cardWidthPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                120, getResources().getDisplayMetrics());
        int midPoint = getDeviceWidthPx() / 2 - cardWidthPx / 2;

        // display cards
        for (int c = 1; c <= gameData.getCardList().size(); c++) {
            // Implement this method to get random color
            int marginLeft = midPoint + (c - Math.round(gameData.getCardList().size() / 2f)) * overlapPx;
            float rotation = (c - Math.round(gameData.getCardList().size() / 2f)) * 0.75f;
            CardFragment cardFragment = CardFragment
                    .newInstance(gameData.getCardList().get(c-1).toString(), cardWidthPx, marginLeft, rotation);
            transaction.add(container.getId(), cardFragment, "card_" + (c));
        }
        transaction.commit();
    }

    @SuppressLint("DiscouragedApi")
    public void displayCardsPlayed() {
        //GameData gameData = new Gson().fromJson(gameDataString, GameData.class);

        // sample code to display played cards

       // if (gameData.getCardsPlayed().size() != 0) {

       // }
        /*// find playerPosition

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
