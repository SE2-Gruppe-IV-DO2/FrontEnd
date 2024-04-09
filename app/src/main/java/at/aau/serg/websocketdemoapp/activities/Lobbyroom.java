package at.aau.serg.websocketdemoapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import at.aau.serg.websocketdemoapp.R;
import at.aau.serg.websocketdemoapp.services.LobbyRoomService;

public class Lobbyroom extends AppCompatActivity {
    TextView lobbyCode;
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
        participants = findViewById(R.id.participants);
        lobbyRoomService = new LobbyRoomService(this, Lobbyroom.this);
        lobbyRoomService.setPlayerName(participants);
    }

    public void backButtonClicked(View view) {
        lobbyRoomService.backButtonClicked();
    }

    public void changeToStartActivity() {
        Intent intent = new Intent(Lobbyroom.this, MainActivity.class);
        startActivity(intent);
    }
}