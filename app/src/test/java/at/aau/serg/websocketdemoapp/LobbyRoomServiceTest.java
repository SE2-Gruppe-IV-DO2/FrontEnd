package at.aau.serg.websocketdemoapp;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import at.aau.serg.websocketdemoapp.activities.LobbyRoom;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.networking.StompHandler;
import at.aau.serg.websocketdemoapp.services.LobbyRoomService;

class LobbyRoomServiceTest {
    @Mock
    Context mockContext;
    @Mock
    LobbyRoom mockLobbyActivity;
    @Mock
    DataHandler dataHandler;
    @Mock
    StompHandler stompHandler;
    @Mock
    TextView mockParticipants;
    @Mock
    TextView mockLobbyCode;
    @Mock
    SharedPreferences sharedPreferences;
    @Mock
    SharedPreferences.Editor editor;
    LobbyRoomService lobbyRoomService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences);
        when(editor.putString(anyString(), anyString())).thenReturn(editor);

        when(mockLobbyActivity.findViewById(R.id.participants)).thenReturn(mockParticipants);
        when(mockLobbyActivity.findViewById(R.id.lobbyCode)).thenReturn(mockLobbyCode);

        lobbyRoomService = new LobbyRoomService(mockContext, mockLobbyActivity);
        lobbyRoomService.setStompHandler(stompHandler);
    }

    @AfterEach
    void tearDown() {
        lobbyRoomService = null;
    }

    @Test
    void testBackButtonClicked() {
        lobbyRoomService.backButtonClicked();
        verify(mockLobbyActivity, times(1)).changeToStartActivity();
    }

    @Test
    void testOnCreation() {
        lobbyRoomService.onCreation();

        verify(mockParticipants, times(1)).append(anyString());
        verify(mockLobbyCode, times(1)).setText(anyString());
    }
}
