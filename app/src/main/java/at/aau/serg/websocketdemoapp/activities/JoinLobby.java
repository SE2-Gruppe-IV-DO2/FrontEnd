package at.aau.serg.websocketdemoapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import at.aau.serg.websocketdemoapp.R;
import at.aau.serg.websocketdemoapp.services.JoinLobbyService;

public class JoinLobby extends AppCompatActivity {
    JoinLobbyService joinLobbyService;

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

        joinLobbyService = new JoinLobbyService(this, JoinLobby.this);
    }

    public void backButtonClicked(View view) {
        joinLobbyService.backButtonClicked();
    }

    public void changeToStartActivity() {
        Intent intent = new Intent(JoinLobby.this, MainActivity.class);
        startActivity(intent);
    }
}