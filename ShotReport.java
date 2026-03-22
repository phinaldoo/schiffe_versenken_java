public final class ShotReport {
    private final ShotResult result;
    private final Coordinate coordinate;
    private final String sunkShipName;
    private final boolean gameWon;

    public ShotReport(ShotResult result, Coordinate coordinate, String sunkShipName, boolean gameWon) {
        this.result = result;
        this.coordinate = coordinate;
        this.sunkShipName = sunkShipName;
        this.gameWon = gameWon;
    }

    public ShotResult getResult() {
        return result;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public String getSunkShipName() {
        return sunkShipName;
    }

    public boolean isGameWon() {
        return gameWon;
    }
}
