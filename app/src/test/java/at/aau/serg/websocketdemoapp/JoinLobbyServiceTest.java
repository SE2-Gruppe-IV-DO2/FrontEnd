package at.aau.serg.websocketdemoapp;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import android.content.Context;
import android.content.SharedPreferences;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import at.aau.serg.websocketdemoapp.activities.JoinLobby;
import at.aau.serg.websocketdemoapp.services.JoinLobbyService;

class JoinLobbyServiceTest {
    @Mock
    Context mockContext;

    @Mock
    SharedPreferences mockSharedPreferences;

    @Mock
    JoinLobby mockJoinLobbyActivity;

    JoinLobbyService joinLobbyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        when(mockContext.getSharedPreferences("druids_data", Context.MODE_PRIVATE))
                .thenReturn(mockSharedPreferences);
        joinLobbyService = new JoinLobbyService(mockContext, mockJoinLobbyActivity);
    }

    @AfterEach
    void tearDown() {
        joinLobbyService = null;
    }

    @Test
    void backButtonClicked_shouldChangeActivity() {
        joinLobbyService.backButtonClicked();
        verify(mockJoinLobbyActivity).changeToStartActivity();
    }
}
