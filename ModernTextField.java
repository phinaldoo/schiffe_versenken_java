import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * A modern styled text field with placeholder support and focus effects.
 */
public class ModernTextField extends JTextField {
    
    private String placeholder;
    private Color borderColor = BattleshipGUI.OCEAN_LIGHT;
    private Color focusBorderColor = BattleshipGUI.ACCENT_COLOR;
    private Color currentBorderColor;
    private boolean isFocused = false;
    private int cornerRadius = 10;
    
    public ModernTextField() {
        this("");
    }
    
    public ModernTextField(String placeholder) {
        this.placeholder = placeholder;
        this.currentBorderColor = borderColor;
        
        setFont(BattleshipGUI.LABEL_FONT);
        setForeground(BattleshipGUI.TEXT_PRIMARY);
        setCaretColor(BattleshipGUI.ACCENT_COLOR);
        setSelectionColor(BattleshipGUI.ACCENT_COLOR);
        setSelectedTextColor(BattleshipGUI.TEXT_PRIMARY);
        
        setOpaque(false);
        setBorder(new EmptyBorder(12, 15, 12, 15));
        setPreferredSize(new Dimension(250, 45));
        
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                currentBorderColor = focusBorderColor;
                repaint();
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                currentBorderColor = borderColor;
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        // Background
        g2d.setColor(BattleshipGUI.CARD_BG);
        g2d.fill(new RoundRectangle2D.Float(0, 0, width, height, cornerRadius, cornerRadius));
        
        // Border
        g2d.setColor(currentBorderColor);
        g2d.setStroke(new BasicStroke(isFocused ? 2f : 1.5f));
        g2d.draw(new RoundRectangle2D.Float(1, 1, width - 2, height - 2, cornerRadius, cornerRadius));
        
        g2d.dispose();
        
        // Draw placeholder if empty and not focused
        if (getText().isEmpty() && !isFocusOwner() && placeholder != null && !placeholder.isEmpty()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(BattleshipGUI.TEXT_SECONDARY);
            g2.setFont(getFont().deriveFont(Font.ITALIC));
            FontMetrics fm = g2.getFontMetrics();
            int textY = (height - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(placeholder, getInsets().left, textY);
            g2.dispose();
        }
    }
    
    @Override
    protected void paintBorder(Graphics g) {
        // Don't paint default border
    }
    
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }
    
    public String getPlaceholder() {
        return placeholder;
    }
}
