import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Beautiful welcome screen with animated background and player name input.
 */
public class WelcomePanel extends JPanel {
    
    private BattleshipGUI parent;
    private ModernTextField player1Field;
    private ModernTextField player2Field;
    private List<WaterBubble> bubbles;
    private Timer animationTimer;
    private float wavePhase = 0;
    
    public WelcomePanel(BattleshipGUI parent) {
        this.parent = parent;
        this.bubbles = new ArrayList<>();
        
        setLayout(new BorderLayout());
        setOpaque(false);
        
        // Initialize bubbles
        Random rand = new Random();
        for (int i = 0; i < 20; i++) {
            bubbles.add(new WaterBubble(rand));
        }
        
        // Start animation
        animationTimer = new Timer(30, e -> {
            wavePhase += 0.05f;
            for (WaterBubble bubble : bubbles) {
                bubble.update();
            }
            repaint();
        });
        animationTimer.start();
        
        // Create content panel
        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(80, 50, 80, 50));
        
        // Title with icon
        JLabel titleLabel = new JLabel("SCHIFFE VERSENKEN");
        titleLabel.setFont(BattleshipGUI.TITLE_FONT.deriveFont(48f));
        titleLabel.setForeground(BattleshipGUI.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Das klassische Seeschlacht-Spiel");
        subtitleLabel.setFont(BattleshipGUI.HEADER_FONT);
        subtitleLabel.setForeground(BattleshipGUI.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Player input card
        JPanel cardPanel = createInputCard();
        cardPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Version info
        JLabel versionLabel = new JLabel("v1.0");
        versionLabel.setFont(BattleshipGUI.SMALL_FONT);
        versionLabel.setForeground(new Color(189, 195, 199, 150));
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(Box.createVerticalGlue());
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(subtitleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 60)));
        panel.add(cardPanel);
        panel.add(Box.createVerticalGlue());
        panel.add(versionLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        return panel;
    }
    
    private JPanel createInputCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Shadow
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillRoundRect(5, 8, getWidth() - 10, getHeight() - 10, 25, 25);
                
