package at.aau.serg.websocketdemoapp.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import at.aau.serg.websocketdemoapp.R;
import at.aau.serg.websocketdemoapp.activities.LobbyRoom;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.networking.StompHandler;

public class LobbyRoomService {
    private final LobbyRoom lobbyActivity;
    private StompHandler stompHandler;
    private final TextView participants;
    private final DataHandler dataHandler;
    private final TextView lobbyCodeTextfield;

    private BarcodeEncoder mEncoder;

    public LobbyRoomService(Context context, LobbyRoom activity) {
        dataHandler = DataHandler.getInstance(context);
        stompHandler = StompHandler.getInstance();
        this.lobbyActivity = activity;
        participants = lobbyActivity.findViewById(R.id.participants);
        lobbyCodeTextfield = lobbyActivity.findViewById(R.id.lobbyCode);

        initGameStartSubscription();
    }

    public LobbyRoomService(DataHandler handler, LobbyRoom activity) {
        dataHandler = handler;
        stompHandler = StompHandler.getInstance();
        this.lobbyActivity = activity;
        participants = lobbyActivity.findViewById(R.id.participants);
        lobbyCodeTextfield = lobbyActivity.findViewById(R.id.lobbyCode);

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
        participants.append(dataHandler.getPlayerName() + "\n");
    }

    public void setLobbyCode() {
        lobbyCodeTextfield.setText(dataHandler.getLobbyCode());
    }

    public void onCreation() {
        setPlayerName();
        setLobbyCode();
        createLobbyQRCode(dataHandler.getLobbyCode());
    }

    public void setStompHandler(StompHandler stompHandler) {
        this.stompHandler = stompHandler;
    }

    public void initGameStartSubscription() {
        this.stompHandler.initGameStartSubscription(this.lobbyActivity);
    }

    public void startGame() {
        // Fügt 2 virtuelle Spieler zur Lobby um starten zu können
        //stompHandler.joinLobby(dataHandler.getLobbyCode(), "Test1", "test1", callback -> {
        //});
        //stompHandler.joinLobby(dataHandler.getLobbyCode(), "Test2", "test2", callback -> {
        //});
        this.stompHandler.startGameForLobby(this.dataHandler.getLobbyCode());
    }

    private void createLobbyQRCode(String lobbyCode) {
        if (mEncoder == null)
            mEncoder = new BarcodeEncoder();

        MultiFormatWriter mWriter = new MultiFormatWriter();

        try {
            Log.d("lobbyCode", lobbyCode);
            BitMatrix mMatrix = mWriter.encode(lobbyCode, BarcodeFormat.QR_CODE, 250, 250);
            Bitmap mBitmap = mEncoder.createBitmap(mMatrix);

            // Change the color of the background (the white was not the right white...)
            for (int x = 0; x < mBitmap.getWidth(); x++) {
                for (int y = 0; y < mBitmap.getHeight(); y++) {
                    // Get the pixel color at (x, y)
                    int pixel = mBitmap.getPixel(x, y);
                    // Check if the pixel is white
                    if (pixel == Color.WHITE) {
                        // Change it to your desired background color
                        mBitmap.setPixel(x, y, ContextCompat.getColor(lobbyActivity, com.google.android.material.R.color.cardview_light_background));
                    }
                }
            }

            try {
                // Getting QR-Code as Bitmap
                ImageView qrCodeImageView = lobbyActivity.findViewById(R.id.qrCode);
                qrCodeImageView.setImageBitmap(mBitmap);

            } catch (Exception e) {
                System.out.println(e.toString());
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
