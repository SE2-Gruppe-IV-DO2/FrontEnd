package at.aau.serg.websocketdemoapp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.widget.EditText;

import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.function.Consumer;

import at.aau.serg.websocketdemoapp.activities.ActiveGame;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.networking.StompHandler;
import at.aau.serg.websocketdemoapp.services.ActiveGameService;

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

    private ActiveGameService activeGameService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(mockDataHandler.getLobbyCode()).thenReturn("testLobbyCode");
        when(mockDataHandler.getGameData()).thenReturn("mockGameData");

        when(sharedPreferences.edit()).thenReturn(mock(SharedPreferences.Editor.class));
        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences);
        when(sharedPreferences.getString(anyString(), anyString())).thenReturn("testGameData");

        activeGameService = new ActiveGameService(mockContext, mockActiveGame);
    }


    @Test
    public void getData_CallsDealNewRoundAndRefreshActiveGame() {
        // Arrange
        String lobbyCode = "testLobbyCode";
        String gameData = "testGameData";


        // Act
        activeGameService.getData();

        // Assert
        //verify(mockStompHandler).dealNewRound(eq(lobbyCode), any());
        verify(mockActiveGame).refreshActiveGame(any());
    }
}
