import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Victory screen with celebration animations.
 */
public class ResultPanel extends JPanel {
    
    private BattleshipGUI parent;
    private JLabel winnerLabel;
    private JLabel trophyLabel;
    private List<Confetti> confettiList;
    private Timer animationTimer;
    
    public ResultPanel(BattleshipGUI parent) {
        this.parent = parent;
        this.confettiList = new ArrayList<>();
        
        setLayout(new BorderLayout());
        setOpaque(false);
        
        // Create content
        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);
        
        // Animation timer for confetti
        animationTimer = new Timer(30, e -> {
            for (Confetti c : confettiList) {
                c.update();
            }
            repaint();
        });
    }
    
    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(100, 50, 100, 50));
        
        // Trophy
        trophyLabel = new JLabel("GEWONNEN");
        trophyLabel.setFont(BattleshipGUI.TITLE_FONT.deriveFont(64f));
        trophyLabel.setForeground(BattleshipGUI.WARNING_COLOR);
        trophyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Winner text
        JLabel congratsLabel = new JLabel("GRATULATION!");
        congratsLabel.setFont(BattleshipGUI.TITLE_FONT.deriveFont(48f));
        congratsLabel.setForeground(BattleshipGUI.SUCCESS_COLOR);
        congratsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        winnerLabel = new JLabel("Spieler");
        winnerLabel.setFont(BattleshipGUI.TITLE_FONT);
        winnerLabel.setForeground(BattleshipGUI.TEXT_PRIMARY);
        winnerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subLabel = new JLabel("hat die Seeschlacht gewonnen!");
        subLabel.setFont(BattleshipGUI.HEADER_FONT);
        subLabel.setForeground(BattleshipGUI.TEXT_SECONDARY);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        
        ModernButton newGameButton = new ModernButton("Neues Spiel", BattleshipGUI.SUCCESS_COLOR);
        newGameButton.addActionListener(e -> parent.newGame());
        
        ModernButton exitButton = new ModernButton("Beenden", BattleshipGUI.MISS_COLOR);
        exitButton.addActionListener(e -> System.exit(0));
        
        buttonPanel.add(newGameButton);
        buttonPanel.add(exitButton);
        
        panel.add(Box.createVerticalGlue());
        panel.add(trophyLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(congratsLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(winnerLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(subLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 60)));
        panel.add(buttonPanel);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    public void showResult(String winner) {
        winnerLabel.setText(winner);
        
        // Generate confetti
        confettiList.clear();
        Random rand = new Random();
        for (int i = 0; i < 100; i++) {
            confettiList.add(new Confetti(rand));
        }
        
        animationTimer.start();
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
            0, height, new Color(10, 25, 45)
        );
        g2d.setPaint(bgGradient);
        g2d.fillRect(0, 0, width, height);
        
        // Draw spotlight effect
        RadialGradientPaint spotlight = new RadialGradientPaint(
            width / 2f, height / 3f, height / 2f,
            new float[]{0f, 1f},
            new Color[]{new Color(255, 215, 0, 30), new Color(0, 0, 0, 0)}
        );
        g2d.setPaint(spotlight);
        g2d.fillRect(0, 0, width, height);
        
        // Draw confetti
        for (Confetti c : confettiList) {
            c.draw(g2d, width, height);
        }
        
        g2d.dispose();
    }
    
    // Inner class for confetti particles
    private class Confetti {
        float x, y, speedX, speedY;
        float rotation, rotationSpeed;
        int size;
        Color color;
        Random rand;
        
        private static final Color[] COLORS = {
            new Color(255, 107, 107),  // Red
            new Color(78, 205, 196),   // Cyan
            new Color(255, 230, 109),  // Yellow
            new Color(170, 111, 255),  // Purple
            new Color(107, 255, 148),  // Green
            new Color(255, 180, 107),  // Orange
        };
        
        Confetti(Random rand) {
            this.rand = rand;
            reset(true);
        }
        
        void reset(boolean randomY) {
            x = rand.nextFloat();
            y = randomY ? rand.nextFloat() * 0.5f - 0.5f : -0.1f;
            speedX = (rand.nextFloat() - 0.5f) * 0.005f;
            speedY = 0.002f + rand.nextFloat() * 0.003f;
            rotation = rand.nextFloat() * 360;
            rotationSpeed = (rand.nextFloat() - 0.5f) * 10;
            size = 8 + rand.nextInt(8);
            color = COLORS[rand.nextInt(COLORS.length)];
        }
        
        void update() {
            y += speedY;
            x += speedX;
            speedX += (rand.nextFloat() - 0.5f) * 0.001f;
            rotation += rotationSpeed;
            
            if (y > 1.1f) {
                reset(false);
            }
        }
        
        void draw(Graphics2D g2d, int width, int height) {
            int drawX = (int)(x * width);
            int drawY = (int)(y * height);
            
            Graphics2D g = (Graphics2D) g2d.create();
            g.translate(drawX, drawY);
            g.rotate(Math.toRadians(rotation));
            
            g.setColor(color);
            g.fillRect(-size/2, -size/4, size, size/2);
            
            g.dispose();
        }
    }
}
