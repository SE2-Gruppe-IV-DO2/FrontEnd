package at.aau.serg.websocketdemoapp.activities;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import at.aau.serg.websocketdemoapp.R;
import at.aau.serg.websocketdemoapp.networking.MessageHandler;
import at.aau.serg.websocketdemoapp.networking.WebSocketClient;
import at.aau.serg.websocketdemoapp.networking.WebSocketMessageHandler;

public class MainActivity extends AppCompatActivity {
    EditText playerName;
    TextView errorText;
    WebSocketClient webSocketClient;
    WebSocketMessageHandler<String> handler;

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

        webSocketClient = new WebSocketClient("ws://10.0.2.2:8080/websocket-example-handler");
        handler = new MessageHandler();
        webSocketClient.connectToServer(handler);

        errorText = findViewById(R.id.labelError);
        playerName = findViewById(R.id.playerName);

        createButtonClicked();
        joinButtonClicked();
    }

    public void createButtonClicked(){
        Button createButton = findViewById(R.id.buttonCreate);
        createButton.setOnClickListener(view -> {
            if (!playerName.getText().toString().isEmpty()) {
                // navigation to create lobbyroom site
                Intent intent = new Intent(MainActivity.this, Lobbyroom.class);

                try {
                    webSocketClient.sendMessageToServer("Hello, Server!");
                } catch(Exception e) {
                    Log.e("WebSocket", e.getMessage());
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                intent.putExtra("playerName", playerName.getText().toString());
                intent.putExtra("lobbyCode", handler.getValue());
                startActivity(intent);
            }else {
                errorText.setVisibility(View.VISIBLE);
            }
        });

    }

    public void joinButtonClicked() {
        Button joinButton = findViewById(R.id.buttonJoin);
        joinButton.setOnClickListener(view -> {
            if (!playerName.getText().toString().isEmpty()) {
                // navigation to create lobbyroom site
                Intent intent = new Intent(MainActivity.this, JoinLobby.class);
                intent.putExtra("playerName", playerName.getText().toString());
                startActivity(intent);

            }else {
                errorText.setVisibility(View.VISIBLE);
            }
        });
    }


    public void tutorialButtonClicked(View view) {
        // Tutorial Button Click handling hier
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Disconnect from the WebSocket server when the activity is destroyed
        try {
            webSocketClient.disconnect();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

/*
    private void connectToWebSocketServer() {
        // register a handler for received messages when setting up the connection
        networkHandler.connectToServer(this::messageReceivedFromServer);
    }

    private void sendMessage() {
        networkHandler.sendMessageToServer("test message");
    }

    private void messageReceivedFromServer(String message) {
        // TODO handle received messages
        Log.d("Network", message);
        textViewServerResponse.setText(message);
    }
 */
}