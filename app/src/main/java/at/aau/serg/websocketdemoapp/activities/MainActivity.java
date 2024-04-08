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

        Button createButton = findViewById(R.id.buttonCreate);




            createButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!playerName.getText().toString().isEmpty()) {
                    // navigation to create lobbyroom site
                    Intent intent = new Intent(MainActivity.this, Lobbyroom.class);
                    intent.putExtra("playerName", playerName.getText().toString());
                    startActivity(intent);

        }else {
            errorText.setText("Input invalid!");
            errorText.setVisibility(View.VISIBLE);
        }
                }
            });
    }



    public void joinGameButtonClicked(View view) {
        // Join Game Button Click handling here
    }

    public void tutorialButtonClicked(View view) {
        // Tutorial Button Click handling hier
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