import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Ship placement setup screen with interactive board.
 */
public class SetupPanel extends JPanel {
    
    private BattleshipGUI parent;
    private BoardPanel boardPanel;
    private JLabel titleLabel;
    private JLabel instructionLabel;
    private JLabel shipInfoLabel;
    private JPanel shipListPanel;
    private ModernButton rotateButton;
    private ModernButton randomButton;
    private ModernButton confirmButton;
    private ModernButton settingsButton;
    
    private int currentPlayerIndex;
    private GRID currentGrid;
    private int currentShipTypeIndex;
    private int currentShipNumber;
    private Orientation currentOrientation = Orientation.HORIZONTAL;
    private List<ShipType> fleet;
    
    public SetupPanel(BattleshipGUI parent) {
        this.parent = parent;
        this.fleet = parent.getFleetDefinition();
        
        setLayout(new BorderLayout(20, 20));
        setBackground(BattleshipGUI.OCEAN_DARK);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Center content
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom controls
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        titleLabel = new JLabel("Schiffe platzieren");
        titleLabel.setFont(BattleshipGUI.TITLE_FONT);
        titleLabel.setForeground(BattleshipGUI.TEXT_PRIMARY);
        
        instructionLabel = new JLabel("Klicke auf das Spielfeld um dein Schiff zu platzieren");
        instructionLabel.setFont(BattleshipGUI.LABEL_FONT);
        instructionLabel.setForeground(BattleshipGUI.TEXT_SECONDARY);
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        settingsButton = new ModernButton("Einstellungen", BattleshipGUI.OCEAN_LIGHT);
        settingsButton.addActionListener(e -> parent.showSettingsDialog());
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        textPanel.add(instructionLabel);
        
        panel.add(textPanel, BorderLayout.CENTER);
        panel.add(settingsButton, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        
        // Board panel
        boardPanel = new BoardPanel(parent.getBoardSize(), false);
        boardPanel.setInteractive(true);
        boardPanel.setClickListener((row, col) -> handleCellClick(row, col));
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(boardPanel, gbc);
        
        // Ship info panel
        JPanel infoPanel = createShipInfoPanel();
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(infoPanel, gbc);
        
        return panel;
    }
    
    private JPanel createShipInfoPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(BattleshipGUI.CARD_BG);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.setColor(BattleshipGUI.OCEAN_LIGHT);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2d.dispose();
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        panel.setPreferredSize(new Dimension(280, 400));
        
        // Current ship info
        JLabel shipTitle = new JLabel("Aktuelles Schiff");
        shipTitle.setFont(BattleshipGUI.HEADER_FONT);
        shipTitle.setForeground(BattleshipGUI.TEXT_PRIMARY);
        shipTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        shipInfoLabel = new JLabel("Warte...");
        shipInfoLabel.setFont(BattleshipGUI.LABEL_FONT);
        shipInfoLabel.setForeground(BattleshipGUI.ACCENT_COLOR);
        shipInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Rotate button
        rotateButton = new ModernButton("Drehen (R)", BattleshipGUI.OCEAN_LIGHT);
        rotateButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        rotateButton.addActionListener(e -> rotateShip());
        
        // Ship list
        JLabel listTitle = new JLabel("Flotte");
        listTitle.setFont(BattleshipGUI.HEADER_FONT);
        listTitle.setForeground(BattleshipGUI.TEXT_PRIMARY);
        listTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        shipListPanel = new JPanel();
        shipListPanel.setLayout(new BoxLayout(shipListPanel, BoxLayout.Y_AXIS));
        shipListPanel.setOpaque(false);
        shipListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(shipTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(shipInfoLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(rotateButton);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(listTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(shipListPanel);
        panel.add(Box.createVerticalGlue());
        
        // Keyboard shortcut for rotation
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('r'), "rotate");
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('R'), "rotate");
        getActionMap().put("rotate", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rotateShip();
            }
        });
        
        return panel;
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panel.setOpaque(false);
        
        randomButton = new ModernButton("Zufällig platzieren", BattleshipGUI.WARNING_COLOR);
        randomButton.addActionListener(e -> placeRandomly());
        
        confirmButton = new ModernButton("Bestaetigen", BattleshipGUI.SUCCESS_COLOR);
        confirmButton.setEnabled(false);
        confirmButton.addActionListener(e -> confirmSetup());
        
        panel.add(randomButton);
        panel.add(confirmButton);
        
