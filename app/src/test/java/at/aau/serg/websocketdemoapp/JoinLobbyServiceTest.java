package at.aau.serg.websocketdemoapp;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.widget.EditText;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import at.aau.serg.websocketdemoapp.activities.JoinLobby;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.networking.StompHandler;
import at.aau.serg.websocketdemoapp.services.JoinLobbyService;

class JoinLobbyServiceTest {
    private JoinLobbyService joinLobbyService;
    @Mock
    JoinLobby joinLobby;
    @Mock
    DataHandler dataHandler;
    @Mock
    EditText editText;
    @Mock
    Context context;
    @Mock
    SharedPreferences sharedPreferences;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(joinLobby.findViewById(R.id.enterLobbyCode)).thenReturn(editText);
        when(sharedPreferences.edit()).thenReturn(mock(SharedPreferences.Editor.class));
        when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences);
        when(sharedPreferences.getString(anyString(), anyString())).thenReturn("Test");
        joinLobbyService = new JoinLobbyService(context, joinLobby);
    }

    @AfterEach
    void tearDown() {
        joinLobbyService = null;
    }

    @Test
    void testBackButtonClicked() {
        joinLobbyService.backButtonClicked();
        verify(joinLobby).changeToStartActivity();
    }

    @Test
    void testJoinLobbyWithIDButton() {
        when(dataHandler.getPlayerID()).thenReturn("playerId");
        when(dataHandler.getPlayerName()).thenReturn("playerName");
        Editable editable = mock(Editable.class);
        when(editable.toString()).thenReturn("12345");
        when(editText.getText()).thenReturn(editable);

        joinLobbyService.joinLobbyWithIDClicked("lobbyCode");
        verify(joinLobby).changeToLobbyRoomActivity();
    }
}
