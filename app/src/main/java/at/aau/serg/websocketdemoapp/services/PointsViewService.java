package at.aau.serg.websocketdemoapp.services;

public class PointsViewService {
    private int[][] pointsArray;
    private int[] sumArray;
    private static final int ROUNDS = 5;
    private final int players;

    public PointsViewService(int players) {
        this.players = players;
        pointsArray = new int[ROUNDS][players];
        sumArray = new int[players];
    }

    public void setPoints(int round, int player, int points) {
        pointsArray[round - 1][player - 1] = points;
        calcSum();
    }

    public void calcSum() {
        for(int i = 0; i < players; i++) {
            int sum = 0;
            for(int j = 0; j < ROUNDS; j++) {
                sum += pointsArray[j][i];
            }
            sumArray[i] = sum;
        }
    }

    public int[][] getPointsArray() {
        return pointsArray;
    }

    public int[] getSumArray() {
        return sumArray;
    }
}
