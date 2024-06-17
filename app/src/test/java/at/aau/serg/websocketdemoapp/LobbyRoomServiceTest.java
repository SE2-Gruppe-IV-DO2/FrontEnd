package at.aau.serg.websocketdemoapp;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import at.aau.serg.websocketdemoapp.activities.LobbyRoom;
import at.aau.serg.websocketdemoapp.dto.GetPlayersInLobbyMessage;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.helper.JsonParsingException;
import at.aau.serg.websocketdemoapp.networking.StompHandler;
import at.aau.serg.websocketdemoapp.services.LobbyRoomService;

@RunWith(RobolectricTestRunner.class)
class LobbyRoomServiceTest {
    @Mock
    Context mockContext;
    @Mock
    LobbyRoom mockLobbyActivity;

    @Mock
    StompHandler stompHandler;
    @Mock
    TextView mockParticipants;
    @Mock
    TextView mockLobbyCode;

    @Mock
    ImageView mockQRCodeImage;
    @Mock
    SharedPreferences sharedPreferences;
    @Mock
    SharedPreferences.Editor editor;

    @Mock
    private BarcodeEncoder mockBarcodeEncoder;

    @Mock
    private Bitmap mockBitmap;

    @Mock
    DataHandler dataHandler;
    private Gson gson;


    LobbyRoomService lobbyRoomService;

    @BeforeEach
    void setUp() {
        openMocks(this);
        gson = new Gson();

        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences);
        when(editor.putString(anyString(), anyString())).thenReturn(editor);

        when(mockLobbyActivity.findViewById(R.id.participants)).thenReturn(mockParticipants);
        when(mockLobbyActivity.findViewById(R.id.lobbyCode)).thenReturn(mockLobbyCode);
        when(mockLobbyActivity.findViewById(R.id.qrCode)).thenReturn(mockQRCodeImage);

        when(sharedPreferences.getString(anyString(), anyString())).thenReturn("Test");

        mockBarcodeEncoder = mock(BarcodeEncoder.class);

        when(dataHandler.getPlayerID()).thenReturn("playerId");
        when(dataHandler.getPlayerName()).thenReturn("playerName");
        when(dataHandler.getLobbyCode()).thenReturn("lobbyCode");

        StompHandler.setInstance(stompHandler);

        lobbyRoomService = new LobbyRoomService(dataHandler, mockLobbyActivity);
        lobbyRoomService.setmEncoder(mockBarcodeEncoder);
        lobbyRoomService.setStompHandler(stompHandler);
    }

    @AfterEach
    void tearDown() {
        lobbyRoomService = null;
    }

    @Test
    void testBackButtonClicked() {
        lobbyRoomService.backButtonClicked();
        verify(mockLobbyActivity, times(1)).changeToMainActivity();
    }

    @Test
    void testOnCreation() {
        when(mockBarcodeEncoder.createBitmap(any(BitMatrix.class))).thenReturn(mockBitmap);

        lobbyRoomService.onCreation();

        verify(mockLobbyCode, times(1)).setText(any());
    }

    @Test
    void testCreateButtonClicked() {
        lobbyRoomService.startButtonClicked();
        verify(stompHandler, times(1)).startGameForLobby(any());
    }

    @Test
    public void addPlayerNameToLobby_ShouldAppendNameWithNewLine() {
        String playerName = "TestPlayer";
        when(lobbyRoomService.getParticipants().getText()).thenReturn(new StringBuilder());

        lobbyRoomService.addPlayerNameToLobby(playerName);

        verify(lobbyRoomService.getParticipants()).append(playerName + "\n");
    }

    @Test
    public void addPlayersToLobby() {
        String playerName1 = "TestPlayer1";
        String playerName2 = "TestPlayer2";

        when(lobbyRoomService.getParticipants().getText()).thenReturn(new StringBuilder());
        List<String> playerNames = new ArrayList<>();
        playerNames.add(playerName1);
        playerNames.add(playerName2);

        lobbyRoomService.addPlayerNamesToLobby(playerNames);

        verify(lobbyRoomService.getParticipants()).append(playerName1 + "\n");
        verify(lobbyRoomService.getParticipants()).append(playerName2 + "\n");
    }

    @Test
    void testFailingOnCreation() {
        assertThrows(RuntimeException.class, ()->{
            lobbyRoomService = new LobbyRoomService(dataHandler, mockLobbyActivity);
            lobbyRoomService.createLobbyQRCode("");
        });
    }

    @Test
    void testGetPlayersInLobbyWithResponse() {
        String playerName1 = "TestPlayer1";

        GetPlayersInLobbyMessage message = new GetPlayersInLobbyMessage();
        message.setLobbyCode("lobbyCode");
        List<String> playerNames = new ArrayList<>();
        playerNames.add(playerName1);
        message.setPlayerNames(playerNames);

        lobbyRoomService.getPlayersInLobbyWithResponse(gson.toJson(message));
        verify(lobbyRoomService.getParticipants()).append(playerName1 + "\n");
    }

    @Test
    void testGetPlayersInLobbyWithResponse_WrongJson() {
        String playerName1 = "TestPlayer1";

        GetPlayersInLobbyMessage message = new GetPlayersInLobbyMessage();
        message.setLobbyCode("lobbyCode");
        List<String> playerNames = new ArrayList<>();
        playerNames.add(playerName1);
        message.setPlayerNames(playerNames);

        assertThrows(JsonParsingException.class, ()->{
            lobbyRoomService.getPlayersInLobbyWithResponse(message.toString());
        });
    }

    @Test
    void testStartGameException() throws Exception {
        when(dataHandler.getPlayerID()).thenReturn("playerId");
        when(dataHandler.getPlayerName()).thenReturn("playerName");
        CompletableFuture<Void> future = new CompletableFuture<>();

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Consumer<String> callback = invocation.getArgument(1); // Fixing the index to 1
                new Thread(() -> {
                    try {
                        callback.accept("invalid JSON");
                    } catch (JsonParsingException e) {
                        future.completeExceptionally(e);
                    } catch (Exception e) {
                        future.completeExceptionally(new RuntimeException("Unexpected exception", e));
                    }
                }).start();
                return null;
            }
        }).when(stompHandler).initGameStartSubscription(anyString(), any());

        Assertions.assertThrows(JsonParsingException.class, () -> {
            lobbyRoomService.initGameStartSubscription();
            try {
                future.get(5, TimeUnit.SECONDS); // Wait with timeout
            } catch (Exception e) {
                if (e.getCause() instanceof JsonParsingException) {
                    throw (JsonParsingException) e.getCause();
                } else {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    void testStartGameSuccess() {
        String successfulResponse = "{\"response\":\"Game started!\"}";
        CompletableFuture<Void> future = new CompletableFuture<>();

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Consumer<String> callback = invocation.getArgument(1);
                new Thread(() -> {
                    callback.accept(successfulResponse);
                    future.complete(null);
                }).start();
                return null;
            }
        }).when(stompHandler).initGameStartSubscription(anyString(), any());

        lobbyRoomService.initGameStartSubscription();

        try {
            future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verify(mockLobbyActivity, times(1)).changeToGameActivity();
    }
}
