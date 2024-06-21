package at.aau.serg.websocketdemoapp.services;

import android.content.Context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.aau.serg.websocketdemoapp.activities.TableView;
import at.aau.serg.websocketdemoapp.dto.GameData;
import at.aau.serg.websocketdemoapp.dto.PlayerTrickResponse;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.helper.JsonParsingException;
import at.aau.serg.websocketdemoapp.networking.StompHandler;

public class TableViewService {
    private final GameData gameData;
    private final DataHandler dataHandler;
    private final StompHandler stompHandler;
    private TableView tableView;
    ObjectMapper objectMapper;

    public TableViewService(Context context, TableView tableView) {
        gameData = GameData.getInstance();
        dataHandler = DataHandler.getInstance(context);
        stompHandler = StompHandler.getInstance();
        this.tableView = tableView;
        objectMapper = new ObjectMapper();
        getPlayerTricks();
    }

    public void getPlayerTricks() {
        new Thread(() -> {
            String lobbyCode = dataHandler.getLobbyCode();
            stompHandler.getPlayerTricks(lobbyCode, response -> {
                PlayerTrickResponse playerTrickResponse;
                try {
                    playerTrickResponse = objectMapper.readValue(response, PlayerTrickResponse.class);
                    gameData.setPlayerTricks(playerTrickResponse.getPlayerTricks());
                    tableView.runOnUiThread(() -> tableView.updateUI());
                } catch (JsonProcessingException e) {
                    throw new JsonParsingException("JSON PARSE", e);
                }
            });
        }).start();
    }

    public void updateTableView(TableView tableView) {
        this.tableView = tableView;
    }
}
