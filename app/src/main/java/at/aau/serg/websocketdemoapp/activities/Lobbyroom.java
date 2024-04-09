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

public class Lobbyroom extends AppCompatActivity {

    TextView lobbyCode;
    TextView participants;

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

        lobbyCode = findViewById(R.id.lobbyCode);
        participants = findViewById(R.id.participants);
        showParticipants();
        cancelLobby();
    }
    /*
    public void createLobbyCode(){
        //toDo Lobbycode vom Server abfragen
        //lobbyCode.setText(...);

    }*/

    //Show the Participants
    public void showParticipants() {
        String playerName=getIntent().getStringExtra("playerName");
        participants.append(playerName + "\n");
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

}