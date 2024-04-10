package at.aau.serg.websocketdemoapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import at.aau.serg.websocketdemoapp.R;
import at.aau.serg.websocketdemoapp.networking.StompHandler;
import ua.naiksoftware.stomp.StompClient;

public class MainActivity extends AppCompatActivity {
    EditText playerName;
    TextView errorText;
    StompClient stompClient;

    StompHandler stompHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stompHandler = new StompHandler("ws://10.0.2.2:8080/websocket-example-broker");
        stompHandler.connectToServer();

        errorText = findViewById(R.id.labelError);
        playerName = findViewById(R.id.playerName);

        createButtonClicked();
        joinButtonClicked();
        tutorialButtonClicked();
    }

    public void createButtonClicked(){
        Button createButton = findViewById(R.id.buttonCreate);
        createButton.setOnClickListener(view -> {
            if (!playerName.getText().toString().isEmpty()) {
                // navigation to create lobbyroom site
                Intent intent = new Intent(MainActivity.this, Lobbyroom.class);
                intent.putExtra("playerName", playerName.getText().toString());
                startActivity(intent);
            } else {
                errorText.setVisibility(View.VISIBLE);
            }
        });
    }

    public void joinButtonClicked() {
        Button joinButton = findViewById(R.id.buttonJoin);
        joinButton.setOnClickListener(view -> {
            if (!playerName.getText().toString().isEmpty()) {
                // navigation to join lobbyroom site
                Intent intent = new Intent(MainActivity.this, JoinLobby.class);
                intent.putExtra("playerName", playerName.getText().toString());
                startActivity(intent);
            } else {
                errorText.setVisibility(View.VISIBLE);
            }
        });
    }

    public void tutorialButtonClicked() {
        Button tutorialButton = findViewById(R.id.buttonTutorial);
        tutorialButton.setOnClickListener(view -> {

        });
    }
}