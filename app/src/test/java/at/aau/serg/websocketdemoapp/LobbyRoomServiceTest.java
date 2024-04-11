package at.aau.serg.websocketdemoapp;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import at.aau.serg.websocketdemoapp.activities.Lobbyroom;
import at.aau.serg.websocketdemoapp.networking.StompHandler;
import at.aau.serg.websocketdemoapp.services.LobbyRoomService;

class LobbyRoomServiceTest {
    @Mock
    Context mockContext;
    @Mock
    Lobbyroom mockLobbyActivity;
    @Mock
    SharedPreferences mockSharedPreferences;
    @Mock
    SharedPreferences.Editor mockEditor;
    @Mock
    TextView mockParticipants;
    @Mock
    StompHandler mockStompHandler;
    LobbyRoomService lobbyRoomService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockContext.getSharedPreferences("druids_data", Context.MODE_PRIVATE)).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);
        when(mockLobbyActivity.findViewById(R.id.participants)).thenReturn(mockParticipants);
        lobbyRoomService = new LobbyRoomService(mockContext, mockLobbyActivity);
        lobbyRoomService.setStompHandler(mockStompHandler);
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
        when(mockSharedPreferences.getString("playerName", "")).thenReturn("TestPlayer");
        lobbyRoomService.onCreation();
        verify(mockStompHandler, times(1)).connectToServer();
        verify(mockParticipants, times(1)).append("TestPlayer\n");
    }
}
