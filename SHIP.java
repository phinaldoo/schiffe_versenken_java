import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SHIP {
    private final String name;
    private final List<Coordinate> coordinates;
    private final boolean[] hits;

    public SHIP(String name, List<Coordinate> coordinates) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Ship name must not be empty.");
        }
        if (coordinates == null || coordinates.isEmpty()) {
            throw new IllegalArgumentException("Ship coordinates must not be empty.");
        }
        this.name = name.trim();
        this.coordinates = new ArrayList<Coordinate>(coordinates);
        this.hits = new boolean[coordinates.size()];
    }

    public String getName() {
        return name;
    }

    public List<Coordinate> getCoordinates() {
        return Collections.unmodifiableList(coordinates);
    }

    public int getLength() {
        return coordinates.size();
    }

    public boolean occupies(Coordinate coordinate) {
        return coordinates.contains(coordinate);
    }

    public boolean registerHit(Coordinate coordinate) {
        for (int i = 0; i < coordinates.size(); i++) {
            if (coordinates.get(i).equals(coordinate)) {
                hits[i] = true;
                return true;
            }
        }
        return false;
    }

    public boolean isSunk() {
        for (boolean hit : hits) {
            if (!hit) {
                return false;
            }
        }
        return true;
    }
}
