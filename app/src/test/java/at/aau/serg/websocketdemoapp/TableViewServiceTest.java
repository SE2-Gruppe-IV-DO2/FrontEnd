package at.aau.serg.websocketdemoapp;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import at.aau.serg.websocketdemoapp.activities.TableView;
import at.aau.serg.websocketdemoapp.dto.GameData;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.networking.StompHandler;
import at.aau.serg.websocketdemoapp.services.TableViewService;

public class TableViewServiceTest {
    @Mock
    GameData mockGameData;
    @Mock
    TableView mockTableView;
    @Mock
    DataHandler mockDataHandler;
    @Mock
    Context mockContext;
    @Mock
    StompHandler stompHandler;
    TableViewService tableViewService;
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        GameData.setInstance(mockGameData);
        DataHandler.setInstance(mockDataHandler);
        StompHandler.setInstance(stompHandler);
        objectMapper = new ObjectMapper();
        tableViewService = new TableViewService(mockContext, mockTableView);
    }

    @AfterEach
    void tearDown() {
        GameData.setInstance(null);
        DataHandler.setInstance(null);
        objectMapper = null;
        tableViewService = null;
    }

    @Test
    void testUpdateTableView() {
        TableView newTableView = mock(TableView.class);
        tableViewService.updateTableView(newTableView);
        assertSame(newTableView, tableViewService.getTableView());
    }
/*
    @Test
    void testGetPlayerTricks() {
        PlayerTrickResponse playerTrickResponse = new PlayerTrickResponse();
        playerTrickResponse.setPlayerTricks(new HashMap<>());
        CompletableFuture<Void> future = new CompletableFuture<>();

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Consumer<String> callback = invocation.getArgument(1);
                new Thread(() -> {
                    try {
                        callback.accept(objectMapper.writeValueAsString(playerTrickResponse));
                    } catch (JsonProcessingException e) {
                        throw new JsonParsingException("JSON PARSE", e);
                    }
                    future.complete(null);
                }).start();
                return null;
            }
        }).when(stompHandler).getPlayerTricks(anyString(), any(Consumer.class));

        tableViewService.getPlayerTricks();

        try {
            future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verify(mockTableView, times(1)).updateUI();
    }
 */
}
