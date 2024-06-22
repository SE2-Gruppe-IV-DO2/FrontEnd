package at.aau.serg.websocketdemoapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.aau.serg.websocketdemoapp.R;
import at.aau.serg.websocketdemoapp.dto.GameData;
import at.aau.serg.websocketdemoapp.fragments.CardFragment;
import at.aau.serg.websocketdemoapp.helper.Card;
import at.aau.serg.websocketdemoapp.helper.CardType;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.services.TableViewService;

public class TableView extends AppCompatActivity {
    private final int[] playedCardViews = {R.id.tablePlayedCard1, R.id.tablePlayedCard2,
            R.id.tablePlayedCard3, R.id.tablePlayedCard4, R.id.tablePlayedCard5};
    private final int[] trickViewIDs = {R.id.tableTricksPlayer1, R.id.tableTricksPlayer2,
            R.id.tableTricksPlayer3, R.id.tableTricksPlayer4, R.id.tableTricksPlayer5};
    private final int[] playerNameViewIDs = {R.id.playerNameView2, R.id.playerNameView3, R.id.playerNameView4, R.id.playerNameView5};
    private GameData gameData;
    private DataHandler dataHandler;
    private TableViewService tableViewService;
    private boolean isResumed = false;
    private boolean pendingFragmentTransaction = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_table_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button pointsButton = findViewById(R.id.pointsViewButton);
        Button handButton = findViewById(R.id.handCardsButton);
        pointsButton.setOnClickListener(v -> pointViewClicked());
        handButton.setOnClickListener(v -> handButtonClicked());
        gameData = GameData.getInstance();
        dataHandler = DataHandler.getInstance(this);
        tableViewService = new TableViewService(this, TableView.this);
        setPlayerNames();
        displayCardsPlayed();
        tableViewService.updateTableView(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResumed = true;
        if (pendingFragmentTransaction) {
            updateUI();
            pendingFragmentTransaction = false;
        }
    }

    public void updateUI() {
        displayCardsPlayed();
        displayPlayerTricks();
    }

    @SuppressLint("DiscouragedApi")
    public void displayCardsPlayed() {
        List<Card> cardsPlayed = gameData.getCardsPlayed();
        for (int i = 0; i < cardsPlayed.size(); i++) {
            if (i >= gameData.getPlayerNames().size()) {
                break;
            }
            Card card = cardsPlayed.get(i);
            ImageView imageView = findViewById(playedCardViews[i]);
            int resId = getResources().getIdentifier(card.getImgPath(), "drawable", getPackageName());
            imageView.setImageResource(resId);
            imageView.setZ(i);
            imageView.setVisibility(View.VISIBLE);
        }
    }

    public void displayPlayerTricks() {
        if (!isResumed) {
            pendingFragmentTransaction = true;
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        HashMap<String, Map<CardType, Integer>> tricksByPlayer = gameData.getPlayerTricks();
        String devicePlayerName = dataHandler.getPlayerName();

        List<String> playerNames = new ArrayList<>(tricksByPlayer.keySet());
        playerNames.remove(devicePlayerName);
        playerNames.add(0, devicePlayerName);

        for (int i = 0; i < trickViewIDs.length; i++) {
            if (i >= playerNames.size()) {
                break;
            }
            FrameLayout container = findViewById(trickViewIDs[i]);
            container.removeAllViews();
            String playerKey = playerNames.get(i);
            Map<CardType, Integer> tricks = tricksByPlayer.get(playerKey);

            if (tricks != null) {
                int overlapPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        (-2 * tricks.size() + 45), getResources().getDisplayMetrics());

                int cardWidthPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        120, getResources().getDisplayMetrics());
                int midPoint = getDeviceWidthPx() / 2 - cardWidthPx / 2;

                int c = 1;
                for (Map.Entry<CardType, Integer> entry : tricks.entrySet()) {
                    CardType cardType = entry.getKey();
                    Integer value = entry.getValue();

                    Card card = new Card(cardType, value);
                    card.createImagePath();
                    String cardImagePath = card.getImgPath();

                    int marginLeft = midPoint + (c - Math.round(tricks.size() / 2f)) * overlapPx;
                    float rotation = (c - Math.round(tricks.size() / 2f)) * 0.75f;
                    CardFragment cardFragment = CardFragment
                            .newInstance(cardImagePath, cardWidthPx, marginLeft, rotation);
                    transaction.add(container.getId(), cardFragment, "trick_card_" + playerKey + "_" + c);
                    c++;
                }
            }
        }
        transaction.commitAllowingStateLoss();
    }

    private void setPlayerNames() {
        String devicePlayerName = dataHandler.getPlayerName();
        int nameIndex = 0;
        for (String playerName : gameData.getPlayerNames()) {
            if (!playerName.equals(devicePlayerName)) {
                TextView nameView = findViewById(playerNameViewIDs[nameIndex]);
                nameView.setText(playerName);
                nameIndex++;
            }
        }
    }

    private int getDeviceWidthPx() {
        return getResources().getDisplayMetrics().widthPixels;
    }

    public void pointViewClicked() {
        Intent intent = new Intent(TableView.this, PointsView.class);
        startActivity(intent);
    }

    public void handButtonClicked() {
        finish();
    }
}