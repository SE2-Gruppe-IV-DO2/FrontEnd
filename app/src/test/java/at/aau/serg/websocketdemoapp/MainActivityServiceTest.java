package at.aau.serg.websocketdemoapp;

import android.view.View;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import at.aau.serg.websocketdemoapp.services.MainActivityService;

public class MainActivityServiceTest {
    MainActivityService mainActivityService;
    @Mock
    View view;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mainActivityService = new MainActivityService();
    }

    @AfterEach
    void tearDown() {
        mainActivityService = null;
    }

    @Test
    void testCreateGameServiceMethodSuccess() {
        
    }

    @Test
    void testCreateGameServiceMethodFailure() {

    }

    @Test
    void testJoinGameServiceMethodSuccess() {

    }

    @Test
    void testJoinGameServiceMethodFailure() {

    }

    @Test
    void testTutorialServiceMethodSuccess() {

    }

    @Test
    void testTutorialServiceMethodFailure() {

    }
}
