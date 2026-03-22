public final class ShipType {
    private final String name;
    private final int length;
    private final int count;

    public ShipType(String name, int length, int count) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Ship name must not be empty.");
        }
        if (length < 2) {
            throw new IllegalArgumentException("Ship length must be at least 2.");
        }
        if (count < 1) {
            throw new IllegalArgumentException("Ship count must be at least 1.");
        }
        this.name = name.trim();
        this.length = length;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public int getLength() {
        return length;
    }

    public int getCount() {
        return count;
    }
}
