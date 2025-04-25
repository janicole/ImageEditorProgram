import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Stack;

/**
 * Manages the undo/redo history for image operations.
 * Maintains separate stacks for undo and redo operations.
 *
 * @author Jana Johnson and Delaney Tobin
 * @date Apr 24 2025
 */

public class ImageHistoryManager {

    /** Stack for storing previous image states */
    public final Stack<BufferedImage> undoStack = new Stack<>();

    /** Stack for storing redoable image states */
    public final Stack<BufferedImage> redoStack = new Stack<>();



    /**
     * 1) STORE IMAGE STATES
     *
     * @param image The current image state to save.
     */
    public void saveState(BufferedImage image) {
        undoStack.push(deepCopyBufferedImage(image));
        redoStack.clear(); // Invalidate redo stack on new save
    }


    /**
     * 2) CLEAR MEMORY STACK WHEN NEW STATE ADDED
     */
    public void clearHistory() {
        undoStack.clear();
        redoStack.clear();
    }

    /**
     * 3) SAVE EACH MEMORY STATE AS A COPY OF THE ORIGINAL
     *
     * @param image The image to copy.
     * @return A deep copy of the image.
     */
    public BufferedImage deepCopyBufferedImage(BufferedImage image) {
        BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics g = copy.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return copy;
    }

    /**
     * Reverts to the last saved state, if available.
     *
     * @param currentState The current image state to save in the redo stack.
     * @return The previous state from the undo stack.
     * @throws IllegalStateException if no previous state is available.
     */
    public BufferedImage undo(BufferedImage currentState) {
        if (undoStack.isEmpty()) {
            throw new IllegalStateException("No undo actions available.");
        }

        redoStack.push(deepCopyBufferedImage(currentState)); // Push current state to redo
        return undoStack.pop(); // Return the last state
    }

    /**
     * Reapplies a previously undone state, if available.
     *
     * @param currentState The current image state to save in the undo stack.
     * @return The next state from the redo stack.
     * @throws IllegalStateException if no redo actions are available.
     */
    public BufferedImage redo(BufferedImage currentState) {
        if (redoStack.isEmpty()) {
            throw new IllegalStateException("No redo actions available.");
        }

        undoStack.push(deepCopyBufferedImage(currentState)); // Push current state to undo
        return redoStack.pop(); // Return the next state
    }

}
