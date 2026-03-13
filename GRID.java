public class GRID
{
    private int[][] grid_matrix;
    private String username;
    private int ship_counter = 0;
    private SHIP[] ship_array;
    private boolean finished_placing_ships = false;
    private int max_ship_2;
    private int max_ship_3;
    private int max_ship_4;
    private int max_ship_5;
    private int current_ship_2 = 0;
    private int current_ship_3 = 0;
    private int current_ship_4 = 0;
    private int current_ship_5 = 0;

    public GRID(String username, int y_length, int x_length, int max_ship_count, int max_ship_2, int max_ship_3, int max_ship_4, int max_ship_5) throws Exception {
        username = username;
        ship_array = new SHIP[max_ship_count];
        grid_matrix = new int[y_length][x_length];
        max_ship_2 = max_ship_2;
        max_ship_3 = max_ship_3;
        max_ship_4 = max_ship_4;
        max_ship_5 = max_ship_5;
    }
    public void print_grid(boolean is_owned) {
        for (int i = 0; i < grid_matrix.length; i++) {
            for (int j = 0; j < grid_matrix[0].length; j++) {
                System.out.print(grid_matrix[i][j]);
            }          
            System.out.println();
        }
    }
    public int set_field(int x, int y){
        // Get the ship number of this field
        int ship_id = grid_matrix[y][x];
        if (ship_id < 1) {
            return -2;
        }
        SHIP ship = ship_array[ship_id-1]; 
        int ship_result = ship.set_field(x,y);
        System.out.println(ship_result);
        if (ship_result == 1) {
            if (check_win()) {
                return 10;
            }
        }
        return -1;
    }
    public String get_username() {
        return username;
    }
    public boolean check_win() {
        boolean win = true;
        for (int i = 0; i < ship_counter; i++) {
            SHIP temp_ship = ship_array[i];
            boolean ship_check_win = temp_ship.check_ship_finished();
            if (ship_check_win) {
                win = false;
            }
        }
        return false;
    }
    public int create_ship(int y_start, int y_end, int x_start, int x_end) throws Exception {
        if (!finished_placing_ships) {
            // Check if the length are valid and not out of reach
            if (y_end > (grid_matrix.length - 1)) {
                throw new Exception("Ship y coordinate out of reach");
            }
            if (x_end > (grid_matrix.length - 1)) {
                throw new Exception("Ship y coordinate out of reach");
            }
            int x_length = x_end - x_start;
            int y_length = y_end - y_start;
            if (x_length != 0 && y_length != 0) {
                throw new Exception("Ship can not be diagonal");
            }
            int ship_length;
            boolean vertical = false;
            if (x_length == 0) {
                ship_length = y_length;
                vertical = true;
            } else {
                ship_length = x_length;
            }
            if (vertical) {
                for (int i = 0; i <= ship_length; i++) {
                    int y_cord = y_start + i;
                    if (grid_matrix[y_cord][x_start] != 0) {
                        throw new Exception("Ship already exists for this position.");
                    }
                }
            } else {
                for (int i = 0; i <= ship_length; i++) {
                    int x_cord = y_start + i;
                    if (grid_matrix[y_start][x_cord] != 0) {
                        throw new Exception("Ship already exists for this position.");
                    }
                } 
            }
            // Ship can be placed
            // Create new ship object
            SHIP new_ship = new SHIP(y_start, y_end, x_start, x_end, ship_length);
            ship_array[ship_counter] = new_ship;
            ship_counter++;
            // Set the matrix to display the new ship
            if (vertical) {
                for (int i = 0; i <= ship_length; i++) {
                    int y_cord = y_start + i;
                    if (grid_matrix[y_cord][x_start] == 0) {
                        grid_matrix[y_cord][x_start] = ship_counter;
                    }
                }
            } else {
               for (int i = 0; i <= ship_length; i++) {
                    int x_cord = x_start + i;
                    if (grid_matrix[y_start][x_cord] == 0) {
                        grid_matrix[y_start][x_cord] = ship_counter;
                    }
                } 
            }
            return 1;
        } else {
            throw new Exception("Already finsihed creating ships!"); 
        }
    }
    public void setup() throws Exception {
        finished_placing_ships = true;
    }
}