        return panel;
    }
    
    public void startSetup(int playerIndex) {
        this.currentPlayerIndex = playerIndex;
        this.currentGrid = parent.getGame().getPlayerGrid(playerIndex);
        this.currentShipTypeIndex = 0;
        this.currentShipNumber = 1;
        this.currentOrientation = Orientation.HORIZONTAL;
        
        titleLabel.setText("Schiffe platzieren - " + currentGrid.getUsername());
        boardPanel.setGrid(currentGrid);
        
        updateShipInfo();
        updateShipList();
        confirmButton.setEnabled(false);
    }
    
    private void updateShipInfo() {
        if (currentShipTypeIndex < fleet.size()) {
            ShipType type = fleet.get(currentShipTypeIndex);
            shipInfoLabel.setText(String.format("%s #%d (Länge: %d)", 
                type.getName(), currentShipNumber, type.getLength()));
            
            boardPanel.setPlacementMode(true, type.getLength(), currentOrientation);
            rotateButton.setEnabled(true);
        } else {
            shipInfoLabel.setText("Alle Schiffe platziert!");
            boardPanel.setPlacementMode(false, 0, currentOrientation);
            rotateButton.setEnabled(false);
            confirmButton.setEnabled(true);
        }
    }
    
    private void updateShipList() {
        shipListPanel.removeAll();
        
        int shipIndex = 0;
        for (ShipType type : fleet) {
            for (int i = 1; i <= type.getCount(); i++) {
                boolean isPlaced = isShipPlaced(shipIndex);
                boolean isCurrent = (shipIndex == getCurrentShipIndex());
                
                JLabel shipLabel = new JLabel(String.format("%s %s #%d (x%d)", 
                    isPlaced ? "OK" : (isCurrent ? ">" : "o"),
                    type.getName(), i, type.getLength()));
                shipLabel.setFont(BattleshipGUI.SMALL_FONT);
                shipLabel.setForeground(isPlaced ? BattleshipGUI.SUCCESS_COLOR : 
                    (isCurrent ? BattleshipGUI.ACCENT_COLOR : BattleshipGUI.TEXT_SECONDARY));
                shipLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                shipLabel.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
                
                shipListPanel.add(shipLabel);
                shipIndex++;
            }
        }
        
        shipListPanel.revalidate();
        shipListPanel.repaint();
    }
    
    private int getCurrentShipIndex() {
        int index = 0;
        for (int t = 0; t < currentShipTypeIndex; t++) {
            index += fleet.get(t).getCount();
        }
        return index + currentShipNumber - 1;
    }
    
    private boolean isShipPlaced(int globalIndex) {
        int currentIndex = getCurrentShipIndex();
        return globalIndex < currentIndex;
    }
    
    private void handleCellClick(int row, int col) {
        if (currentShipTypeIndex >= fleet.size()) return;
        
        ShipType type = fleet.get(currentShipTypeIndex);
        Coordinate start = new Coordinate(row, col);
        
        try {
            if (currentGrid.canPlaceShip(start, currentOrientation, type.getLength())) {
                currentGrid.placeShip(type.getName() + " #" + currentShipNumber, 
                    start, currentOrientation, type.getLength());
                
                // Move to next ship
                currentShipNumber++;
                if (currentShipNumber > type.getCount()) {
                    currentShipTypeIndex++;
                    currentShipNumber = 1;
                }
                
                boardPanel.repaint();
                updateShipInfo();
                updateShipList();
            }
        } catch (IllegalArgumentException e) {
            // Invalid placement, ignore
        }
    }
    
    private void rotateShip() {
        currentOrientation = (currentOrientation == Orientation.HORIZONTAL) ? 
            Orientation.VERTICAL : Orientation.HORIZONTAL;
        boardPanel.setPlacementOrientation(currentOrientation);
        
        String orientText = currentOrientation == Orientation.HORIZONTAL ? "Horizontal" : "Vertikal";
        instructionLabel.setText("Ausrichtung: " + orientText + " - Klicke zum Platzieren");
    }
    
    private void placeRandomly() {
        long seed = System.nanoTime() + currentPlayerIndex * 97L;
        currentGrid.placeShipsRandomly(fleet, seed);
        
        // Mark all ships as placed
        currentShipTypeIndex = fleet.size();
        currentShipNumber = 1;
        
        boardPanel.repaint();
        updateShipInfo();
        updateShipList();
    }
    
    private void confirmSetup() {
        parent.setupComplete(currentPlayerIndex);
    }
    
    public void resetState() {
        currentGrid = null;
        currentShipTypeIndex = 0;
        currentShipNumber = 1;
        currentOrientation = Orientation.HORIZONTAL;
        titleLabel.setText("Schiffe platzieren");
        instructionLabel.setText("Klicke auf das Spielfeld um dein Schiff zu platzieren");
        shipInfoLabel.setText("Warte...");
        boardPanel.setGrid(null);
        boardPanel.setPlacementMode(false, 0, currentOrientation);
        rotateButton.setEnabled(false);
        confirmButton.setEnabled(false);
        shipListPanel.removeAll();
        shipListPanel.revalidate();
        shipListPanel.repaint();
    }
}
