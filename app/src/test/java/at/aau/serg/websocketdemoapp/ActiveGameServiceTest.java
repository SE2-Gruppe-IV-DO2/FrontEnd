package at.aau.serg.websocketdemoapp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.function.Consumer;

import at.aau.serg.websocketdemoapp.activities.ActiveGame;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.networking.StompHandler;
import at.aau.serg.websocketdemoapp.services.ActiveGameService;
import ua.naiksoftware.stomp.StompClient;

public class ActiveGameServiceTest {
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
    private StompClient stompClient;

    private ActiveGameService activeGameService;

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

        activeGameService = new ActiveGameService(mockActiveGame, mockDataHandler);
    }
/*
    @Test
    public void testDefaultConstructor() {
        // Act
        activeGameService = new ActiveGameService(mockContext, mockActiveGame);
        activeGameService.getData();

        // Assert
        verify(mockActiveGame).refreshActiveGame(any());
    }

    @Test
    public void getData_CallsDealNewRoundAndRefreshActiveGame() {
        // Act
        activeGameService.getData();

        // Assert
        //verify(mockStompHandler).dealNewRound(eq(LOBBY_CODE), eq(PLAYER_ID), any());
        verify(mockActiveGame).refreshActiveGame(any());
    }

 */

    @Test
    public void testPlayerChangeSubscription_ActivePlayer() {
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
    public void testPlayerChangeSubscription_InactivePlayer() {
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
    public void testSetActivePlayer_ActivePlayer() {
        activeGameService.setActivePlayer(PLAYER_ID);

        assertTrue(activeGameService.isCurrentlyActivePlayer());
    }

    @Test
    public void testSetActivePlayer_InactivePlayer() {
        activeGameService.setActivePlayer("player2");

        assertFalse(activeGameService.isCurrentlyActivePlayer());
    }
}
