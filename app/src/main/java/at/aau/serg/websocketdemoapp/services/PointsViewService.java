package at.aau.serg.websocketdemoapp.services;

import java.util.HashMap;
import java.util.Map;

public class PointsViewService {
    Map<String, HashMap<Integer, Integer>> playerPoints;
    private int[] sumArray;

    public PointsViewService() {
        playerPoints = new HashMap<>();
    }

    public void calcSum() {
        sumArray = new int[playerPoints.size()];
        int playerIndex = 0;
        for (Map.Entry<String, HashMap<Integer, Integer>> entry : playerPoints.entrySet()) {
            HashMap<Integer, Integer> roundsMap = entry.getValue();
            int sum = 0;
            assert roundsMap != null;
            for (int points : roundsMap.values()) {
                sum += points;
            }
            sumArray[playerIndex] = sum;
            playerIndex++;
        }
    }

    public Map<String, HashMap<Integer, Integer>> getPlayerPoints() {
        return playerPoints;
    }

    public int[] getSumArray() {
        return sumArray;
    }
}
