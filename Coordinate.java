import java.util.Objects;

public final class Coordinate {
    private final int row;
    private final int column;

    public Coordinate(int row, int column) {
        if (row < 0 || column < 0) {
            throw new IllegalArgumentException("Row and column must be >= 0.");
        }
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public static Coordinate parse(String input, int boardSize) {
        if (input == null) {
            throw new IllegalArgumentException("Coordinate input must not be null.");
        }

        String normalized = input.trim().toUpperCase();
        if (normalized.matches("^[A-Z]\\d+$")) {
            int row = normalized.charAt(0) - 'A';
            int column = Integer.parseInt(normalized.substring(1)) - 1;
            return validateBounds(new Coordinate(row, column), boardSize);
        }

        if (normalized.matches("^\\d+\\s*,\\s*\\d+$")) {
            String[] parts = normalized.split("\\s*,\\s*");
            int row = Integer.parseInt(parts[0]) - 1;
            int column = Integer.parseInt(parts[1]) - 1;
            return validateBounds(new Coordinate(row, column), boardSize);
        }

        throw new IllegalArgumentException("Invalid coordinate format. Use A1 or 1,1.");
    }

    public static Coordinate validateBounds(Coordinate coordinate, int boardSize) {
        if (boardSize < 2 || boardSize > 26) {
            throw new IllegalArgumentException("Board size must be between 2 and 26.");
        }
        if (coordinate.row >= boardSize || coordinate.column >= boardSize) {
            throw new IllegalArgumentException("Coordinate is outside the board.");
        }
        return coordinate;
    }

    public String toHumanReadable() {
        return String.valueOf((char) ('A' + row)) + (column + 1);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Coordinate)) {
            return false;
        }
        Coordinate that = (Coordinate) other;
        return row == that.row && column == that.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }
}
