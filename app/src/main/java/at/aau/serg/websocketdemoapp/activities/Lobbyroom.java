package at.aau.serg.websocketdemoapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
        Button breakButton = findViewById(R.id.buttonBreak);


        breakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // navigation zur nächsten Seite
                Intent intent = new Intent(Lobbyroom.this,MainActivity.class);
                startActivity(intent);
            }
        });
        showParticipants();
    }
    /*
    public void createLobbyCode(){
        //toDo Lobbycode vom Server abfragen
        //lobbyCode.setText(...);

    }*/

    public void showParticipants() {
        String playerName=getIntent().getStringExtra("playerName");
        participants.setText(playerName);
        //toDo die weiteren Mitspieler zeigen
        //participants.append(...);
    }

}