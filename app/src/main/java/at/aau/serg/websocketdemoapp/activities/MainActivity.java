package at.aau.serg.websocketdemoapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
        editText = findViewById(R.id.playerName);
        textView = findViewById(R.id.labelError);
        mainActivityService = new MainActivityService(this, MainActivity.this);
    }

    public void createGameButtonClicked(View view) {
        Log.v("Create Game", "Button Clicked");
        // Create Game Button Click handling here
        mainActivityService.createGameService(editText, textView);
    }

    public void joinGameButtonClicked(View view) {
        // Join Game Button Click handling here
        mainActivityService.joinGameService(editText, textView);
    }

    public void tutorialButtonClicked(View view) {
        // Tutorial Button Click handling here
        mainActivityService.tutorialService(editText, textView);
    }

    public void changeToCreateActivity() {
        Intent intent = new Intent(MainActivity.this, Lobbyroom.class);
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