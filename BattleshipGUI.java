import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Main GUI class for Schiffe Versenken (Battleship) game.
 * Modern, clean design with smooth animations and beautiful visuals.
 */
public class BattleshipGUI extends JFrame {
    
    // Color scheme - Modern ocean theme
    public static final Color OCEAN_DARK = new Color(15, 32, 56);
    public static final Color OCEAN_MEDIUM = new Color(22, 54, 92);
    public static final Color OCEAN_LIGHT = new Color(32, 78, 135);
    public static final Color WATER_COLOR = new Color(47, 109, 176);
    public static final Color WATER_HOVER = new Color(64, 133, 198);
    public static final Color SHIP_COLOR = new Color(85, 98, 112);
    public static final Color SHIP_HOVER = new Color(105, 118, 132);
    public static final Color HIT_COLOR = new Color(220, 53, 69);
    public static final Color HIT_GLOW = new Color(255, 100, 100);
    public static final Color MISS_COLOR = new Color(108, 117, 125);
    public static final Color SUNK_COLOR = new Color(139, 0, 0);
    public static final Color ACCENT_COLOR = new Color(0, 188, 212);
    public static final Color ACCENT_HOVER = new Color(38, 198, 218);
    public static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    public static final Color WARNING_COLOR = new Color(241, 196, 15);
    public static final Color TEXT_PRIMARY = new Color(236, 240, 241);
    public static final Color TEXT_SECONDARY = new Color(189, 195, 199);
    public static final Color PANEL_BG = new Color(25, 42, 68);
    public static final Color CARD_BG = new Color(35, 55, 85);
    
