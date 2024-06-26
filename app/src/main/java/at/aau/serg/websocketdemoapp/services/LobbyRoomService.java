package at.aau.serg.websocketdemoapp.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.List;

import at.aau.serg.websocketdemoapp.R;
import at.aau.serg.websocketdemoapp.activities.LobbyRoom;
import at.aau.serg.websocketdemoapp.dto.GetPlayersInLobbyMessage;
import at.aau.serg.websocketdemoapp.dto.StartGameResponse;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.helper.JsonParsingException;
import at.aau.serg.websocketdemoapp.networking.StompHandler;
import lombok.Getter;

public class LobbyRoomService {
    private final LobbyRoom lobbyActivity;
    private StompHandler stompHandler;

    @Getter
    private final TextView participants;
    private final DataHandler dataHandler;
    private final TextView lobbyCodeTextfield;
    private final ObjectMapper objectMapper = new ObjectMapper();

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

        getPlayersInLobby();

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

    public void setLobbyCode() {
        lobbyCodeTextfield.setText(dataHandler.getLobbyCode());
    }

    public void onCreation() {
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
        this.stompHandler.initGameStartSubscription(dataHandler.getLobbyCode(), this::handleStartGameResponse);
    }

    private void handleStartGameResponse(String data) {
        StartGameResponse startGameResponse;
        try {
            startGameResponse = objectMapper.readValue(data, StartGameResponse.class);
        } catch (JsonProcessingException e) {
            throw new JsonParsingException("JSON PARSE", e);
        }

        if (startGameResponse.getResponse().equals("Game started!")) {
            lobbyActivity.changeToGameActivity();
        }
    }

    public void addPlayerNameToLobby(String playerName) {
        participants.append(playerName + "\n");
    }

    public void addPlayerNamesToLobby(List<String> playerNames) {
        lobbyActivity.clearParticipantView();
        for (String playerName : playerNames) {
            addPlayerNameToLobby(playerName);
        }
    }

    public void initPlayerJoinedLobbySubscription() {
        this.stompHandler.subscribeForPlayerJoinedLobbyEvent(dataHandler.getLobbyCode(), this::addPlayerNameToLobby);
    }

    public void getPlayersInLobby() {
        this.stompHandler.getPlayersInLobbyMessage(dataHandler.getLobbyCode(), response -> new Handler(Looper.getMainLooper()).post(() -> {
            getPlayersInLobbyWithResponse(response);
        }));
    }

    public void getPlayersInLobbyWithResponse(String response) {
        GetPlayersInLobbyMessage playersInLobby;
        try {
            playersInLobby = objectMapper.readValue(response, GetPlayersInLobbyMessage.class);
        } catch (JsonProcessingException e) {
            throw new JsonParsingException("Failed to parse JSON response", e);
        }
        List<String> playerNames = playersInLobby.getPlayerNames();
        addPlayerNamesToLobby(playerNames);
    }

    public void startGame() {
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
