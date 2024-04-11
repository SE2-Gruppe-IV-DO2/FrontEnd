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
        EditText editText = mock(EditText.class);
        TextView errorLabel = mock(TextView.class);

        when(view.findViewById(R.id.playerName)).thenReturn(editText);
        when(view.findViewById(R.id.labelError)).thenReturn(errorLabel);

        Editable editable = mock(Editable.class);
        when(editText.getText()).thenReturn(editable);
        when(editable.toString()).thenReturn("Test");

        mainActivityService.createGameService(editText, errorLabel);

        verify(sharedEditor).putString("playerName", "Test");
        verify(sharedEditor).apply();
        verify(errorLabel).setVisibility(View.INVISIBLE);
    }

    @Test
    void testCreateGameServiceMethodFailure() {
        EditText editText = mock(EditText.class);
        TextView errorLabel = mock(TextView.class);

        when(view.findViewById(R.id.playerName)).thenReturn(editText);
        when(view.findViewById(R.id.labelError)).thenReturn(errorLabel);

        Editable editable = mock(Editable.class);
        when(editText.getText()).thenReturn(editable);
        when(editable.toString()).thenReturn("Test1");

        mainActivityService.createGameService(editText, errorLabel);

        verify(sharedEditor, times(0)).putString("playerName", "Test1");
        verify(sharedEditor, times(0)).apply();
        verify(errorLabel).setVisibility(View.VISIBLE);
    }

    @Test
    void testJoinGameServiceMethodSuccess() {
        EditText editText = mock(EditText.class);
        TextView errorLabel = mock(TextView.class);

        when(view.findViewById(R.id.playerName)).thenReturn(editText);
        when(view.findViewById(R.id.labelError)).thenReturn(errorLabel);

        Editable editable = mock(Editable.class);
        when(editText.getText()).thenReturn(editable);
        when(editable.toString()).thenReturn("Test");

        mainActivityService.joinGameService(editText, errorLabel);

        verify(sharedEditor).putString("playerName", "Test");
        verify(sharedEditor).apply();
        verify(errorLabel).setVisibility(View.INVISIBLE);
    }

    @Test
    void testJoinGameServiceMethodFailure() {
        EditText editText = mock(EditText.class);
        TextView errorLabel = mock(TextView.class);

        when(view.findViewById(R.id.playerName)).thenReturn(editText);
        when(view.findViewById(R.id.labelError)).thenReturn(errorLabel);

        Editable editable = mock(Editable.class);
        when(editText.getText()).thenReturn(editable);
        when(editable.toString()).thenReturn("Test1");

        mainActivityService.joinGameService(editText, errorLabel);

        verify(sharedEditor, times(0)).putString("playerName", "Test1");
        verify(sharedEditor, times(0)).apply();
        verify(errorLabel).setVisibility(View.VISIBLE);
    }
}
