package at.aau.serg.websocketdemoapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import at.aau.serg.websocketdemoapp.R;
import at.aau.serg.websocketdemoapp.services.JoinLobbyService;

public class JoinLobby extends AppCompatActivity implements View.OnClickListener {
    private JoinLobbyService joinLobbyService;

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
        Button joinLobbyIDButton = findViewById(R.id.buttonEnterLobby);
        Button cancelButton = findViewById(R.id.buttonCancel2);

        joinLobbyIDButton.setOnClickListener(v -> JoinLobby.this.joinLobbyWithIDButtonClicked());
        cancelButton.setOnClickListener(v -> JoinLobby.this.backButtonClicked());

        Button scanBtn = findViewById(R.id.buttonEnterLobbyQR);
        scanBtn.setOnClickListener(this);
    }

    public void joinLobbyWithIDButtonClicked() {
        TextView lobbyTextField = findViewById(R.id.enterLobbyCode);
        String lobbyCode = lobbyTextField.getText().toString();
        joinLobbyService.joinLobbyWithIDClicked(lobbyCode);
    }

    public void backButtonClicked() {
        joinLobbyService.backButtonClicked();
    }

    public void changeToStartActivity() {
        Intent intent = new Intent(JoinLobby.this, MainActivity.class);
        startActivity(intent);
    }

    public void changeToLobbyRoomActivity() {
        Intent intent = new Intent(JoinLobby.this, LobbyRoom.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setPrompt("Scan a barcode or QR Code");
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (intentResult != null) {
            if (intentResult.getContents() != null) {
                String qrCode = intentResult.getContents();
                TextView lobbyTextField = findViewById(R.id.enterLobbyCode);
                lobbyTextField.setText(qrCode);
                joinLobbyWithIDButtonClicked();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}