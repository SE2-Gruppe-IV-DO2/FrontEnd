package at.aau.serg.websocketdemoapp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
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


import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import at.aau.serg.websocketdemoapp.activities.LobbyRoom;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
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

    LobbyRoomService lobbyRoomService;

    @BeforeEach
    void setUp() {
        openMocks(this);

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

        verify(mockParticipants, times(1)).append(anyString());
        verify(mockLobbyCode, times(1)).setText(any());
    }

    @Test
    void testCreateButtonClicked() {
        lobbyRoomService.startButtonClicked();
        verify(stompHandler, times(1)).startGameForLobby(any());
    }
}
