public class SHIP {
    public int y_start;
    public int y_end;
    public int x_start;
    public int x_end;
    private int[] ship_array;
    private boolean completely_destroyed = false;
    public SHIP(int y_start, int y_end, int x_start, int x_end, int ship_length) throws Exception {
        // Instanzvariable initialisieren
        y_start = y_start;
        y_end = y_end;
        x_start = x_start;
        x_end = x_end;
        ship_array = new int[ship_length];
        for (int i = 0; i < ship_array.length; i++) {
            ship_array[i] = 0;
        }
    }
    public int[] get_ship_values() {
        return ship_array;
    }
    public boolean check_ship_finished() {
        if (completely_destroyed) {
            return true;
        }
        for (int i = 0; i < ship_array.length; i++) {
            if (ship_array[i] > 0) {
                return false;
            }
        }
        return true; 
    }
    public int set_field(int x_cord, int y_cord) {
        // If ship is completely discovered, then it returns -1
        // If ship is not completely discovered, then it returns 1
        boolean vertical = false;
        // Check if the ship is vertical
        int y_length = y_end - y_start;
        int x_length = x_end - x_start;
        if (x_length == 0) {
            vertical = true;
        }
        int ship_array_index = 0;
        if (vertical) {
            ship_array_index = y_cord - y_start;
        } else {
            ship_array_index = x_cord - x_start;
        }
        System.out.println(ship_array_index);
        ship_array[ship_array_index] = -1;
        if (!check_ship_finished()) {
            return 1;
        }
        return -1;
    }
}