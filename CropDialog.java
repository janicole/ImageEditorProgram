import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;


/**
 * Dialog for handling image cropping operations.
 * Provides a user interface for selecting and applying crop areas to images.
 * @author Jana Johnson and Delaney Tobin
 * @date Apr 24 2025
 */

public class CropDialog extends JDialog implements ImagePanel.SelectionChangeListener {
    private final BufferedImage originalImage;
    private final BufferedImage workingImage;
    private final ImagePanel imagePanel;
    private JButton applyButton = new JButton("Apply Crop");


    /**
     * Creates a new crop dialog.
     *
     * @param parent The parent frame
     * @param image  The image to be cropped
     * @param panel  The panel displaying the image
     */

    public CropDialog(JFrame parent, BufferedImage image, ImagePanel panel) {
        super(parent, "Crop Image", true);
        this.originalImage = image;
        this.workingImage = deepCopyImage(image);
        this.imagePanel = panel;

        setLayout(new BorderLayout());

        // Instruction Label
        JLabel instructionLabel = new JLabel("Drag to select crop area", SwingConstants.CENTER);
        instructionLabel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        //Create button panel
        JPanel buttonPanel = new JPanel();
        JButton cancelButton = new JButton("Cancel");

        applyButton.setEnabled(false);
        applyButton.addActionListener(e -> applyCrop());
        cancelButton.addActionListener(e -> cancel());

        buttonPanel.add(applyButton);
        buttonPanel.add(cancelButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add components
        add(instructionLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        // Register as selection listener
        imagePanel.addSelectionChangeListener(this);

        // Set dialog properties
        setSize(300, 150);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    /**
     * Applies the crop operation to the selected area.
     * Creates a new image from the selected region.
     */
    private void applyCrop() {
        try {
            Rectangle selection = imagePanel.getSelectionRect();
            if (selection != null && !selection.isEmpty()) {
                BufferedImage croppedImage = new BufferedImage(
                        selection.width,
                        selection.height,
                        workingImage.getType()
                );
                Graphics2D g2d = croppedImage.createGraphics();
                g2d.drawImage(
                        workingImage,
                        0, 0,
                        selection.width, selection.height,
                        selection.x, selection.y,
                        selection.x + selection.width,
                        selection.y + selection.height,
                        null
                );
                g2d.dispose();
                imagePanel.setImage(croppedImage);
            }
            dispose();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid crop area: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Cancels the crop operation and restores the original image.
     */
    private void cancel() {
        imagePanel.setImage(originalImage);
        imagePanel.resetSelectionRectangle();
        dispose();
    }

    @Override
    public void onSelectionChanged(boolean hasSelection) {
        applyButton.setEnabled(hasSelection);
    }

    @Override
    public void dispose() {
        imagePanel.resetSelectionRectangle();
        super.dispose();
    }


    /**
     * Creates a deep copy of the provided BufferedImage.
     *
     * @param source The source image to copy
     * @return A new BufferedImage containing a copy of the source
     */
    private BufferedImage deepCopyImage(BufferedImage source) {
        BufferedImage copy = new BufferedImage(
                source.getWidth(),
                source.getHeight(),
                source.getType()
        );
        Graphics g = copy.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return copy;
    }
}



