package at.aau.serg.websocketdemoapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import at.aau.serg.websocketdemoapp.R;

public class JoinLobby extends AppCompatActivity {


    EditText enterLobbyCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_join_lobby);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        enterLobbyCode = findViewById(R.id.enterLobbyCode);
        cancelLobby2();
    }

    public void cancelLobby2() {
        Button breakButton2 = findViewById(R.id.buttonCancel2);


        breakButton2.setOnClickListener(view -> {
            // navigation to the start site
            Intent intent = new Intent(JoinLobby.this,MainActivity.class);
            startActivity(intent);
        });


    }
}