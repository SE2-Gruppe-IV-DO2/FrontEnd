package at.aau.serg.websocketdemoapp;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import at.aau.serg.websocketdemoapp.activities.MainActivity;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.services.MainActivityService;

class MainActivityServiceTest {
    MainActivityService mainActivityService;
    @Mock
    Context context;
    @Mock
    SharedPreferences sharedPreferences;
    @Mock
    View view;
    @Mock
    SharedPreferences.Editor sharedEditor;
    @Mock
    MainActivity activity;
    @Mock
    DataHandler dataHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences);
        when(sharedPreferences.edit()).thenReturn(sharedEditor);
        when(sharedEditor.putString(anyString(), anyString())).thenReturn(sharedEditor);

        mainActivityService = new MainActivityService(context, activity);
    }

    @AfterEach
    void tearDown() {
        mainActivityService = null;
    }

    @Test
    void testCreateGameServiceMethodSuccess() {
        when(dataHandler.getPlayerName()).thenReturn("TestPlayer");

        EditText editText = mock(EditText.class);
        TextView errorLabel = mock(TextView.class);

        when(view.findViewById(R.id.playerName)).thenReturn(editText);
        when(view.findViewById(R.id.labelError)).thenReturn(errorLabel);

        Editable editable = mock(Editable.class);
        when(editText.getText()).thenReturn(editable);
        when(editable.toString()).thenReturn("TestPlayer");

        mainActivityService.createGameService(editText, errorLabel);

        //verify(activity, times(1)).changeToCreateActivity();
        verify(errorLabel).setVisibility(View.INVISIBLE);
    }

    /*
    @Test
    void testCreateGameServiceMethodFailure() {
        when(dataHandler.getPlayerName()).thenReturn("TestPlayer");

        EditText editText = mock(EditText.class);
        TextView errorLabel = mock(TextView.class);

        when(view.findViewById(R.id.playerName)).thenReturn(editText);
        when(view.findViewById(R.id.labelError)).thenReturn(errorLabel);

        Editable editable = mock(Editable.class);
        when(editText.getText()).thenReturn(editable);
        when(editable.toString()).thenReturn("TestPlayer");

        mainActivityService.createGameService(editText, errorLabel);

        verify(sharedEditor, times(0)).putString("playerName", "TestPlayer");
        verify(sharedEditor, times(0)).apply();
        verify(errorLabel).setVisibility(View.VISIBLE);
    }
     */

    @Test
    void testJoinGameServiceMethodSuccess() {
        when(dataHandler.getPlayerName()).thenReturn("TestPlayer");

        EditText editText = mock(EditText.class);
        TextView errorLabel = mock(TextView.class);

        when(view.findViewById(R.id.playerName)).thenReturn(editText);
        when(view.findViewById(R.id.labelError)).thenReturn(errorLabel);

        Editable editable = mock(Editable.class);
        when(editText.getText()).thenReturn(editable);
        when(editable.toString()).thenReturn("TestPlayer");

        mainActivityService.joinGameService(editText, errorLabel);

        verify(activity, times(1)).changeToJoinActivity();
        verify(errorLabel).setVisibility(View.INVISIBLE);
    }

    /*
    @Test
    void testJoinGameServiceMethodFailure() {
        when(dataHandler.getPlayerName()).thenReturn("TestPlayer");

        EditText editText = mock(EditText.class);
        TextView errorLabel = mock(TextView.class);

        when(view.findViewById(R.id.playerName)).thenReturn(editText);
        when(view.findViewById(R.id.labelError)).thenReturn(errorLabel);

        Editable editable = mock(Editable.class);
        when(editText.getText()).thenReturn(editable);
        when(editable.toString()).thenReturn("TestPlayer");

        mainActivityService.joinGameService(editText, errorLabel);

        verify(sharedEditor, times(0)).putString("playerName", "TestPlayer");
        verify(sharedEditor, times(0)).apply();
        verify(errorLabel).setVisibility(View.VISIBLE);
    }
     */
}