    // Fonts
    public static final Font TITLE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 32);
    public static final Font HEADER_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 19);
    public static final Font LABEL_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
    public static final Font BUTTON_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 16);
    public static final Font SMALL_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    // Screens
    private WelcomePanel welcomePanel;
    private SetupPanel setupPanel;
    private GamePanel gamePanel;
    private ResultPanel resultPanel;
    
    // Game state
    private GAME game;
    private String player1Name;
    private String player2Name;
    private List<ShipType> fleetDefinition;
    private int boardSize = 10;
    
    public BattleshipGUI() {
        setTitle("Schiffe Versenken");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 800));
        setPreferredSize(new Dimension(1400, 900));
        
        // Setup main card layout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(OCEAN_DARK);
        
        // Initialize fleet
        fleetDefinition = defaultFleet();
        
        // Create screens
        welcomePanel = new WelcomePanel(this);
        setupPanel = new SetupPanel(this);
        gamePanel = new GamePanel(this);
        resultPanel = new ResultPanel(this);
        
        mainPanel.add(welcomePanel, "welcome");
        mainPanel.add(setupPanel, "setup");
        mainPanel.add(gamePanel, "game");
        mainPanel.add(resultPanel, "result");
        
        add(mainPanel);
        
        // Center on screen
        pack();
        setLocationRelativeTo(null);
        
        // Start with welcome screen
        showWelcome();
    }
    
    private List<ShipType> defaultFleet() {
        List<ShipType> fleet = new ArrayList<>();
        fleet.add(new ShipType("Patrouillenboot", 2, 1));
        fleet.add(new ShipType("U-Boot", 3, 2));
        fleet.add(new ShipType("Zerstörer", 4, 1));
        fleet.add(new ShipType("Schlachtschiff", 5, 1));
        return fleet;
    }
    
    public void showWelcome() {
        cardLayout.show(mainPanel, "welcome");
    }
    
    public void startGame(String p1Name, String p2Name) {
        this.player1Name = p1Name;
        this.player2Name = p2Name;
        this.game = new GAME(player1Name, player2Name, boardSize, fleetDefinition);
        setupPanel.startSetup(0);
        cardLayout.show(mainPanel, "setup");
    }
    
    public void setupComplete(int playerIndex) {
        if (playerIndex == 0) {
            // Show transition, then setup player 2
            showTransitionScreen("Spieler 1 Setup komplett!", 
                "Weiter zu " + player2Name, () -> {
                setupPanel.startSetup(1);
            });
        } else {
            // Both players ready, start the game
            showTransitionScreen("Beide Spieler bereit!", 
                "Spiel beginnt...", () -> {
                gamePanel.startGame();
                cardLayout.show(mainPanel, "game");
            });
        }
    }
    
    private void showTransitionScreen(String message, String subMessage, Runnable onComplete) {
        JDialog transition = new JDialog(this, true);
        transition.setUndecorated(true);
        transition.setSize(500, 300);
        transition.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gp = new GradientPaint(0, 0, OCEAN_DARK, getWidth(), getHeight(), OCEAN_MEDIUM);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                
                // Border
                g2d.setColor(ACCENT_COLOR);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 28, 28);
                
                g2d.dispose();
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        JLabel msgLabel = new JLabel(message);
        msgLabel.setFont(TITLE_FONT);
        msgLabel.setForeground(TEXT_PRIMARY);
        msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subLabel = new JLabel(subMessage);
        subLabel.setFont(HEADER_FONT);
        subLabel.setForeground(TEXT_SECONDARY);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        ModernButton continueBtn = new ModernButton("Weiter", ACCENT_COLOR);
        continueBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        continueBtn.addActionListener(e -> {
            transition.dispose();
            SwingUtilities.invokeLater(onComplete);
        });
        
        panel.add(Box.createVerticalGlue());
        panel.add(msgLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(subLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 40)));
        panel.add(continueBtn);
        panel.add(Box.createVerticalGlue());
        
        transition.add(panel);
        transition.setVisible(true);
    }
    
    public void endTurn() {
        showTransitionScreen("Zug beendet", 
            game.getActivePlayerName() + " ist am Zug", () -> {
            gamePanel.refreshBoards();
        });
    }
    
    public void showGameOver(String winner) {
        resultPanel.showResult(winner);
        cardLayout.show(mainPanel, "result");
    }
    
    public void newGame() {
        game = null;
        player1Name = null;
        player2Name = null;
        setupPanel.resetState();
        gamePanel.resetState();
        resultPanel.stopCelebration();
        showWelcome();
    }
    
    public void showSettingsDialog() {
        JDialog dialog = createSettingsDialog();
        dialog.setVisible(true);
    }
    
    public void leaveCurrentGame() {
        newGame();
    }
    
    public void closeGame() {
        dispose();
        System.exit(0);
    }
    
    public GAME getGame() {
        return game;
    }
    
    public List<ShipType> getFleetDefinition() {
        return fleetDefinition;
    }
    
    public int getBoardSize() {
        return boardSize;
    }
    
    private JDialog createSettingsDialog() {
        JDialog dialog = new JDialog(this, true);
        dialog.setUndecorated(true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);
        dialog.setSize(440, 330);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gp = new GradientPaint(0, 0, OCEAN_DARK, getWidth(), getHeight(), OCEAN_MEDIUM);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
                
                g2d.setColor(new Color(ACCENT_COLOR.getRed(), ACCENT_COLOR.getGreen(), ACCENT_COLOR.getBlue(), 130));
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 28, 28);
                g2d.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(22, 28, 28, 28));
        
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Einstellungen");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_PRIMARY);
        
        JButton closeButton = createDialogCloseButton();
        closeButton.addActionListener(e -> dialog.dispose());
        
        titleBar.add(titleLabel, BorderLayout.WEST);
        titleBar.add(closeButton, BorderLayout.EAST);
        
        JLabel descriptionLabel = new JLabel("<html>Verwalte das laufende Spiel oder schliesse die Anwendung.</html>");
        descriptionLabel.setFont(LABEL_FONT);
        descriptionLabel.setForeground(TEXT_SECONDARY);
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        ModernButton leaveButton = new ModernButton("Spiel verlassen", WARNING_COLOR);
        leaveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        leaveButton.addActionListener(e -> {
            dialog.dispose();
            confirmLeaveCurrentGame();
        });
        
        JLabel leaveHint = createDialogHintLabel("Beendet das aktuelle Match und bringt dich zum Startbildschirm.");
        
        ModernButton closeGameButton = new ModernButton("Spiel schliessen", HIT_COLOR);
        closeGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeGameButton.addActionListener(e -> {
            dialog.dispose();
            confirmCloseGame();
        });
        
        JLabel closeHint = createDialogHintLabel("Beendet die komplette Anwendung.");
        
        ModernButton resumeButton = new ModernButton("Zurueck", ACCENT_COLOR);
        resumeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resumeButton.addActionListener(e -> dialog.dispose());
        
        panel.add(titleBar);
        panel.add(Box.createRigidArea(new Dimension(0, 14)));
        panel.add(descriptionLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 26)));
        panel.add(leaveButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(leaveHint);
        panel.add(Box.createRigidArea(new Dimension(0, 18)));
        panel.add(closeGameButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(closeHint);
        panel.add(Box.createVerticalGlue());
        panel.add(resumeButton);
        
        dialog.setContentPane(panel);
        dialog.getRootPane().setDefaultButton(resumeButton);
        dialog.getRootPane().registerKeyboardAction(
            e -> dialog.dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        return dialog;
    }
    
    private JButton createDialogCloseButton() {
        JButton button = new JButton("x");
        button.setFont(HEADER_FONT);
        button.setForeground(TEXT_PRIMARY);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private JLabel createDialogHintLabel(String text) {
        JLabel label = new JLabel("<html><div style='width: 320px;'>" + text + "</div></html>");
        label.setFont(SMALL_FONT);
        label.setForeground(TEXT_SECONDARY);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }
    
    private void confirmLeaveCurrentGame() {
        int selection = JOptionPane.showOptionDialog(
            this,
            "Das aktuelle Spiel wird beendet und du kehrst zum Startbildschirm zurueck.",
            "Spiel verlassen?",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null,
            new Object[]{"Spiel verlassen", "Abbrechen"},
            "Abbrechen"
        );
        
        if (selection == JOptionPane.YES_OPTION) {
            leaveCurrentGame();
        }
    }
    
    private void confirmCloseGame() {
        int selection = JOptionPane.showOptionDialog(
            this,
            "Die Anwendung wird sofort geschlossen.",
            "Spiel schliessen?",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null,
            new Object[]{"Spiel schliessen", "Abbrechen"},
            "Abbrechen"
        );
        
        if (selection == JOptionPane.YES_OPTION) {
            closeGame();
        }
    }
    
    public static void main(String[] args) {
        // Set system look and feel improvements
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default
        }

        setGlobalUIFont(BattleshipGUI.LABEL_FONT);
        
        SwingUtilities.invokeLater(() -> {
            BattleshipGUI gui = new BattleshipGUI();
            gui.setVisible(true);
        });
    }

    private static void setGlobalUIFont(Font font) {
        FontUIResource resource = new FontUIResource(font);
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, resource);
            }
        }
    }
}
