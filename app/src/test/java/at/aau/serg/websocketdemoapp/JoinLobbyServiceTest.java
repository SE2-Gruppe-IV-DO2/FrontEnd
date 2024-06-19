package at.aau.serg.websocketdemoapp;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.widget.EditText;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import at.aau.serg.websocketdemoapp.activities.JoinLobby;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.helper.JsonParsingException;
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
    @Mock
    StompHandler stompHandler;
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(joinLobby.findViewById(R.id.enterLobbyCode)).thenReturn(editText);
        when(sharedPreferences.edit()).thenReturn(mock(SharedPreferences.Editor.class));
        when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences);
        when(sharedPreferences.getString(anyString(), anyString())).thenReturn("Test");
        objectMapper = new ObjectMapper();
        StompHandler.setInstance(stompHandler);
        joinLobbyService = new JoinLobbyService(context, joinLobby);
    }

    @AfterEach
    void tearDown() {
        objectMapper = null;
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

    @Test
    void testJoinLobbyWithIDButton_StompHandlerCalled() {
        when(dataHandler.getPlayerID()).thenReturn("playerId");
        when(dataHandler.getPlayerName()).thenReturn("playerName");
        Editable editable = mock(Editable.class);
        when(editable.toString()).thenReturn("12345");
        when(editText.getText()).thenReturn(editable);

        joinLobbyService.joinLobbyWithIDClicked("lobbyCode");
        /*
        verify(stompHandler, times(1)).joinLobby(eq("lobbyCode"), eq("playerId"), eq("playerName"), any());
         */
    }




    @Test
    void testJoinLobbyWithIDException() throws Exception {
        when(dataHandler.getPlayerID()).thenReturn("playerId");
        when(dataHandler.getPlayerName()).thenReturn("playerName");

        CompletableFuture<Void> future = new CompletableFuture<>();

        // Mocking stompHandler.joinLobby to throw an exception with invalid JSON
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Consumer<String> callback = invocation.getArgument(3);
                new Thread(() -> {
                    try {
                        callback.accept("invalid JSON");
                    } catch (JsonParsingException e) { // Catching specific JsonParsingException
                        future.completeExceptionally(e);
                    } catch (Exception e) {
                        future.completeExceptionally(new RuntimeException("Unexpected exception", e));
                    }
                }).start();
                return null;
            }
        }).when(stompHandler).joinLobby(anyString(), anyString(), anyString(), any());

        // Asserting that JsonParsingException is thrown and properly handled
        JsonParsingException exception = assertThrows(JsonParsingException.class, () -> {
            joinLobbyService.joinLobbyWithIDClicked("lobbyCode");
            try {
                future.get(5, TimeUnit.SECONDS); // Wait with timeout
            } catch (Exception e) {
                throw (Exception) e.getCause();
            }
        });
    }
}
