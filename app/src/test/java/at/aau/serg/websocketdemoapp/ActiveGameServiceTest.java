package at.aau.serg.websocketdemoapp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import at.aau.serg.websocketdemoapp.activities.ActiveGame;
import at.aau.serg.websocketdemoapp.dto.CardPlayedRequest;
import at.aau.serg.websocketdemoapp.dto.GameData;
import at.aau.serg.websocketdemoapp.helper.Card;
import at.aau.serg.websocketdemoapp.helper.CardType;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.networking.StompHandler;
import at.aau.serg.websocketdemoapp.services.ActiveGameService;

class ActiveGameServiceTest {
    @Mock
    Context context;
    @Mock
    private DataHandler mockDataHandler;
    @Mock
    private ActiveGame mockActiveGame;
    @Mock
    private StompHandler mockStompHandler;
    @Mock
    private Context mockContext;
    @Mock
    SharedPreferences sharedPreferences;
    @Mock
    GameData mockGameData;
    @Mock
    ObjectMapper mockObjectMapper;
    @InjectMocks
    private ActiveGameService activeGameService;
    @Captor
    private ArgumentCaptor<Consumer<String>> responseHandlerCaptor;
    private Gson gson;
    private static final String PLAYER_ID = "playerId";
    private static final String LOBBY_CODE = "lobbyCode";
    private static final String PLAYER_NAME = "playerName";
    private static final String GAME_DATA = "gameData";

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        when(sharedPreferences.edit()).thenReturn(mock(SharedPreferences.Editor.class));
        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences);

        when(mockDataHandler.getPlayerID()).thenReturn(PLAYER_ID);
        when(mockDataHandler.getLobbyCode()).thenReturn(LOBBY_CODE);
        when(mockDataHandler.getPlayerName()).thenReturn(PLAYER_NAME);
        when(mockDataHandler.getGameData()).thenReturn(GAME_DATA);
        StompHandler.setInstance(mockStompHandler);
        DataHandler.setInstance(mockDataHandler);
        activeGameService = new ActiveGameService(context, mockActiveGame, mockGameData);
        gson = new Gson();
    }

    @AfterEach
    public void tearDown() {
        activeGameService = null;
        gson = null;
    }

    @Test
    void testPlayerChangeSubscription_ActivePlayer() {
        // Arrange
        doAnswer(invocation -> {
            Consumer<String> callback = invocation.getArgument(0);
            callback.accept(PLAYER_ID); // Simulate server response that the player is active
            return null;
        }).when(mockStompHandler).subscribeForPlayerChangedEvent(any());

        // Assert
        //verify(mockActiveGame, times(1)).updateActivePlayerInformation(PLAYER_NAME);
        //assert (activeGameService.isCurrentlyActivePlayer());
    }

    @Test
    void testPlayerChangeSubscription_InactivePlayer() {
        // Arrange
        doAnswer(invocation -> {
            Consumer<String> callback = invocation.getArgument(0);
            callback.accept("otherPlayerId"); // Simulate server response that the player is inactive
            return null;
        }).when(mockStompHandler).subscribeForPlayerChangedEvent(any());

        // Assert
        assert (!activeGameService.isCurrentlyActivePlayer());
    }

    @Test
    void testSetActivePlayer_ActivePlayer() {
        activeGameService.setActivePlayer(PLAYER_ID);

        assertTrue(activeGameService.isCurrentlyActivePlayer());
    }

    @Test
    void testSetActivePlayer_InactivePlayer() {
        activeGameService.setActivePlayer("player2");

        assertFalse(activeGameService.isCurrentlyActivePlayer());
    }

    @Test
    void testOnCardFling() {
        String cardName = "testCard";
        Card mockCard = new Card(CardType.RED, 5);
        List<Card> cardList = new ArrayList<>();
        cardList.add(mockCard);
        when(mockGameData.findCardByCardName(anyString())).thenReturn(mockCard);
        when(mockGameData.getCardList()).thenReturn(cardList);

        activeGameService.onCardFling(cardName);

        verify(mockGameData).findCardByCardName(cardName);
        assertEquals(0, cardList.size());
        verify(mockActiveGame).refreshActiveGame();
    }

    @Test
    void testHandlePlayCardResponse() throws JsonProcessingException {
        CardPlayedRequest cardPlayedRequest = new CardPlayedRequest();
        cardPlayedRequest.setColor("red");
        cardPlayedRequest.setValue("5");
        Card expectedCard = new Card(CardType.RED, 5);
        List<Card> cardList = new ArrayList<>();

        when(mockObjectMapper.readValue(gson.toJson(cardPlayedRequest), CardPlayedRequest.class)).thenReturn(cardPlayedRequest);
        when(mockGameData.getCardsPlayed()).thenReturn(cardList);

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(mockActiveGame).runOnUiThread(any(Runnable.class));

        activeGameService.handlePlayCardResponse(gson.toJson(cardPlayedRequest));

        assertEquals(1, cardList.size());
        assertEquals(expectedCard.getValue(), cardList.get(0).getValue());
        assertEquals(expectedCard.getColor(), cardList.get(0).getColor());
        verify(mockActiveGame).displayCardsPlayed();
        verify(mockGameData).getCardsPlayed();
    }

    @Test
    void testPlayCard() {
        String color = "red";
        int value = 5;
        String lobbyCode = "testLobby";
        String playerID = "player1";

        when(mockDataHandler.getLobbyCode()).thenReturn(lobbyCode);
        when(mockDataHandler.getPlayerID()).thenReturn(playerID);

        activeGameService.playCard(color, value);

        verify(mockStompHandler).playCard(anyString());
    }
}
