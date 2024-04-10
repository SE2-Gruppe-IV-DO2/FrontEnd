package at.aau.serg.websocketdemoapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import at.aau.serg.websocketdemoapp.R;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class MainActivity extends AppCompatActivity {
    EditText playerName;
    TextView errorText;
    StompClient stompClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://10.0.2.2:8080/websocket-example-handler");
        stompClient.connect();

        errorText = findViewById(R.id.labelError);
        playerName = findViewById(R.id.playerName);

        Button view = findViewById(R.id.buttonTutorial);
        view.setOnClickListener(c -> {
            new Thread(() -> {
                final String[] result = {"test"};
                try {
                    stompClient.topic("/topic/hello-response").subscribe(stompMessage -> {
                        Log.v("Resposne", stompMessage.getPayload());
                        result[0] = stompMessage.getPayload().toString();
                    });
                    
                    stompClient.send("/app/hello", "Hello, Server!");

                    runOnUiThread(() -> {
                        playerName.setText(result[0].toString());
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        });

        createButtonClicked();
        joinButtonClicked();
    }

    public void createButtonClicked(){
        Button createButton = findViewById(R.id.buttonCreate);
        createButton.setOnClickListener(view -> {
            if (!playerName.getText().toString().isEmpty()) {
                // navigation to create lobbyroom site
                Intent intent = new Intent(MainActivity.this, Lobbyroom.class);
                intent.putExtra("playerName", "");
                intent.putExtra("lobbyCode", "");
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
}