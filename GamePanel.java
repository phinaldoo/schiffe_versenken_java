import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Main game panel showing both boards and turn management.
 */
public class GamePanel extends JPanel {
    
    private BattleshipGUI parent;
    private BoardPanel ownBoard;
    private BoardPanel enemyBoard;
    private JLabel turnLabel;
    private JLabel statusLabel;
    private JPanel messagePanel;
    private JLabel messageLabel;
    private ModernButton endTurnButton;
    
    private boolean canShoot = true;
    private Timer messageTimer;
    
    public GamePanel(BattleshipGUI parent) {
        this.parent = parent;
        
        setLayout(new BorderLayout(20, 20));
        setBackground(BattleshipGUI.OCEAN_DARK);
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Center with two boards
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);
        
        // Message panel (shows shot results)
        messagePanel = createMessagePanel();
        add(messagePanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        turnLabel = new JLabel("Spieler am Zug");
        turnLabel.setFont(BattleshipGUI.TITLE_FONT);
        turnLabel.setForeground(BattleshipGUI.TEXT_PRIMARY);
        turnLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        statusLabel = new JLabel("Wähle ein Ziel auf dem gegnerischen Feld");
        statusLabel.setFont(BattleshipGUI.LABEL_FONT);
        statusLabel.setForeground(BattleshipGUI.TEXT_SECONDARY);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        turnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(turnLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        centerPanel.add(statusLabel);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 30, 10, 30);
        gbc.fill = GridBagConstraints.NONE;
        
        // Own board (left)
        JPanel ownBoardCard = createBoardCard("Dein Feld", false);
        ownBoard = (BoardPanel) ((JPanel)ownBoardCard.getComponent(1)).getComponent(0);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(ownBoardCard, gbc);
        
        // VS divider
        JLabel vsLabel = new JLabel("VS");
        vsLabel.setFont(BattleshipGUI.TITLE_FONT.deriveFont(42f));
        vsLabel.setForeground(BattleshipGUI.ACCENT_COLOR);
        gbc.gridx = 1;
        panel.add(vsLabel, gbc);
        
        // Enemy board (right)
        JPanel enemyBoardCard = createBoardCard("Gegnerisches Feld", true);
        enemyBoard = (BoardPanel) ((JPanel)enemyBoardCard.getComponent(1)).getComponent(0);
        
        gbc.gridx = 2;
        panel.add(enemyBoardCard, gbc);
        
        return panel;
    }
    
    private JPanel createBoardCard(String title, boolean isEnemy) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Card background
                g2d.setColor(BattleshipGUI.CARD_BG);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Border
                Color borderColor = isEnemy ? BattleshipGUI.HIT_COLOR : BattleshipGUI.ACCENT_COLOR;
                g2d.setColor(new Color(borderColor.getRed(), borderColor.getGreen(), 
                    borderColor.getBlue(), 100));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);
                
                g2d.dispose();
            }
        };
        card.setLayout(new BorderLayout(0, 10));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(BattleshipGUI.HEADER_FONT);
        titleLabel.setForeground(BattleshipGUI.TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(titleLabel, BorderLayout.NORTH);
        
        // Board
        BoardPanel board = new BoardPanel(parent.getBoardSize(), isEnemy);
        if (isEnemy) {
            board.setInteractive(true);
            board.setClickListener((row, col) -> handleShot(row, col));
        }
        
        JPanel boardWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        boardWrapper.setOpaque(false);
        boardWrapper.add(board);
        card.add(boardWrapper, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createMessagePanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(BattleshipGUI.PANEL_BG);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.dispose();
            }
        };
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 15));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, 70));
        
        messageLabel = new JLabel("Spiel gestartet - Viel Erfolg!");
        messageLabel.setFont(BattleshipGUI.HEADER_FONT);
        messageLabel.setForeground(BattleshipGUI.TEXT_PRIMARY);
        
        endTurnButton = new ModernButton("Zug beenden", BattleshipGUI.ACCENT_COLOR);
        endTurnButton.setVisible(false);
        endTurnButton.addActionListener(e -> endTurn());
        
        panel.add(messageLabel);
        panel.add(endTurnButton);
        
        return panel;
    }
    
    public void startGame() {
        refreshBoards();
        canShoot = true;
        endTurnButton.setVisible(false);
        updateTurnInfo();
    }
    
    public void refreshBoards() {
        GAME game = parent.getGame();
        ownBoard.setGrid(game.getActivePlayerGrid());
        enemyBoard.setGrid(game.getOpponentGrid());
        
        canShoot = true;
        endTurnButton.setVisible(false);
        updateTurnInfo();
        
        ownBoard.repaint();
        enemyBoard.repaint();
    }
    
    private void updateTurnInfo() {
        GAME game = parent.getGame();
        turnLabel.setText(game.getActivePlayerName() + " ist am Zug");
        statusLabel.setText("Wähle ein Ziel auf dem gegnerischen Feld");
    }
    
    private void handleShot(int row, int col) {
        if (!canShoot) return;
        
        GAME game = parent.getGame();
        Coordinate target = new Coordinate(row, col);
        
        // Check if already shot
        if (game.getOpponentGrid().hasShotAt(target)) {
            showMessage("Dieses Feld wurde bereits beschossen!", BattleshipGUI.WARNING_COLOR);
            return;
        }
        
        // Fire!
        ShotReport report = game.playTurn(target);
        enemyBoard.repaint();
        
        // Show result
        switch (report.getResult()) {
            case MISS:
                showMessage(target.toHumanReadable() + " - Daneben!", BattleshipGUI.MISS_COLOR);
                canShoot = false;
                endTurnButton.setVisible(true);
                break;
                
            case HIT:
                showMessage(target.toHumanReadable() + " - TREFFER!", BattleshipGUI.HIT_COLOR);
                statusLabel.setText("Gut getroffen! Schieße weiter...");
                // Can shoot again
                break;
                
            case SUNK:
                if (report.isGameWon()) {
                    showMessage(report.getSunkShipName() + " versenkt! SIEG!", BattleshipGUI.SUCCESS_COLOR);
                    canShoot = false;
                    
                    // Delay before showing game over
                    SwingUtilities.invokeLater(() -> {
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {}
                        parent.showGameOver(game.getWinnerName());
                    });
                } else {
                    showMessage(report.getSunkShipName() + " VERSENKT!", BattleshipGUI.SUCCESS_COLOR);
                    statusLabel.setText("Schiff versenkt! Weiter schießen...");
                }
                break;
                
            default:
                break;
        }
    }
    
    private void showMessage(String message, Color color) {
        messageLabel.setText(message);
        messageLabel.setForeground(color);
        
        // Animate the message
        messagePanel.repaint();
    }
    
    private void endTurn() {
        GAME game = parent.getGame();
        game.switchTurn();
        parent.endTurn();
    }
}
