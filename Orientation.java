public enum Orientation {
    HORIZONTAL,
    VERTICAL;

    public static Orientation parse(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Orientation must not be null.");
        }
        String normalized = input.trim().toUpperCase();
        if (normalized.equals("H") || normalized.equals("HORIZONTAL")) {
            return HORIZONTAL;
        }
        if (normalized.equals("V") || normalized.equals("VERTICAL")) {
            return VERTICAL;
        }
        throw new IllegalArgumentException("Orientation must be H or V.");
    }
}
