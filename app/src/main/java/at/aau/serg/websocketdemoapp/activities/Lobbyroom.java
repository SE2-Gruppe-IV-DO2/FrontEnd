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
import at.aau.serg.websocketdemoapp.networking.StompHandler;

public class Lobbyroom extends AppCompatActivity {

    TextView lobbyCode;
    TextView participants;
    final StompHandler stompHandler = new StompHandler("ws://10.0.2.2:8080/websocket-example-broker");

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
        Intent intent = getIntent();
        createLobbyCode();

        stompHandler.connectToServer();
        lobbyCode = findViewById(R.id.lobbyCode);
        participants = findViewById(R.id.participants);
        participants.setText(intent.getStringExtra("playerName") + "\n");
        showParticipants();
        cancelLobby();
    }

    public void createLobbyCode(){
        new Thread(() -> {
            stompHandler.createLobby("TEST", "USER_NAME", code -> {
                runOnUiThread(() -> {
                    lobbyCode.setText(code);
                });
            });
        }).start();
    }

    //Show the Participants
    public void showParticipants() {
        String playerName=getIntent().getStringExtra("playerName");
        //toDo die weiteren Mitspieler zeigen
        //participants.append(...);
    }

    public void cancelLobby() {
        Button breakButton = findViewById(R.id.buttonBreak);
        breakButton.setOnClickListener(view -> {
            // navigation to the start site
            Intent intent = new Intent(Lobbyroom.this,MainActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stompHandler.disconnect();
    }
}