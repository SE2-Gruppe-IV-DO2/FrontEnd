package at.aau.serg.websocketdemoapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Comparator;
import java.util.List;

import at.aau.serg.websocketdemoapp.R;
import at.aau.serg.websocketdemoapp.dto.GameData;
import at.aau.serg.websocketdemoapp.dto.HandCardsRequest;
import at.aau.serg.websocketdemoapp.fragments.CardFragment;
import at.aau.serg.websocketdemoapp.helper.Card;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.helper.JsonParsingException;
import at.aau.serg.websocketdemoapp.helper.ShakeDetector;
import at.aau.serg.websocketdemoapp.networking.StompHandler;
import at.aau.serg.websocketdemoapp.services.ActiveGameService;

public class ActiveGame extends AppCompatActivity {
    private GameData gameData;
    private StompHandler stompHandler;
    private DataHandler dataHandler;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final int[] imageViewIds = {R.id.playedCardPlayerX, R.id.playedCardPlayer1,
            R.id.playedCardPlayer2, R.id.playedCardPlayer3, R.id.playedCardPlayer4};
    private ActiveGameService activeGameService;

    private boolean pendingFragmentTransaction = false;
    private boolean isResumed = false;

    private ShakeDetector mShakeDetector;

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
        stompHandler = StompHandler.getInstance();
        dataHandler = DataHandler.getInstance(this);
        gameData = new GameData();
        activeGameService = new ActiveGameService(this, ActiveGame.this, gameData);
        Button pointView = findViewById(R.id.pointsView);
        pointView.setOnClickListener(v -> pointViewClicked());
        getData();

        mShakeDetector = new ShakeDetector(this, this::sortCardsInHand);
    }

    public void refreshActiveGame() {
        this.displayCardsInHand();
    }

    public void displayCardsInHand() {

        if (!isResumed) {
            pendingFragmentTransaction = true;
            return;
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
                    .newInstance(gameData.getCardList().get(c - 1).getImgPath(), cardWidthPx, marginLeft, rotation);
            cardFragment.setFlingListener(activeGameService);
            transaction.add(container.getId(), cardFragment, "card_" + (c));
        }
        transaction.commitAllowingStateLoss();
    }

    private void checkForGaiaAndChooseColor() {
        if (activeGameService.isCurrentlyActivePlayer()
                && gameData.findCardByCardName("card_gaia0") != null) {

            activeGameService.setPreventCardFling(true);

            runOnUiThread(() -> Toast.makeText(getApplicationContext(), getString(R.string.play_gaia), Toast.LENGTH_SHORT).show());

            ImageView gaiaImage = findViewById(R.id.playedGaia);
            gaiaImage.setImageResource(R.drawable.card_gaia0);
            gaiaImage.setVisibility(View.VISIBLE);

            LinearLayout playedGaiaColors = findViewById(R.id.playedGaiaColors);

            int[] colorDrawables = {
                    R.drawable.color_green,
                    R.drawable.color_yellow,
                    R.drawable.color_red,
                    R.drawable.color_blue,
                    R.drawable.color_purple
            };

            for (int color : colorDrawables) {
                ImageView colorImage = new ImageView(this);
                colorImage.setImageResource(color);
                LinearLayout.LayoutParams circleParams = new LinearLayout.LayoutParams(
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics()),
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics())
                );
                circleParams.setMargins(8, 0, 8, 0); // Add margin between circles
                colorImage.setLayoutParams(circleParams);

                colorImage.setOnClickListener(v ->
                        onColorPicked(getResources().getResourceEntryName(color))
                );

                playedGaiaColors.addView(colorImage);
                playedGaiaColors.setVisibility(View.VISIBLE);
            }
        }
    }

    private void onColorPicked(String pickedColor) {
        Toast.makeText(this, "Picked :" + pickedColor, Toast.LENGTH_SHORT).show();

        Card card = gameData.findCardByCardName("card_gaia0");
        if (gameData.getCardList().remove(card)) {
            Log.d("REMOVE CARD", "CARD REMOVED SUCCESSFULLY");
        }

        activeGameService.playCard("gaia", pickedColor, 0);
        this.refreshActiveGame();

        findViewById(R.id.playedGaia).setVisibility(View.INVISIBLE);
        findViewById(R.id.playedGaiaColors).setVisibility(View.INVISIBLE);
        activeGameService.setPreventCardFling(false);
    }

    @SuppressLint("DiscouragedApi")
    public void displayCardsPlayed() {
        List<Card> cardsPlayed = gameData.getCardsPlayed();
        for (int i = 0; i < cardsPlayed.size(); i++) {
            Card card = cardsPlayed.get(i);
            ImageView imageView = findViewById(imageViewIds[i]);
            int resId = getResources().getIdentifier(card.getImgPath(), "drawable", getPackageName());
            imageView.setImageResource(resId);
            imageView.setZ(i);
            imageView.setVisibility(View.VISIBLE);
        }
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
            TextView activePlayerNameView = findViewById(R.id.active_player_name);
            activePlayerNameView.setText(activePlayerName);
        });
    }

    public void showPlayerWonTrickMessage(String playerWonMessage) {
        Toast.makeText(this, playerWonMessage, Toast.LENGTH_SHORT).show();
    }

    public void getData() {
        new Thread(() -> stompHandler.dealNewRound(dataHandler.getLobbyCode(), dataHandler.getPlayerID(),
                response -> new Handler(Looper.getMainLooper()).post(() -> {
                    HandCardsRequest handCardsRequest;
                    try {
                        handCardsRequest = objectMapper.readValue(response, HandCardsRequest.class);
                    } catch (JsonProcessingException e) {
                        throw new JsonParsingException("Failed to parse JSON response", e);
                    }
                    gameData.setCardList(handCardsRequest.getHandCards());
                    dataHandler.setGameData(response);
                    if (!isFinishing() && !isDestroyed()) {
                            runOnUiThread(this::refreshActiveGame);
                            runOnUiThread(this::checkForGaiaAndChooseColor);
                    }
                }))).start();
    }

    private void sortCardsInHand() {
        Comparator<Card> noColorFirst = Comparator.comparing(card -> card.getColor().isEmpty(), Comparator.reverseOrder());
        Comparator<Card> nameComparator = Comparator.comparing(card -> card.getCardType().getName());
        Comparator<Card> valueComparator = Comparator.comparingInt(Card::getValue);

        gameData.getCardList().sort(noColorFirst
                .thenComparing(nameComparator)
                .thenComparing(valueComparator)
        );

        this.refreshActiveGame();
    }

    public void clearPlayedCards() {
        gameData.getCardsPlayed().clear();
        for (int imageViewId : imageViewIds) {
            ImageView imageView = findViewById(imageViewId);
            imageView.setImageDrawable(null); // Remove the image
            imageView.setVisibility(View.INVISIBLE); // Make the ImageView invisible
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResumed = true;
        if (pendingFragmentTransaction) {
            displayCardsInHand();
            pendingFragmentTransaction = false;
        }
        mShakeDetector.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResumed = false;
        mShakeDetector.stopListening();
    }
}
