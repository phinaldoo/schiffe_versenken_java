import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GRID {
    private final String username;
    private final int size;
    private final List<SHIP> ships;
    private final int[][] shipIndexAtCell;
    private final boolean[][] shotsTaken;
    private int placedShipCells;
    private int hitShipCells;

    public GRID(String username, int size) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username must not be empty.");
        }
        if (size < 2 || size > 26) {
            throw new IllegalArgumentException("Grid size must be between 2 and 26.");
        }
        this.username = username.trim();
        this.size = size;
        this.ships = new ArrayList<SHIP>();
        this.shipIndexAtCell = new int[size][size];
        this.shotsTaken = new boolean[size][size];
        this.placedShipCells = 0;
        this.hitShipCells = 0;
    }

    public String getUsername() {
        return username;
    }

    public int getSize() {
        return size;
    }

    public List<SHIP> getShips() {
        return Collections.unmodifiableList(ships);
    }

    public boolean canPlaceShip(Coordinate start, Orientation orientation, int length) {
        List<Coordinate> cells;
        try {
            cells = getShipCells(start, orientation, length);
        } catch (IllegalArgumentException ex) {
            return false;
        }
        for (Coordinate c : cells) {
            if (shipIndexAtCell[c.getRow()][c.getColumn()] > 0) {
                return false;
            }
            if (hasAdjacentShip(c)) {
                return false;
            }
        }
        return true;
    }

    public SHIP placeShip(String shipName, Coordinate start, Orientation orientation, int length) {
        if (length < 2) {
            throw new IllegalArgumentException("Ship length must be at least 2.");
        }
        List<Coordinate> cells = getShipCells(start, orientation, length);
        if (!canPlaceShip(start, orientation, length)) {
            throw new IllegalArgumentException("Ship placement collides or touches another ship.");
        }
        SHIP ship = new SHIP(shipName, cells);
        ships.add(ship);
        int shipIndex = ships.size();
        for (Coordinate c : cells) {
            shipIndexAtCell[c.getRow()][c.getColumn()] = shipIndex;
        }
        placedShipCells += cells.size();
        return ship;
    }

    public void placeShipsRandomly(List<ShipType> fleetDefinition, long seed) {
        if (fleetDefinition == null || fleetDefinition.isEmpty()) {
            throw new IllegalArgumentException("Fleet definition must not be empty.");
        }
        clearShips();
        Random random = new Random(seed);
        for (ShipType type : fleetDefinition) {
            for (int i = 1; i <= type.getCount(); i++) {
                boolean placed = false;
                int tries = 0;
                while (!placed && tries < 10_000) {
                    tries++;
                    Orientation orientation = random.nextBoolean() ? Orientation.HORIZONTAL : Orientation.VERTICAL;
                    int row = random.nextInt(size);
                    int col = random.nextInt(size);
                    Coordinate start = new Coordinate(row, col);
                    if (canPlaceShip(start, orientation, type.getLength())) {
                        placeShip(type.getName() + " #" + i, start, orientation, type.getLength());
                        placed = true;
                    }
                }
                if (!placed) {
                    throw new IllegalStateException("Unable to place all ships for " + type.getName() + ".");
                }
            }
        }
    }

    public ShotReport fireAt(Coordinate target) {
        Coordinate c = Coordinate.validateBounds(target, size);
        if (shotsTaken[c.getRow()][c.getColumn()]) {
            return new ShotReport(ShotResult.ALREADY_TARGETED, c, null, false);
        }

        shotsTaken[c.getRow()][c.getColumn()] = true;
        int shipIndex = shipIndexAtCell[c.getRow()][c.getColumn()];
        if (shipIndex == 0) {
            return new ShotReport(ShotResult.MISS, c, null, false);
        }

        SHIP ship = ships.get(shipIndex - 1);
        ship.registerHit(c);
        hitShipCells++;
        if (ship.isSunk()) {
            boolean won = isAllShipsSunk();
            return new ShotReport(ShotResult.SUNK, c, ship.getName(), won);
        }
        return new ShotReport(ShotResult.HIT, c, null, false);
    }

    public boolean isAllShipsSunk() {
        return placedShipCells > 0 && hitShipCells >= placedShipCells;
    }

    public boolean hasShotAt(Coordinate c) {
        Coordinate checked = Coordinate.validateBounds(c, size);
        return shotsTaken[checked.getRow()][checked.getColumn()];
    }

    public boolean hasShipAt(Coordinate c) {
        Coordinate checked = Coordinate.validateBounds(c, size);
        return shipIndexAtCell[checked.getRow()][checked.getColumn()] > 0;
    }

    public String renderOwnBoard() {
        return renderBoard(true);
    }

    public String renderOpponentView() {
        return renderBoard(false);
    }

    private String renderBoard(boolean ownBoard) {
        StringBuilder sb = new StringBuilder();
        sb.append("   ");
        for (int col = 1; col <= size; col++) {
            if (col < 10) {
                sb.append(" ");
            }
            sb.append(col).append(" ");
        }
        sb.append('\n');

        for (int row = 0; row < size; row++) {
            sb.append((char) ('A' + row)).append("  ");
            for (int col = 0; col < size; col++) {
                boolean shot = shotsTaken[row][col];
                boolean ship = shipIndexAtCell[row][col] > 0;
                char symbol;
                if (shot && ship) {
                    symbol = 'X';
                } else if (shot) {
                    symbol = 'o';
                } else if (ownBoard && ship) {
                    symbol = 'S';
                } else {
                    symbol = '.';
                }
                sb.append(" ").append(symbol).append(" ");
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    private List<Coordinate> getShipCells(Coordinate start, Orientation orientation, int length) {
        List<Coordinate> cells = new ArrayList<Coordinate>();
        for (int i = 0; i < length; i++) {
            int row = start.getRow() + (orientation == Orientation.VERTICAL ? i : 0);
            int column = start.getColumn() + (orientation == Orientation.HORIZONTAL ? i : 0);
            Coordinate c = new Coordinate(row, column);
            cells.add(Coordinate.validateBounds(c, size));
        }
        return cells;
    }

    private boolean hasAdjacentShip(Coordinate coordinate) {
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int rr = coordinate.getRow() + dr;
                int cc = coordinate.getColumn() + dc;
                if (rr < 0 || cc < 0 || rr >= size || cc >= size) {
                    continue;
                }
                if (shipIndexAtCell[rr][cc] > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private void clearShips() {
        ships.clear();
        placedShipCells = 0;
        hitShipCells = 0;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                shipIndexAtCell[row][col] = 0;
                shotsTaken[row][col] = false;
            }
        }
    }
}
