package at.aau.serg.websocketdemoapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import at.aau.serg.websocketdemoapp.activities.CheatingAccusationActivity;
import at.aau.serg.websocketdemoapp.dto.CheatingAccusationRequest;
import at.aau.serg.websocketdemoapp.dto.GetPlayersInLobbyMessage;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.helper.JsonParsingException;
import at.aau.serg.websocketdemoapp.networking.StompHandler;
import at.aau.serg.websocketdemoapp.services.CheatingAccusationService;

@RunWith(RobolectricTestRunner.class)
class CheatingAccusationServiceTest {

    @Mock
    Context context;
    @Mock
    CheatingAccusationActivity mockCheatingAccusationActivity;
    @Mock
    DataHandler mockDataHandler;
    @Mock
    StompHandler mockStompHandler;
    @Mock
    ObjectMapper mockObjectMapper;

    private CheatingAccusationService cheatingAccusationService;
    private ObjectMapper objectMapper;
    @Captor
    private ArgumentCaptor<Consumer<String>> responseHandlerCaptor;

    private static final String PLAYER_ID = "playerId";
    private static final String LOBBY_CODE = "lobbyCode";
    private static final String PLAYER_NAME = "playerName";

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        when(mockDataHandler.getPlayerID()).thenReturn(PLAYER_ID);
        when(mockDataHandler.getLobbyCode()).thenReturn(LOBBY_CODE);

        StompHandler.setInstance(mockStompHandler);
        DataHandler.setInstance(mockDataHandler);

        cheatingAccusationService = new CheatingAccusationService(context, mockCheatingAccusationActivity);
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() {
        cheatingAccusationService = null;
    }

    @Test
    void testGetPlayers() throws Exception {
        // Arrange
        CountDownLatch latch = new CountDownLatch(1);

        doAnswer(invocation -> {
            Consumer<String> callback = invocation.getArgument(1);
            GetPlayersInLobbyMessage message = new GetPlayersInLobbyMessage();
            Map<String, String> playerMap = new HashMap<>();
            playerMap.put("player1", "Player One");
            playerMap.put("player2", "Player Two");
            message.setPlayerNamesAndIds(playerMap);
            callback.accept(new ObjectMapper().writeValueAsString(message));
            latch.countDown();
            return null;
        }).when(mockStompHandler).getPlayersInLobbyMessage(any(), any());

        // Act
        cheatingAccusationService.getPlayers();

        // Wait for the asynchronous operation to complete
        latch.await();

        // Assert
        verify(mockStompHandler).getPlayersInLobbyMessage(any(), any());
    }

    @Test
    void testHandlePlayersToView() throws JsonProcessingException {
        // Arrange
        GetPlayersInLobbyMessage message = new GetPlayersInLobbyMessage();
        Map<String, String> playerNamesAndIds = new HashMap<>();
        playerNamesAndIds.put(PLAYER_ID, PLAYER_NAME);
        message.setPlayerNamesAndIds(playerNamesAndIds);
        String messageJson = objectMapper.writeValueAsString(message);

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run(); // Immediately run the passed Runnable
            return null;
        }).when(mockCheatingAccusationActivity).runOnUiThread(any(Runnable.class));

        // Act
        cheatingAccusationService.handlePlayersToView(messageJson);

        // Assert
        verify(mockCheatingAccusationActivity).updateUI();
        assertEquals(playerNamesAndIds, cheatingAccusationService.getPlayerNamesAndIds());
    }

    @Test
    void testHandlePlayersToViewException() throws JsonProcessingException {
        // Arrange
        String invalidJson = "invalidJson";
        when(mockObjectMapper.readValue(invalidJson, GetPlayersInLobbyMessage.class)).thenThrow(JsonProcessingException.class);

        // Assert
        assertThrows(JsonParsingException.class, () -> cheatingAccusationService.handlePlayersToView(invalidJson));
    }



    @Test
    void testHandleCheatingResult() throws JsonProcessingException {
        // Arrange
        CheatingAccusationRequest request = new CheatingAccusationRequest();
        request.setUserID(PLAYER_ID);
        request.setCorrectAccusation(true);
        String json = new ObjectMapper().writeValueAsString(request);

        when(mockObjectMapper.readValue(json, CheatingAccusationRequest.class)).thenReturn(request);

        // Act
        cheatingAccusationService.handleCheatingResult(json);

        // Assert
        verify(mockCheatingAccusationActivity).showCheatingAccusationResult(true);
    }

    @Test
    void testHandleCheatingResultException() throws JsonProcessingException {
        // Arrange
        String invalidJson = "invalidJson";
        when(mockObjectMapper.readValue(invalidJson, CheatingAccusationRequest.class)).thenThrow(JsonProcessingException.class);

        // Assert
        assertThrows(JsonParsingException.class, () -> cheatingAccusationService.handleCheatingResult(invalidJson));
    }
}