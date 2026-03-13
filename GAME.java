
/**
 * Beschreiben Sie hier die Klasse GAME.
 * 
 * @author (Ihr Name) 
 * @version (eine Versionsnummer oder ein Datum)
 */
public class GAME
{
    // Instanzvariablen - ersetzen Sie das folgende Beispiel mit Ihren Variablen
    
    private GRID user1_grid;
    private GRID user2_grid;
    private String username1;
    private String username2;
    private int max_ship_2;
    private int max_ship_3;
    private int max_ship_4;
    private int max_ship_5;
    private int turn = 0; // 0 = user_1, 1 = user_2
    private boolean user1_setup_completed = false;
    private boolean user2_setup_completed = false;
    private int round_counter = 0;
    /**
     * Konstruktor für Objekte der Klasse GAME
     */
    public GAME(String username1, String username2, int max_ship_2, int max_ship_3, int max_ship_4, int max_ship_5) throws Exception{
        username1 = username1;
        username2 = username2;
        max_ship_2 = max_ship_2;
        max_ship_3 = max_ship_3;
        max_ship_4 = max_ship_4;
        max_ship_5 = max_ship_5;
        user1_grid = new GRID(username1, 10, 10, 5, max_ship_2, max_ship_3, max_ship_4, max_ship_5);
        user2_grid = new GRID(username2, 10, 10, 5, max_ship_2, max_ship_3, max_ship_4, max_ship_5);
    }

    /**
     * Ein Beispiel einer Methode - ersetzen Sie diesen Kommentar mit Ihrem eigenen
     * 
     * @param  y    ein Beispielparameter für eine Methode
     * @return        die Summe aus x und y
     */
    public void print_for_user(int user_id) {
        if (user_id == 0) {
            // Print grid 1 with details
            // user1_grid.print_grid_ship_values(true);
            // print grid 2 without details
            // user2_grid.print_grid_ship_values(false);
        }
    }
    public int check_turn() throws Exception {
        if (!user1_setup_completed && !user2_setup_completed) {
            throw new Exception("Setup not completed yet");
        }
        return turn;
    }
    public void create_ship(int user_id, int y_start, int y_end, int x_start, int x_end) {
        if (user_id != 0 || user_id != 1) {
            throw new Exception("Invalid user_id"); 
        }
        if (user_id == 0) {
            user1_grid.create_ship(y_start, y_end, x_start, x_end);
        } else if (user_id == 1) {
            user2_grid.create_ship(y_start, y_end, x_start, x_end);
        }
    }
    public void setup(int user_id) throws Exception {
        if (user_id != 0 || user_id != 1) {
            throw new Exception("Invalid user_id"); 
        }
        if (user_id == 0) {
            if (!user1_setup_completed) {
                user1_grid.setup();
                user1_setup_completed = true;
            } else {
                throw new Exception("User 1 has already compelteld setup");
            }
        } else if (user_id == 1) {
            if (!user2_setup_completed) {
                user2_grid.setup();
                user2_setup_completed = true;
            } else {
                throw new Exception("User 2 has already compelteld setup");
            }
        }
    }
}
