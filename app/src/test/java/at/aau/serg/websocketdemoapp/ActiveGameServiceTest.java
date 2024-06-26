package at.aau.serg.websocketdemoapp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import at.aau.serg.websocketdemoapp.activities.ActiveGame;
import at.aau.serg.websocketdemoapp.dto.ActivePlayerMessage;
import at.aau.serg.websocketdemoapp.dto.CardPlayedRequest;
import at.aau.serg.websocketdemoapp.dto.GameData;
import at.aau.serg.websocketdemoapp.helper.Card;
import at.aau.serg.websocketdemoapp.helper.CardType;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.helper.JsonParsingException;
import at.aau.serg.websocketdemoapp.networking.StompHandler;
import at.aau.serg.websocketdemoapp.services.ActiveGameService;

@RunWith(RobolectricTestRunner.class)
class ActiveGameServiceTest {
    @Mock
    Context mockContext;
    @Mock
    private DataHandler mockDataHandler;
    @Mock
    private ActiveGame mockActiveGame;
    @Mock
    private StompHandler mockStompHandler;
    @Mock
    SharedPreferences sharedPreferences;
    @Mock
    GameData mockGameData;
    ObjectMapper objectMapper;
    @Mock
    ObjectMapper mockObjectMapper;
    private ActiveGameService activeGameService;
    @Captor
    private ArgumentCaptor<Runnable> runnableCaptor;
    private Gson gson;
    private static final String PLAYER_ID = "playerId";
    private static final String LOBBY_CODE = "lobbyCode";
    private static final String PLAYER_NAME = "playerName";
    private static final String GAME_DATA = "gameData";

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences);
        when(mockDataHandler.getPlayerID()).thenReturn(PLAYER_ID);
        when(mockDataHandler.getLobbyCode()).thenReturn(LOBBY_CODE);
        when(mockDataHandler.getPlayerName()).thenReturn(PLAYER_NAME);
        when(mockDataHandler.getGameData()).thenReturn(GAME_DATA);

        StompHandler.setInstance(mockStompHandler);
        DataHandler.setInstance(mockDataHandler);
        GameData.setInstance(mockGameData);

        activeGameService = ActiveGameService.getInstance(mockContext, mockActiveGame);
        gson = new Gson();
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    public void tearDown() {
        gson = null;
        runnableCaptor = null;
        objectMapper = null;
        GameData.setInstance(null);
        DataHandler.setInstance(null);
        activeGameService = null;
    }

    @Test
    void testPlayerChangeSubscription_ActivePlayer() {
        // Arrange
        doAnswer(invocation -> {
            Consumer<String> callback = invocation.getArgument(0);
            callback.accept(PLAYER_ID); // Simulate server response that the player is active
            return null;
        }).when(mockStompHandler).subscribeForPlayerChangedEvent(anyString(), any());

        // Assert
        //verify(mockActiveGame, times(1)).updateActivePlayerInformation(PLAYER_NAME);
        //assert (activeGameService.isCurrentlyActivePlayer());
    }

    @Test
    void testPlayerChangeSubscription_InactivePlayer() {
        doAnswer(invocation -> {
            Consumer<String> callback = invocation.getArgument(0);
            callback.accept("otherPlayerId");
            return null;
        }).when(mockStompHandler).subscribeForPlayerChangedEvent(anyString(), any());

        // Assert
        assert (activeGameService.isCurrentlyActivePlayer());
    }

    @Test
    void testSetActivePlayer_ActivePlayer() {
        ActivePlayerMessage activePlayerMessage = new ActivePlayerMessage();
        activePlayerMessage.setActivePlayerId(PLAYER_ID);
        activePlayerMessage.setActivePlayerName(PLAYER_NAME);
        try {
            activeGameService.setActivePlayer(objectMapper.writeValueAsString(activePlayerMessage));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        assertTrue(activeGameService.isCurrentlyActivePlayer());
    }

    @Test
    void testSetActivePlayer_InactivePlayer() {
        ActivePlayerMessage activePlayerMessage = new ActivePlayerMessage();
        activePlayerMessage.setActivePlayerId("player2");
        activePlayerMessage.setActivePlayerName(PLAYER_NAME);
        try {
            activeGameService.setActivePlayer(objectMapper.writeValueAsString(activePlayerMessage));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        assertFalse(activeGameService.isCurrentlyActivePlayer());
    }
/*
    @Test
    void testOnCardFling_ActivePlayer_AllowedFling() {
        Card mockCard = new Card(CardType.RED, 5);
        List<Card> cardList = new ArrayList<>();
        cardList.add(mockCard);
        mockGameData.setCardList(cardList);
        when(mockGameData.findCardByCardName(anyString())).thenReturn(mockCard);
        when(mockGameData.getCardList()).thenReturn(cardList);
        when(mockGameData.findCardByCardName(anyString())).thenReturn(mockCard);

        activeGameService.setCurrentlyActivePlayer(true);
        activeGameService.setPreventCardFling(false);

        activeGameService.onCardFling(mockCard.getName());

        verify(mockGameData).findCardByCardName(mockCard.getName());
        Assertions.assertEquals(0, cardList.size());
    }
 */

    @Test
    void testOnCardFling_InactivePlayer() {
        String cardName = "testCard";
        Card mockCard = new Card(CardType.RED, 5);
        List<Card> cardList = new ArrayList<>();
        cardList.add(mockCard);
        when(mockGameData.findCardByCardName(anyString())).thenReturn(mockCard);
        when(mockGameData.getCardList()).thenReturn(cardList);

        ActivePlayerMessage activePlayerMessage = new ActivePlayerMessage();
        activePlayerMessage.setActivePlayerId("player2");
        activePlayerMessage.setActivePlayerName(PLAYER_NAME);
        try {
            activeGameService.setActivePlayer(objectMapper.writeValueAsString(activePlayerMessage));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        activeGameService.onCardFling(cardName);

        verifyNoMoreInteractions(mockGameData);
    }

    @Test
    void testOnCardFling_ActivePlayer_PreventFling() {
        String cardName = "testCard";
        Card mockCard = new Card(CardType.RED, 5);
        List<Card> cardList = new ArrayList<>();
        cardList.add(mockCard);
        when(mockGameData.findCardByCardName(anyString())).thenReturn(mockCard);
        when(mockGameData.getCardList()).thenReturn(cardList);

        ActivePlayerMessage activePlayerMessage = new ActivePlayerMessage();
        activePlayerMessage.setActivePlayerId(PLAYER_ID);
        activePlayerMessage.setActivePlayerName(PLAYER_NAME);
        try {
            activeGameService.setActivePlayer(objectMapper.writeValueAsString(activePlayerMessage));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        activeGameService.setPreventCardFling(true);

        activeGameService.onCardFling(cardName);

        verifyNoMoreInteractions(mockGameData);
    }

    @Test
    void testHandlePlayCardResponse() {
        ActiveGame activeGame = mock(ActiveGame.class);
        activeGameService.updateActiveGame(activeGame);
        CardPlayedRequest cardPlayedRequest = new CardPlayedRequest();
        cardPlayedRequest.setCardType(CardType.GREEN);
        cardPlayedRequest.setColor("green");
        cardPlayedRequest.setValue("5");

        try {
            activeGameService.handlePlayCardResponse(objectMapper.writeValueAsString(cardPlayedRequest));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verify(activeGame).runOnUiThread(runnableCaptor.capture());
        runnableCaptor.getValue().run();

        verify(activeGame).displayCardsPlayed();
    }

    @Test
    void testHandlePlayCardResponse_JsonProcessingException() {
        String invalidJson = "invalid json";
        Assertions.assertThrows(JsonParsingException.class, () -> activeGameService.handlePlayCardResponse(invalidJson));
    }
/*
    @Test
    void testWonTrickEvent() throws JsonProcessingException {
        TrickWonMessage trickWonMessage = new TrickWonMessage();
        trickWonMessage.setWinningPlayerId(PLAYER_ID);
        trickWonMessage.setWinningPlayerName(PLAYER_NAME);

        String trickWonJson = objectMapper.writeValueAsString(trickWonMessage);

        activeGameService.handleTrickWon(trickWonJson);

        verify(mockActiveGame).runOnUiThread(runnableCaptor.capture());

        runnableCaptor.getValue().run();

        verify(mockActiveGame).showPlayerWonTrickMessage("Trick was won by player: " + PLAYER_NAME);
        verify(mockActiveGame).clearPlayedCards();
    }
 */

    @Test
    void testWonTrickEventExceptionForWrongJson() {
        String incorrectJson = "This is not a valid JSON string";

        assertThrows(JsonParsingException.class, () -> {
            activeGameService.handleTrickWon(incorrectJson);
        });
    }

    @Test
    void testPlayCard() {
        String color = "red";
        int value = 5;
        String lobbyCode = "testLobby";
        String playerID = "player1";

        when(mockDataHandler.getLobbyCode()).thenReturn(lobbyCode);
        when(mockDataHandler.getPlayerID()).thenReturn(playerID);

        activeGameService.playCard(CardType.getByColor(color), color, value);

        //verify(mockStompHandler).playCard(anyString());
    }

    @Test
    void testPlayGaia() {
        CardType cardType = CardType.GAIA;
        String color = "green";
        int value = 0;
        String lobbyCode = "testLobby";
        String playerID = "player1";

        when(mockDataHandler.getLobbyCode()).thenReturn(lobbyCode);
        when(mockDataHandler.getPlayerID()).thenReturn(playerID);

        activeGameService.playCard(CardType.getByColor(color), color, value);

        //verify(mockStompHandler).playCard(anyString());
    }

    @Test
    void testCardFlingWhenNotAllowed() {
        String cardName = "testCard";
        Card mockCard = new Card(CardType.RED, 5);
        List<Card> cardList = new ArrayList<>();
        cardList.add(mockCard);
        when(mockGameData.findCardByCardName(anyString())).thenReturn(mockCard);
        when(mockGameData.getCardList()).thenReturn(cardList);

        ActivePlayerMessage activePlayerMessage = new ActivePlayerMessage();
        activePlayerMessage.setActivePlayerId("player2");
        activePlayerMessage.setActivePlayerName(PLAYER_NAME);
        try {
            activeGameService.setActivePlayer(objectMapper.writeValueAsString(activePlayerMessage));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        activeGameService.onCardFling(cardName);

        verifyNoMoreInteractions(mockGameData);
    }

    @Test
    void testHandleRoundEnd() {
        List<Card> actualList = new ArrayList<>();
        when(mockGameData.getCardList()).thenReturn(actualList);
        when(mockGameData.getCardsPlayed()).thenReturn(actualList);

        activeGameService.handleRoundEnd();

        Assertions.assertEquals(new ArrayList<>().size(), mockGameData.getCardList().size());
        Assertions.assertEquals(new ArrayList<>().size(), mockGameData.getCardList().size());
        //verify(mockActiveGame, times(1)).getData();
    }

    @Test
    void testHandleRoundEndFinishedGame() {
        when(mockActiveGame.isGameFinsihed()).thenReturn(true);

        activeGameService.handleRoundEnd();

        verify(mockActiveGame).goToEndScreen();
    }
}
