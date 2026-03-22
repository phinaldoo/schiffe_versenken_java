import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final int DEFAULT_BOARD_SIZE = 10;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            printTitle();
            String player1 = readNonEmpty(scanner, "Spieler 1 Name: ");
            String player2 = readNonEmpty(scanner, "Spieler 2 Name: ");
            List<ShipType> fleet = defaultFleet();

            GAME game = new GAME(player1, player2, DEFAULT_BOARD_SIZE, fleet);

            setupPlayer(scanner, game, 0);
            setupPlayer(scanner, game, 1);

            runGameLoop(scanner, game);
        } finally {
            scanner.close();
        }
    }

    private static void printTitle() {
        System.out.println("==========================================");
        System.out.println("         SCHIFFE VERSENKEN (KONSOLE)      ");
        System.out.println("==========================================");
        System.out.println("Eingabeformat Koordinate: A1 oder 1,1");
        System.out.println("Treffer: X | Fehlschuss: o | Schiff: S");
        System.out.println();
    }

    private static List<ShipType> defaultFleet() {
        List<ShipType> fleet = new ArrayList<ShipType>();
        fleet.add(new ShipType("Patrouillenboot", 2, 1));
        fleet.add(new ShipType("U-Boot", 3, 2));
        fleet.add(new ShipType("Zerstoerer", 4, 1));
        fleet.add(new ShipType("Schlachtschiff", 5, 1));
        return fleet;
    }

    private static void setupPlayer(Scanner scanner, GAME game, int playerIndex) {
        GRID grid = game.getPlayerGrid(playerIndex);
        System.out.println("------------------------------------------");
        System.out.println("Setup fuer " + grid.getUsername());
        System.out.println("------------------------------------------");
        System.out.println("Modus waehlen: [1] Manuell [2] Zufaellig");

        int mode = readIntInRange(scanner, "Auswahl (1/2): ", 1, 2);
        if (mode == 2) {
            long seed = System.nanoTime() + playerIndex * 97L;
            grid.placeShipsRandomly(game.getFleetDefinition(), seed);
            System.out.println("Schiffe zufaellig platziert.");
            System.out.println(grid.renderOwnBoard());
            waitForEnter(scanner, "Weiter mit Enter...");
            clearScreenHint();
            return;
        }

        for (ShipType type : game.getFleetDefinition()) {
            for (int i = 1; i <= type.getCount(); i++) {
                while (true) {
                    try {
                        System.out.println(grid.renderOwnBoard());
                        System.out.println("Platziere " + type.getName() + " #" + i + " (Laenge " + type.getLength() + ")");
                        Coordinate start = readCoordinate(scanner, grid.getSize(), "Start (z.B. A1): ");
                        Orientation orientation = readOrientation(scanner, "Richtung [H/V]: ");
                        grid.placeShip(type.getName() + " #" + i, start, orientation, type.getLength());
                        break;
                    } catch (IllegalArgumentException ex) {
                        System.out.println("Fehler: " + ex.getMessage());
                    }
                }
            }
        }
        System.out.println("Setup abgeschlossen fuer " + grid.getUsername());
        System.out.println(grid.renderOwnBoard());
        waitForEnter(scanner, "Weiter mit Enter...");
        clearScreenHint();
    }

    private static void runGameLoop(Scanner scanner, GAME game) {
        while (!game.isGameOver()) {
            GRID ownGrid = game.getActivePlayerGrid();
            GRID enemyGrid = game.getOpponentGrid();

            System.out.println("------------------------------------------");
            System.out.println("Am Zug: " + ownGrid.getUsername());
            System.out.println("------------------------------------------");
            System.out.println("Dein Feld:");
            System.out.println(ownGrid.renderOwnBoard());
            System.out.println("Gegnerisches Feld:");
            System.out.println(enemyGrid.renderOpponentView());

            ShotReport report = null;
            while (report == null) {
                try {
                    Coordinate target = readCoordinate(scanner, enemyGrid.getSize(), "Schussziel: ");
                    report = game.playTurn(target);
                    announceShot(report);
                    if (report.getResult() == ShotResult.ALREADY_TARGETED) {
                        report = null;
                    }
                } catch (IllegalArgumentException ex) {
                    System.out.println("Fehler: " + ex.getMessage());
                }
            }

            if (!game.isGameOver()) {
                waitForEnter(scanner, "Zug beendet. Enter fuer Spielerwechsel...");
                clearScreenHint();
                game.switchTurn();
            }
        }

        System.out.println("==========================================");
        System.out.println("Spielende! Gewinner: " + game.getWinnerName());
        System.out.println("==========================================");
    }

    private static void announceShot(ShotReport report) {
        ShotResult result = report.getResult();
        if (result == ShotResult.MISS) {
            System.out.println(report.getCoordinate().toHumanReadable() + ": Daneben.");
            return;
        }
        if (result == ShotResult.HIT) {
            System.out.println(report.getCoordinate().toHumanReadable() + ": Treffer!");
            return;
        }
        if (result == ShotResult.SUNK) {
            System.out.println(report.getCoordinate().toHumanReadable() + ": Schiff versenkt! (" + report.getSunkShipName() + ")");
            if (report.isGameWon()) {
                System.out.println("Alle gegnerischen Schiffe wurden versenkt.");
            }
            return;
        }
        System.out.println(report.getCoordinate().toHumanReadable() + ": Feld wurde bereits beschossen.");
    }

    private static Orientation readOrientation(Scanner scanner, String prompt) {
        while (true) {
            try {
                return Orientation.parse(readNonEmpty(scanner, prompt));
            } catch (IllegalArgumentException ex) {
                System.out.println("Fehler: " + ex.getMessage());
            }
        }
    }

    private static Coordinate readCoordinate(Scanner scanner, int boardSize, String prompt) {
        while (true) {
            try {
                return Coordinate.parse(readNonEmpty(scanner, prompt), boardSize);
            } catch (IllegalArgumentException ex) {
                System.out.println("Fehler: " + ex.getMessage());
            }
        }
    }

    private static int readIntInRange(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            String input = readNonEmpty(scanner, prompt);
            try {
                int value = Integer.parseInt(input);
                if (value < min || value > max) {
                    throw new IllegalArgumentException("Wert ausserhalb des erlaubten Bereichs.");
                }
                return value;
            } catch (NumberFormatException ex) {
                System.out.println("Bitte eine Zahl eingeben.");
            } catch (IllegalArgumentException ex) {
                System.out.println("Fehler: " + ex.getMessage());
            }
        }
    }

    private static String readNonEmpty(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine();
            if (value != null && !value.trim().isEmpty()) {
                return value.trim();
            }
            System.out.println("Eingabe darf nicht leer sein.");
        }
    }

    private static void waitForEnter(Scanner scanner, String message) {
        System.out.print(message);
        scanner.nextLine();
    }

    private static void clearScreenHint() {
        for (int i = 0; i < 30; i++) {
            System.out.println();
        }
    }
}