                // Card background
                GradientPaint gp = new GradientPaint(
                    0, 0, BattleshipGUI.CARD_BG,
                    0, getHeight(), new Color(30, 50, 75)
                );
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 8, 25, 25);
                
                // Border
                g2d.setColor(new Color(BattleshipGUI.ACCENT_COLOR.getRed(), 
                    BattleshipGUI.ACCENT_COLOR.getGreen(), 
                    BattleshipGUI.ACCENT_COLOR.getBlue(), 100));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 7, getHeight() - 10, 24, 24);
                
                g2d.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        card.setMaximumSize(new Dimension(450, 400));
        card.setPreferredSize(new Dimension(450, 350));
        
        // Card title
        JLabel cardTitle = new JLabel("Spieler eingeben");
        cardTitle.setFont(BattleshipGUI.HEADER_FONT);
        cardTitle.setForeground(BattleshipGUI.TEXT_PRIMARY);
        cardTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Player 1 input
        JLabel p1Label = new JLabel("Spieler 1");
        p1Label.setFont(BattleshipGUI.LABEL_FONT);
        p1Label.setForeground(BattleshipGUI.TEXT_SECONDARY);
        p1Label.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        player1Field = new ModernTextField("Name eingeben...");
        player1Field.setMaximumSize(new Dimension(300, 45));
        player1Field.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Player 2 input
        JLabel p2Label = new JLabel("Spieler 2");
        p2Label.setFont(BattleshipGUI.LABEL_FONT);
        p2Label.setForeground(BattleshipGUI.TEXT_SECONDARY);
        p2Label.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        player2Field = new ModernTextField("Name eingeben...");
        player2Field.setMaximumSize(new Dimension(300, 45));
        player2Field.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Start button
        ModernButton startButton = new ModernButton("Spiel starten", BattleshipGUI.SUCCESS_COLOR);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.addActionListener(e -> startGame());
        
        // Allow Enter key to start game
        Action startAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        };
        player1Field.addActionListener(e -> player2Field.requestFocus());
        player2Field.addActionListener(e -> startGame());
        
        card.add(cardTitle);
        card.add(Box.createRigidArea(new Dimension(0, 30)));
        card.add(p1Label);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(player1Field);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(p2Label);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(player2Field);
        card.add(Box.createRigidArea(new Dimension(0, 35)));
        card.add(startButton);
        
        return card;
    }
    
    private void startGame() {
        String p1 = player1Field.getText().trim();
        String p2 = player2Field.getText().trim();
        
        if (p1.isEmpty()) p1 = "Spieler 1";
        if (p2.isEmpty()) p2 = "Spieler 2";
        
        parent.startGame(p1, p2);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        // Background gradient
        GradientPaint bgGradient = new GradientPaint(
            0, 0, BattleshipGUI.OCEAN_DARK,
            0, height, BattleshipGUI.OCEAN_MEDIUM
        );
        g2d.setPaint(bgGradient);
        g2d.fillRect(0, 0, width, height);
        
        // Draw animated waves at bottom
        drawWaves(g2d, width, height);
        
        // Draw bubbles
        for (WaterBubble bubble : bubbles) {
            bubble.draw(g2d, width, height);
        }
        
        g2d.dispose();
    }
    
    private void drawWaves(Graphics2D g2d, int width, int height) {
        int waveHeight = 100;
        int baseY = height - waveHeight;
        
        // Draw multiple wave layers
        for (int layer = 0; layer < 3; layer++) {
            float alpha = 0.15f - layer * 0.04f;
            g2d.setColor(new Color(BattleshipGUI.WATER_COLOR.getRed(),
                BattleshipGUI.WATER_COLOR.getGreen(),
                BattleshipGUI.WATER_COLOR.getBlue(),
                (int)(alpha * 255)));
            
            int[] xPoints = new int[width + 2];
            int[] yPoints = new int[width + 2];
            
            for (int x = 0; x <= width; x++) {
                xPoints[x] = x;
                float phase = wavePhase + layer * 0.5f;
                yPoints[x] = baseY + layer * 20 + (int)(Math.sin((x * 0.02f) + phase) * 15 + Math.sin((x * 0.01f) + phase * 0.5f) * 10);
            }
            xPoints[width + 1] = width;
            yPoints[width + 1] = height;
            xPoints[0] = 0;
            
            java.awt.Polygon wave = new java.awt.Polygon();
            for (int i = 0; i <= width; i++) {
                wave.addPoint(xPoints[i], yPoints[i]);
            }
            wave.addPoint(width, height);
            wave.addPoint(0, height);
            
            g2d.fill(wave);
        }
    }
    
    // Inner class for animated bubbles
    private class WaterBubble {
        float x, y, speed, size;
        float wobblePhase;
        Random rand;
        
        WaterBubble(Random rand) {
            this.rand = rand;
            reset(true);
        }
        
        void reset(boolean randomY) {
            x = rand.nextFloat();
            y = randomY ? rand.nextFloat() : 1.1f;
            speed = 0.001f + rand.nextFloat() * 0.002f;
            size = 3 + rand.nextFloat() * 8;
            wobblePhase = rand.nextFloat() * (float)Math.PI * 2;
        }
        
        void update() {
            y -= speed;
            wobblePhase += 0.05f;
            if (y < -0.1f) {
                reset(false);
            }
        }
        
        void draw(Graphics2D g2d, int width, int height) {
            float drawX = x * width + (float)Math.sin(wobblePhase) * 10;
            float drawY = y * height;
            
            g2d.setColor(new Color(255, 255, 255, 30));
            g2d.fill(new Ellipse2D.Float(drawX - size/2, drawY - size/2, size, size));
            
            // Highlight
            g2d.setColor(new Color(255, 255, 255, 60));
            g2d.fill(new Ellipse2D.Float(drawX - size/4, drawY - size/3, size/3, size/3));
        }
    }
}
