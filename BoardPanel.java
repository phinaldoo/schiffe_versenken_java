import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * A beautifully rendered game board with cells for ships and shots.
 */
public class BoardPanel extends JPanel {
    
    private GRID grid;
    private int boardSize;
    private boolean isOpponentView;
    private boolean isInteractive;
    private CellClickListener clickListener;
    private int hoveredRow = -1;
    private int hoveredCol = -1;
    
    // Ship placement mode
    private boolean placementMode = false;
    private int placementLength = 0;
    private Orientation placementOrientation = Orientation.HORIZONTAL;
    
    // Cell rendering
    private static final int CELL_SIZE = 40;
    private static final int CELL_GAP = 2;
    private static final int LABEL_SIZE = 25;
    private static final int CORNER_RADIUS = 6;
    
    public interface CellClickListener {
        void onCellClicked(int row, int col);
    }
    
    public BoardPanel(int boardSize, boolean isOpponentView) {
        this.boardSize = boardSize;
        this.isOpponentView = isOpponentView;
        this.isInteractive = false;
        
        int totalSize = LABEL_SIZE + boardSize * (CELL_SIZE + CELL_GAP) + 20;
        setPreferredSize(new Dimension(totalSize, totalSize));
        setMinimumSize(new Dimension(totalSize, totalSize));
        setOpaque(false);
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isInteractive && clickListener != null) {
                    int[] cell = getCellAt(e.getX(), e.getY());
                    if (cell != null) {
                        clickListener.onCellClicked(cell[0], cell[1]);
                    }
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredRow = -1;
                hoveredCol = -1;
                repaint();
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int[] cell = getCellAt(e.getX(), e.getY());
                if (cell != null) {
                    if (hoveredRow != cell[0] || hoveredCol != cell[1]) {
                        hoveredRow = cell[0];
                        hoveredCol = cell[1];
                        repaint();
                    }
                } else {
                    if (hoveredRow != -1) {
                        hoveredRow = -1;
                        hoveredCol = -1;
                        repaint();
                    }
                }
            }
        });
    }
    
    private int[] getCellAt(int x, int y) {
        int startX = LABEL_SIZE + 10;
        int startY = LABEL_SIZE + 10;
        
        int col = (x - startX) / (CELL_SIZE + CELL_GAP);
        int row = (y - startY) / (CELL_SIZE + CELL_GAP);
        
        if (row >= 0 && row < boardSize && col >= 0 && col < boardSize) {
            int cellX = startX + col * (CELL_SIZE + CELL_GAP);
            int cellY = startY + row * (CELL_SIZE + CELL_GAP);
            if (x >= cellX && x < cellX + CELL_SIZE && y >= cellY && y < cellY + CELL_SIZE) {
                return new int[]{row, col};
            }
        }
        return null;
    }
    
    public void setGrid(GRID grid) {
        this.grid = grid;
        repaint();
    }
    
    public void setInteractive(boolean interactive) {
        this.isInteractive = interactive;
        setCursor(interactive ? new Cursor(Cursor.HAND_CURSOR) : new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    public void setClickListener(CellClickListener listener) {
        this.clickListener = listener;
    }
    
    public void setPlacementMode(boolean enabled, int length, Orientation orientation) {
        this.placementMode = enabled;
        this.placementLength = length;
        this.placementOrientation = orientation;
        repaint();
    }
    
    public void setPlacementOrientation(Orientation orientation) {
        this.placementOrientation = orientation;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        int startX = LABEL_SIZE + 10;
        int startY = LABEL_SIZE + 10;
        
        // Draw column labels (1-10)
        g2d.setFont(BattleshipGUI.SMALL_FONT.deriveFont(Font.BOLD));
        g2d.setColor(BattleshipGUI.TEXT_SECONDARY);
        for (int col = 0; col < boardSize; col++) {
            String label = String.valueOf(col + 1);
            FontMetrics fm = g2d.getFontMetrics();
            int labelX = startX + col * (CELL_SIZE + CELL_GAP) + (CELL_SIZE - fm.stringWidth(label)) / 2;
            g2d.drawString(label, labelX, startY - 8);
        }
        
        // Draw row labels (A-J)
        for (int row = 0; row < boardSize; row++) {
            String label = String.valueOf((char)('A' + row));
            FontMetrics fm = g2d.getFontMetrics();
            int labelY = startY + row * (CELL_SIZE + CELL_GAP) + (CELL_SIZE + fm.getAscent()) / 2 - 2;
            g2d.drawString(label, 5, labelY);
        }
        
        // Draw cells
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                drawCell(g2d, row, col, startX, startY);
            }
        }
        
        // Draw placement preview
        if (placementMode && hoveredRow >= 0 && hoveredCol >= 0) {
            drawPlacementPreview(g2d, startX, startY);
        }
        
        g2d.dispose();
    }
    
    private void drawCell(Graphics2D g2d, int row, int col, int startX, int startY) {
        int x = startX + col * (CELL_SIZE + CELL_GAP);
        int y = startY + row * (CELL_SIZE + CELL_GAP);
        
        boolean isHovered = (row == hoveredRow && col == hoveredCol && isInteractive);
        boolean hasShip = false;
        boolean wasShot = false;
        boolean isSunk = false;
        
        if (grid != null) {
            Coordinate coord = new Coordinate(row, col);
            hasShip = grid.hasShipAt(coord);
            wasShot = grid.hasShotAt(coord);
            
            // Check if ship at this cell is sunk
            if (hasShip && wasShot) {
                for (SHIP ship : grid.getShips()) {
                    if (ship.occupies(coord) && ship.isSunk()) {
                        isSunk = true;
                        break;
                    }
                }
            }
        }
        
        Color cellColor;
        Color borderColor;
        
        if (wasShot && hasShip) {
            // Hit
            cellColor = isSunk ? BattleshipGUI.SUNK_COLOR : BattleshipGUI.HIT_COLOR;
            borderColor = BattleshipGUI.HIT_GLOW;
        } else if (wasShot) {
            // Miss
            cellColor = BattleshipGUI.MISS_COLOR;
            borderColor = BattleshipGUI.MISS_COLOR.brighter();
        } else if (!isOpponentView && hasShip) {
            // Own ship (visible)
            cellColor = isHovered ? BattleshipGUI.SHIP_HOVER : BattleshipGUI.SHIP_COLOR;
            borderColor = BattleshipGUI.ACCENT_COLOR;
        } else {
            // Water
            cellColor = isHovered ? BattleshipGUI.WATER_HOVER : BattleshipGUI.WATER_COLOR;
            borderColor = BattleshipGUI.OCEAN_LIGHT;
        }
        
        // Draw cell shadow
        g2d.setColor(new Color(0, 0, 0, 30));
        g2d.fill(new RoundRectangle2D.Float(x + 2, y + 2, CELL_SIZE, CELL_SIZE, CORNER_RADIUS, CORNER_RADIUS));
        
        // Draw cell background with gradient
        GradientPaint gp = new GradientPaint(x, y, cellColor, x, y + CELL_SIZE, darker(cellColor, 0.15f));
        g2d.setPaint(gp);
        g2d.fill(new RoundRectangle2D.Float(x, y, CELL_SIZE, CELL_SIZE, CORNER_RADIUS, CORNER_RADIUS));
        
        // Draw cell border
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.draw(new RoundRectangle2D.Float(x + 0.5f, y + 0.5f, CELL_SIZE - 1, CELL_SIZE - 1, CORNER_RADIUS, CORNER_RADIUS));
        
        // Draw icons for hits/misses
        if (wasShot && hasShip) {
            // Draw X for hit
            drawHitMarker(g2d, x, y);
        } else if (wasShot) {
            // Draw dot for miss
            drawMissMarker(g2d, x, y);
        } else if (!isOpponentView && hasShip) {
            // Draw ship indicator
            drawShipIndicator(g2d, x, y);
        }
        
        // Hover glow effect
        if (isHovered && isInteractive && !wasShot) {
            g2d.setColor(new Color(255, 255, 255, 40));
            g2d.fill(new RoundRectangle2D.Float(x, y, CELL_SIZE, CELL_SIZE / 2, CORNER_RADIUS, CORNER_RADIUS));
        }
    }
    
    private void drawHitMarker(Graphics2D g2d, int x, int y) {
        int padding = 10;
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(x + padding, y + padding, x + CELL_SIZE - padding, y + CELL_SIZE - padding);
        g2d.drawLine(x + CELL_SIZE - padding, y + padding, x + padding, y + CELL_SIZE - padding);
        
        // Glow effect
        g2d.setColor(new Color(255, 100, 100, 100));
        g2d.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(x + padding, y + padding, x + CELL_SIZE - padding, y + CELL_SIZE - padding);
        g2d.drawLine(x + CELL_SIZE - padding, y + padding, x + padding, y + CELL_SIZE - padding);
    }
    
    private void drawMissMarker(Graphics2D g2d, int x, int y) {
        int centerX = x + CELL_SIZE / 2;
        int centerY = y + CELL_SIZE / 2;
        int radius = 6;
        
        g2d.setColor(new Color(255, 255, 255, 180));
        g2d.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
    }
    
    private void drawShipIndicator(Graphics2D g2d, int x, int y) {
        g2d.setColor(new Color(255, 255, 255, 70));
        int cx = x + CELL_SIZE / 2;
        int cy = y + CELL_SIZE / 2;
        int r = 7;
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawOval(cx - r, cy - r, 2 * r, 2 * r);
        g2d.drawLine(cx - r + 2, cy + r + 2, cx + r - 2, cy - r - 2);
    }
    
    private void drawPlacementPreview(Graphics2D g2d, int startX, int startY) {
        boolean canPlace = true;
        
        // Check if placement is valid
        if (grid != null) {
            try {
                Coordinate start = new Coordinate(hoveredRow, hoveredCol);
                canPlace = grid.canPlaceShip(start, placementOrientation, placementLength);
            } catch (Exception e) {
                canPlace = false;
            }
        }
        
        Color previewColor = canPlace ? 
            new Color(BattleshipGUI.SUCCESS_COLOR.getRed(), BattleshipGUI.SUCCESS_COLOR.getGreen(), 
                      BattleshipGUI.SUCCESS_COLOR.getBlue(), 150) :
            new Color(BattleshipGUI.HIT_COLOR.getRed(), BattleshipGUI.HIT_COLOR.getGreen(), 
                      BattleshipGUI.HIT_COLOR.getBlue(), 150);
        
        for (int i = 0; i < placementLength; i++) {
            int row = hoveredRow + (placementOrientation == Orientation.VERTICAL ? i : 0);
            int col = hoveredCol + (placementOrientation == Orientation.HORIZONTAL ? i : 0);
            
            if (row >= 0 && row < boardSize && col >= 0 && col < boardSize) {
                int x = startX + col * (CELL_SIZE + CELL_GAP);
                int y = startY + row * (CELL_SIZE + CELL_GAP);
                
                g2d.setColor(previewColor);
                g2d.fill(new RoundRectangle2D.Float(x, y, CELL_SIZE, CELL_SIZE, CORNER_RADIUS, CORNER_RADIUS));
                
                g2d.setColor(canPlace ? BattleshipGUI.SUCCESS_COLOR : BattleshipGUI.HIT_COLOR);
                g2d.setStroke(new BasicStroke(2));
                g2d.draw(new RoundRectangle2D.Float(x, y, CELL_SIZE, CELL_SIZE, CORNER_RADIUS, CORNER_RADIUS));
            }
        }
    }
    
    private Color darker(Color c, float factor) {
        return new Color(
            Math.max(0, (int)(c.getRed() * (1 - factor))),
            Math.max(0, (int)(c.getGreen() * (1 - factor))),
            Math.max(0, (int)(c.getBlue() * (1 - factor)))
        );
    }
}
