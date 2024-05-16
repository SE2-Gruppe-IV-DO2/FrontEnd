package at.aau.serg.websocketdemoapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import at.aau.serg.websocketdemoapp.R;
import at.aau.serg.websocketdemoapp.dto.GameData;
import at.aau.serg.websocketdemoapp.fragments.CardFragment;
import at.aau.serg.websocketdemoapp.helper.Card;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.services.ActiveGameService;

public class ActiveGame extends AppCompatActivity {
    private GameData gameData;
    private final DataHandler dataHandler = DataHandler.getInstance(this);
    private ActiveGameService activeGameService;

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
        activeGameService = new ActiveGameService(this, ActiveGame.this, gameData);
        Button pointView = findViewById(R.id.pointsView);
        pointView.setOnClickListener(v -> pointViewClicked());
        activeGameService.getData();
    }

    public void refreshActiveGame() {
        this.displayCardsInHand();
    }

    public void displayCardsInHand() {
        // todo: handle gameData and remove manual creation of card list

        if (gameData.getCardList() == null) {
            gameData.parseJsonString(dataHandler.getGameData());
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        FrameLayout container = findViewById(R.id.cardsInHand);
        int overlapPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                (-2 * gameData.getCardList().size() + 65), getResources().getDisplayMetrics());

        int cardWidthPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                120, getResources().getDisplayMetrics());
        int midPoint = getDeviceWidthPx() / 2 - cardWidthPx / 2;

        container.removeAllViews();

        for (int c = 1; c <= gameData.getCardList().size(); c++) {
            int marginLeft = midPoint + (c - Math.round(gameData.getCardList().size() / 2f)) * overlapPx;
            float rotation = (c - Math.round(gameData.getCardList().size() / 2f)) * 0.75f;
            CardFragment cardFragment = CardFragment
                    .newInstance(gameData.getCardList().get(c-1).toString(), cardWidthPx, marginLeft, rotation);
            cardFragment.setFlingListener(activeGameService);
            transaction.add(container.getId(), cardFragment, "card_" + (c));
        }
        transaction.commit();
    }

    @SuppressLint("DiscouragedApi")
    public void displayCardsPlayed() {
        ImageView view = findViewById(R.id.playedCardPlayerX);
        Card card = gameData.getCardsPlayed().get(gameData.getCardsPlayed().size() - 1);
        Log.d("DISPLAY CARD", card.toString());
        view.setImageResource(getResources().getIdentifier(card.getName(), "drawable", this.getPackageName()));
        view.setVisibility(View.VISIBLE);

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
        //int[] imageViewIds = {R.id.playedCardPlayerX, R.id.playedCardPlayer1,
          //      R.id.playedCardPlayer2, R.id.playedCardPlayer3, R.id.playedCardPlayer4};

        /*// set card data to imageview
        for (int i = 1; i <= gameData.getCardsPlayed().size(); i++) {
            Card card = gameData.getCardsPlayed().get(i - 1);
            ImageView iv = findViewById(imageViewIds[(playerPosition - i) % imageViewIds.length]);
            iv.setImageResource(getResources().getIdentifier(card.getName(), "drawable"));
        }*/
    }

    private int getDeviceWidthPx() {
        WindowManager windowManager = getWindowManager();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public void pointViewClicked() {
        Intent intent = new Intent(ActiveGame.this, PointsView.class);
        startActivity(intent);
    }

    public void updateActivePlayerInformation(String activePlayerName) {
        runOnUiThread(() -> {
            Toast toast = Toast.makeText(this, getString(R.string.active_player) + activePlayerName, Toast.LENGTH_LONG);
            toast.show();
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), getString(R.string.active_player) + activePlayerName, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
