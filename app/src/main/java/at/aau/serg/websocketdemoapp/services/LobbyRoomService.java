package at.aau.serg.websocketdemoapp.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import at.aau.serg.websocketdemoapp.R;
import at.aau.serg.websocketdemoapp.activities.LobbyRoom;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.networking.StompHandler;
import lombok.Getter;

public class LobbyRoomService {
    private final LobbyRoom lobbyActivity;
    private StompHandler stompHandler;

    @Getter
    private final TextView participants;
    private final DataHandler dataHandler;
    private final TextView lobbyCodeTextfield;

    private BarcodeEncoder mEncoder;

    public LobbyRoomService(Context context, LobbyRoom activity) {
        this(DataHandler.getInstance(context), activity);
    }

    public LobbyRoomService(DataHandler handler, LobbyRoom activity) {
        dataHandler = handler;
        stompHandler = StompHandler.getInstance();
        this.lobbyActivity = activity;
        participants = lobbyActivity.findViewById(R.id.participants);
        lobbyCodeTextfield = lobbyActivity.findViewById(R.id.lobbyCode);

        initPlayerJoinedLobbySubscription();
        initGameStartSubscription();
    }

    public void setmEncoder(BarcodeEncoder mEncoder) {
        this.mEncoder = mEncoder;
    }

    public void backButtonClicked() {
        lobbyActivity.changeToMainActivity();
    }

    public void startButtonClicked() {this.startGame();}

    private void setPlayerName() {
        addPlayerNameToLobby(dataHandler.getPlayerName());
    }

    public void setLobbyCode() {
        lobbyCodeTextfield.setText(dataHandler.getLobbyCode());
    }

    public void onCreation() {
        setPlayerName();
        setLobbyCode();
        try {
            createLobbyQRCode(dataHandler.getLobbyCode());
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

    public void setStompHandler(StompHandler stompHandler) {
        this.stompHandler = stompHandler;
    }

    public void initGameStartSubscription() {
        this.stompHandler.initGameStartSubscription(this.lobbyActivity);
    }

    public void addPlayerNameToLobby(String playerName) {
        participants.append(playerName + "\n");
    }

    public void initPlayerJoinedLobbySubscription() {
        this.stompHandler.subscribeForPlayerJoinedLobbyEvent(this::addPlayerNameToLobby);
    }

    public void startGame() {
        // Fügt 2 virtuelle Spieler zur Lobby um starten zu können
        //stompHandler.joinLobby(dataHandler.getLobbyCode(), "Test1", "test1", callback -> {
        //});
        //stompHandler.joinLobby(dataHandler.getLobbyCode(), "Test2", "test2", callback -> {
        //});
        this.stompHandler.startGameForLobby(this.dataHandler.getLobbyCode());
    }

    public void createLobbyQRCode(String lobbyCode) throws WriterException {
        if (mEncoder == null)
            mEncoder = new BarcodeEncoder();

        MultiFormatWriter mWriter = new MultiFormatWriter();

        BitMatrix mMatrix = mWriter.encode(lobbyCode, BarcodeFormat.QR_CODE, 250, 250);
        Bitmap mBitmap = mEncoder.createBitmap(mMatrix);

        // Getting QR-Code as Bitmap
        ImageView qrCodeImageView = lobbyActivity.findViewById(R.id.qrCode);
        qrCodeImageView.setImageBitmap(mBitmap);
    }
}
