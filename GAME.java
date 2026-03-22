import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GAME {
    private final GRID playerOneGrid;
    private final GRID playerTwoGrid;
    private final List<ShipType> fleetDefinition;
    private int activePlayer;
    private boolean gameOver;
    private String winnerName;

    public GAME(String playerOneName, String playerTwoName, int boardSize, List<ShipType> fleetDefinition) {
        if (fleetDefinition == null || fleetDefinition.isEmpty()) {
            throw new IllegalArgumentException("Fleet definition must not be empty.");
        }
        this.playerOneGrid = new GRID(playerOneName, boardSize);
        this.playerTwoGrid = new GRID(playerTwoName, boardSize);
        this.fleetDefinition = new ArrayList<ShipType>(fleetDefinition);
        this.activePlayer = 0;
        this.gameOver = false;
        this.winnerName = null;
    }

    public GRID getPlayerGrid(int playerIndex) {
        if (playerIndex == 0) {
            return playerOneGrid;
        }
        if (playerIndex == 1) {
            return playerTwoGrid;
        }
        throw new IllegalArgumentException("Player index must be 0 or 1.");
    }

    public GRID getActivePlayerGrid() {
        return getPlayerGrid(activePlayer);
    }

    public GRID getOpponentGrid() {
        return getPlayerGrid(1 - activePlayer);
    }

    public int getActivePlayerIndex() {
        return activePlayer;
    }

    public String getActivePlayerName() {
        return getActivePlayerGrid().getUsername();
    }

    public String getOpponentName() {
        return getOpponentGrid().getUsername();
    }

    public List<ShipType> getFleetDefinition() {
        return Collections.unmodifiableList(fleetDefinition);
    }

    public void switchTurn() {
        if (!gameOver) {
            activePlayer = 1 - activePlayer;
        }
    }

    public ShotReport playTurn(Coordinate target) {
        if (gameOver) {
            throw new IllegalStateException("Game is already over.");
        }
        ShotReport report = getOpponentGrid().fireAt(target);
        if (report.isGameWon()) {
            gameOver = true;
            winnerName = getActivePlayerName();
        }
        return report;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public String getWinnerName() {
        return winnerName;
    }
}
