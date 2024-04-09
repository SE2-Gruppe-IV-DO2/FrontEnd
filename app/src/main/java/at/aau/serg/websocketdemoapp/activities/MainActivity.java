package at.aau.serg.websocketdemoapp.activities;


import android.content.Intent;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {


    EditText playerName;
    TextView errorText;

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


    public void tutorialButtonClicked() {
        Button tutorialButton = findViewById(R.id.buttonTutorial);


        tutorialButton.setOnClickListener(view -> {
                // navigation to tutorial
                Intent intent = new Intent(MainActivity.this, Tutorial.class);
                startActivity(intent);

        });
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