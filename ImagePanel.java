import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;


/**
 * Panel to display the BufferedImage.
 *
 * @author Jana Johnson and Delaney Tobin
 *  *@date Apr 24 2025
 */
public class ImagePanel extends JPanel {

    private final List<SelectionChangeListener> listeners = new ArrayList<>();

    private static final Color SELECTION_FILL_COLOR = new Color(105, 105, 105, 125);
    private static final Color SELECTION_OUTLINE_COLOR = Color.RED;

    private static final int SHADOW_SIZE = 5;
    private static final int SHADOW_OPACITY = 100; // 0-255


    private BufferedImage img;
    private Rectangle selectedRectangle; // Previously 'selectionRect'
    private Point dragStartPoint;
    private double scaleX, scaleY;

    /**
     * Sets the image to display in the panel.
     *
     * @param img The BufferedImage to display.
     */
    public void setImage(BufferedImage img) {
        this.img = img;
        resetSelectionRectangle(); // Clear selection when setting a new image
        repaint();
    }

    public void addSelectionChangeListener(SelectionChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Returns the current selection rectangle (in image coordinates).
     *
     * @return The selection rectangle.
     */
    public Rectangle getSelectionRect() {
        return convertToImageCoordinates(selectedRectangle);
    }

    /**
     * Clears the selection rectangle.
     */
    public void resetSelectionRectangle() {
        selectedRectangle = null;
        repaint();
        notifySelectionChanged();
    }

    public ImagePanel() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(SHADOW_SIZE, SHADOW_SIZE, SHADOW_SIZE, SHADOW_SIZE));

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragStartPoint = e.getPoint();
                resetSelectionRectangle();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragStartPoint != null) {
                    selectedRectangle = createRectangle(dragStartPoint, e.getPoint());
                    repaint();
                    notifySelectionChanged();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                repaint();
                notifySelectionChanged();
            }
        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    /**
     * Handles scaling and drawing the image and selection rectangle.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (img != null) {
            Graphics2D g2d = (Graphics2D) g.create();

            // Calculate dimensions
            Point scaledDimensions = calculateScaledDimensions();
            int scaledWidth = scaledDimensions.x;
            int scaledHeight = scaledDimensions.y;
            int xOffset = (getWidth() - scaledWidth) / 2;
            int yOffset = (getHeight() - scaledHeight) / 2;

            // Draw shadow
            g2d.setColor(new Color(0, 0, 0, SHADOW_OPACITY));
            for (int i = 0; i < SHADOW_SIZE; i++) {
                g2d.fillRect(xOffset + i, yOffset + i,
                        scaledWidth + SHADOW_SIZE - i,
                        scaledHeight + SHADOW_SIZE - i);
            }

            // Draw image
            g2d.drawImage(img, xOffset, yOffset, scaledWidth, scaledHeight, null);

            // Draw selection if exists
            if (selectedRectangle != null) {
                g2d.setColor(SELECTION_FILL_COLOR);
                g2d.fillRect(selectedRectangle.x, selectedRectangle.y,
                        selectedRectangle.width, selectedRectangle.height);
                g2d.setColor(SELECTION_OUTLINE_COLOR);
                g2d.drawRect(selectedRectangle.x, selectedRectangle.y,
                        selectedRectangle.width, selectedRectangle.height);
            }

            g2d.dispose();
        }
    }



/**
     * Notify all registered listeners that the selection has changed.
     */
    private void notifySelectionChanged() {
        boolean hasSelection = (selectedRectangle != null && !selectedRectangle.isEmpty());
        for (SelectionChangeListener listener : listeners) {
            listener.onSelectionChanged(hasSelection);
        }
    }

    /**
     * Listener interface for selection changes.
     */
    public interface SelectionChangeListener {
        void onSelectionChanged(boolean hasSelection);
    }

    /* -------------------  Helper Methods ------------------- */

    /**
     * Converts the given rectangle to image coordinates.
     *
     * @param rect The rectangle in display coordinates.
     * @return The rectangle in image coordinates, or null if the input is null.
     */
    private Rectangle convertToImageCoordinates(Rectangle rect) {
        if (rect == null) {
            return null;
        }
        int x = (int) (rect.x / scaleX);
        int y = (int) (rect.y / scaleY);
        int width = (int) (rect.width / scaleX);
        int height = (int) (rect.height / scaleY);
        return new Rectangle(x, y, width, height);
    }

    /**
     * Calculates the scaled dimensions of the image to fit within the panel.
     *
     * @return A Point where x is the scaled width and y is the scaled height.
     */
    private Point calculateScaledDimensions() {
        scaleX = (double) getWidth() / img.getWidth();
        scaleY = (double) getHeight() / img.getHeight();

        double scale = Math.min(scaleX, scaleY); // Maintain aspect ratio
        int scaledWidth = (int) (img.getWidth() * scale);
        int scaledHeight = (int) (img.getHeight() * scale);

        return new Point(scaledWidth, scaledHeight);
    }

    /**
     * Creates a rectangle from two points (e.g., drag start and current drag).
     *
     * @param start The starting point.
     * @param end   The ending point.
     * @return The rectangle defined by the two points.
     */
    private Rectangle createRectangle(Point start, Point end) {
        int x = Math.min(start.x, end.x);
        int y = Math.min(start.y, end.y);
        int width = Math.abs(start.x - end.x);
        int height = Math.abs(start.y - end.y);
        return new Rectangle(x, y, width, height);
    }
}