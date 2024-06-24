package at.aau.serg.websocketdemoapp.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import at.aau.serg.websocketdemoapp.R;
import at.aau.serg.websocketdemoapp.activities.CheatingAccusationActivity;
import at.aau.serg.websocketdemoapp.dto.CheatingAccusationRequest;
import at.aau.serg.websocketdemoapp.dto.GetPlayersInLobbyMessage;
import at.aau.serg.websocketdemoapp.dto.PointsResponse;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.helper.JsonParsingException;
import at.aau.serg.websocketdemoapp.networking.StompHandler;
import lombok.Getter;

public class CheatingAccusationService {

    private final CheatingAccusationActivity cheatingAccusationActivity;
    private final DataHandler dataHandler;
    private final StompHandler stompHandler;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Getter
    private Map<String, String> playerNamesAndIds = new HashMap<>();

    public CheatingAccusationService(Context context, CheatingAccusationActivity cheatingAccusationActivity) {
        this.cheatingAccusationActivity = cheatingAccusationActivity;
        dataHandler = DataHandler.getInstance(context);
        stompHandler = StompHandler.getInstance();
    }

    public void getPlayers() {
        new Thread(() -> this.stompHandler.getPlayersInLobbyMessage(dataHandler.getLobbyCode(), this::handlePlayersToView)).start();
    }

    public void handlePlayersToView(String data) {
        GetPlayersInLobbyMessage getPlayersInLobbyMessage;
        try {
            getPlayersInLobbyMessage = objectMapper.readValue(data, GetPlayersInLobbyMessage.class);
        } catch (JsonProcessingException e) {
            throw new JsonParsingException("JSON Parse Exception", e);
        }
        playerNamesAndIds = getPlayersInLobbyMessage.getPlayerNamesAndIds();
        cheatingAccusationActivity.runOnUiThread(cheatingAccusationActivity::updateUI);
    }

    public void onCheatingAccusationButtonClicked(String accusedPlayerId) {
        new Thread(() -> this.stompHandler.sendCheatingAccusation(dataHandler.getLobbyCode(), dataHandler.getPlayerID(), accusedPlayerId, this::handleCheatingResult)).start();
    }

    public void handleCheatingResult(String data) {
        CheatingAccusationRequest cheatingAccusationRequest;
        try{
            cheatingAccusationRequest = objectMapper.readValue(data, CheatingAccusationRequest.class);
        }catch (JsonProcessingException e) {
            throw new JsonParsingException("JSON Parse Exception", e);
        }

        if (cheatingAccusationRequest != null &&  cheatingAccusationRequest.getAccusedUserId() != null && !cheatingAccusationRequest.getAccusedUserId().isEmpty()) {
            cheatingAccusationActivity.showCheatingAccusationResult(cheatingAccusationRequest.isCorrectAccusation());
        }
        else {
            cheatingAccusationActivity.finishActivity();
        }
    }
}
