package at.aau.serg.websocketdemoapp;

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
import at.aau.serg.websocketdemoapp.activities.Lobbyroom;
import at.aau.serg.websocketdemoapp.services.LobbyRoomService;

class LobbyRoomServiceTest {
    @Mock
    Context mockContext;
    @Mock
    SharedPreferences mockSharedPreferences;
    @Mock
    TextView mockTextView;
    @Mock
    Lobbyroom mockLobbyActivity;
    LobbyRoomService lobbyRoomService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        when(mockContext.getSharedPreferences("druids_data", Context.MODE_PRIVATE))
                .thenReturn(mockSharedPreferences);
        lobbyRoomService = new LobbyRoomService(mockContext, mockLobbyActivity);
    }

    @AfterEach
    void tearDown() {
        lobbyRoomService = null;
    }

    @Test
    void testBackButtonClicked() {
        lobbyRoomService.backButtonClicked();
        verify(mockLobbyActivity).changeToStartActivity();
    }

    @Test
    void testSetPlayerName() {
        String playerName = "John Doe";
        when(mockSharedPreferences.getString("playerName", "")).thenReturn(playerName);
        lobbyRoomService.setPlayerName(mockTextView);
        verify(mockTextView).setText(playerName);
    }
}
