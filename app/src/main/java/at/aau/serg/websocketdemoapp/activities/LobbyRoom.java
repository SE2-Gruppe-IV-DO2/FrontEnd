package at.aau.serg.websocketdemoapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import at.aau.serg.websocketdemoapp.R;
import at.aau.serg.websocketdemoapp.services.LobbyRoomService;

public class LobbyRoom extends AppCompatActivity {
    TextView participants;
    LobbyRoomService lobbyRoomService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lobbyroom);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button cancelButton = findViewById(R.id.buttonBreak);
        Button startButton = findViewById(R.id.buttonStart);

        participants = findViewById(R.id.participants);
        lobbyRoomService = new LobbyRoomService(this, LobbyRoom.this);
        lobbyRoomService.onCreation();
        cancelButton.setOnClickListener(v -> backButtonClicked());
        startButton.setOnClickListener(v -> startButtonClicked());
    }

    public void backButtonClicked() {
        lobbyRoomService.backButtonClicked();
    }

    public void startButtonClicked() {
        lobbyRoomService.startButtonClicked();
    }

    public void changeToMainActivity() {
        Intent intent = new Intent(LobbyRoom.this, MainActivity.class);
        startActivity(intent);
    }

    public void changeToGameActivity() {
         //For testing
        Intent intent = new Intent(LobbyRoom.this, PointsView.class);
        startActivity(intent);

    }
}