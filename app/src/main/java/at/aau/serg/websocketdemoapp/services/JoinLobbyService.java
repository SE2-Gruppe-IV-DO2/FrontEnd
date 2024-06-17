package at.aau.serg.websocketdemoapp.services;

import android.content.Context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.aau.serg.websocketdemoapp.activities.JoinLobby;
import at.aau.serg.websocketdemoapp.dto.JoinLobbyResponse;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.helper.JsonParsingException;
import at.aau.serg.websocketdemoapp.networking.StompHandler;

public class JoinLobbyService {
    private final JoinLobby joinLobbyActivity;
    private final DataHandler dataHandler;
    private final StompHandler stompHandler;
    private final ObjectMapper objectMapper;

    public JoinLobbyService(Context context, JoinLobby joinLobby) {
        stompHandler = StompHandler.getInstance();
        dataHandler = DataHandler.getInstance(context);
        objectMapper = new ObjectMapper();
        this.joinLobbyActivity = joinLobby;
    }

    public void backButtonClicked() {
        joinLobbyActivity.changeToStartActivity();
    }

    public void joinLobbyWithIDClicked(String lobbyCode) {
        dataHandler.setLobbyCode(lobbyCode);
        new Thread(() -> stompHandler.joinLobby(dataHandler.getLobbyCode(), dataHandler.getPlayerID(), dataHandler.getPlayerName(), callback -> {
            JoinLobbyResponse joinLobbyResponse;
            try {
                joinLobbyResponse = objectMapper.readValue(callback, JoinLobbyResponse.class);
            } catch (JsonProcessingException e) {
                throw new JsonParsingException("JSON PARSE", e);
            }
            dataHandler.setLobbyCode(joinLobbyResponse.getLobbyCode());
        })).start();
        joinLobbyActivity.changeToLobbyRoomActivity();
    }
}
