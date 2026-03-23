import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * A modern, beautiful button with hover effects and smooth animations.
 */
public class ModernButton extends JButton {
    
    private Color baseColor;
    private Color hoverColor;
    private Color pressedColor;
    private Color currentColor;
    private float animationProgress = 0f;
    private Timer animationTimer;
    private boolean isHovered = false;
    private boolean isPressed = false;
    private int cornerRadius = 12;
    
    public ModernButton(String text) {
        this(text, BattleshipGUI.ACCENT_COLOR);
    }
    
    public ModernButton(String text, Color color) {
        super(text);
        this.baseColor = color;
        this.hoverColor = brighter(color, 0.15f);
        this.pressedColor = darker(color, 0.15f);
        this.currentColor = baseColor;
        
        setFont(BattleshipGUI.BUTTON_FONT);
        setForeground(BattleshipGUI.TEXT_PRIMARY);
        setPreferredSize(new Dimension(180, 45));
        setMaximumSize(new Dimension(250, 50));
        setMinimumSize(new Dimension(120, 40));
        
        // Remove default button styling
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Setup animation timer
        animationTimer = new Timer(16, e -> {
            if (isHovered && animationProgress < 1f) {
                animationProgress = Math.min(1f, animationProgress + 0.15f);
                repaint();
            } else if (!isHovered && animationProgress > 0f) {
                animationProgress = Math.max(0f, animationProgress - 0.15f);
                repaint();
            } else {
                animationTimer.stop();
            }
        });
        
        // Mouse listeners for effects
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                if (!animationTimer.isRunning()) {
                    animationTimer.start();
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                isPressed = false;
                if (!animationTimer.isRunning()) {
                    animationTimer.start();
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        // Calculate current color based on animation
        currentColor = interpolateColor(baseColor, hoverColor, animationProgress);
        if (isPressed) {
            currentColor = pressedColor;
        }
        
        // Draw shadow
        if (isEnabled()) {
            g2d.setColor(new Color(0, 0, 0, 40));
            g2d.fill(new RoundRectangle2D.Float(2, 4, width - 4, height - 4, cornerRadius, cornerRadius));
        }
        
        // Draw button background with gradient
        GradientPaint gradient = new GradientPaint(
            0, 0, brighter(currentColor, 0.1f),
            0, height, darker(currentColor, 0.1f)
        );
        g2d.setPaint(gradient);
        g2d.fill(new RoundRectangle2D.Float(0, 0, width - 2, height - 4, cornerRadius, cornerRadius));
        
        // Draw subtle highlight at top
        g2d.setColor(new Color(255, 255, 255, 30));
        g2d.fill(new RoundRectangle2D.Float(1, 1, width - 4, height / 3, cornerRadius - 2, cornerRadius - 2));
        
        // Draw border
        g2d.setColor(brighter(currentColor, 0.2f));
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.draw(new RoundRectangle2D.Float(0.5f, 0.5f, width - 3, height - 5, cornerRadius, cornerRadius));
        g2d.dispose();

        // Let Swing render text/icons to avoid glyph clipping and font fallback issues.
        super.paintComponent(g);
    }
    
    private Color interpolateColor(Color c1, Color c2, float fraction) {
        int r = (int) (c1.getRed() + fraction * (c2.getRed() - c1.getRed()));
        int g = (int) (c1.getGreen() + fraction * (c2.getGreen() - c1.getGreen()));
        int b = (int) (c1.getBlue() + fraction * (c2.getBlue() - c1.getBlue()));
        return new Color(r, g, b);
    }
    
    private Color brighter(Color c, float factor) {
        int r = Math.min(255, (int) (c.getRed() + 255 * factor));
        int g = Math.min(255, (int) (c.getGreen() + 255 * factor));
        int b = Math.min(255, (int) (c.getBlue() + 255 * factor));
        return new Color(r, g, b);
    }
    
    private Color darker(Color c, float factor) {
        int r = Math.max(0, (int) (c.getRed() * (1 - factor)));
        int g = Math.max(0, (int) (c.getGreen() * (1 - factor)));
        int b = Math.max(0, (int) (c.getBlue() * (1 - factor)));
        return new Color(r, g, b);
    }
    
    public void setButtonColor(Color color) {
        this.baseColor = color;
        this.hoverColor = brighter(color, 0.15f);
        this.pressedColor = darker(color, 0.15f);
        repaint();
    }
}
