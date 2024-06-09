package at.aau.serg.websocketdemoapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import at.aau.serg.websocketdemoapp.R;
import at.aau.serg.websocketdemoapp.services.MainActivityService;

public class MainActivity extends AppCompatActivity {
    private MainActivityService mainActivityService;
    private EditText editText;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button createGameButton = findViewById(R.id.buttonCreate);
        Button joinGameButton = findViewById(R.id.buttonJoin);
        Button tutorialButton = findViewById(R.id.buttonTutorial);
        editText = findViewById(R.id.playerName);
        textView = findViewById(R.id.labelError);
        mainActivityService = new MainActivityService(this, MainActivity.this);

        createGameButton.setOnClickListener(v -> createGameButtonClicked());
        joinGameButton.setOnClickListener(v -> joinGameButtonClicked());
        tutorialButton.setOnClickListener(v -> tutorialButtonClicked());
    }

    public void setPlayerNameView(String playerName) {
        editText.setText(playerName);
    }

    public void createGameButtonClicked() {
        Log.v("Create Game", "Button Clicked");
        // Create Game Button Click handling here
        mainActivityService.createGameService(editText, textView);
    }

    public void joinGameButtonClicked() {
        // Join Game Button Click handling here
        mainActivityService.joinGameService(editText, textView);
    }

    public void tutorialButtonClicked() {
        // Tutorial Button Click handling here
        mainActivityService.tutorialService();
    }

    public void changeToCreateActivity() {
        Intent intent = new Intent(MainActivity.this, LobbyRoom.class);
        startActivity(intent);
    }

    public void changeToJoinActivity() {
        Intent intent = new Intent(MainActivity.this, JoinLobby.class);
        startActivity(intent);
    }

    public void changeToTutorialActivity() {
        Intent intent = new Intent(MainActivity.this, Tutorial.class);
        startActivity(intent);
    }
}