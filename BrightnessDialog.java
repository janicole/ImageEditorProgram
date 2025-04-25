import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;


/**
 * Dialog for adjusting image brightness.
 * Provides real-time preview of brightness adjustments using a slider.
 *
 * @author Jana Johnson and Delaney Tobin
 * @date Apr 24 2025
 */
public class BrightnessDialog extends JDialog {
    private final JSlider brightnessSlider;
    private final BufferedImage originalImage;
    private final BufferedImage workingImage;
    private final ImagePanel imagePanel;


    /**
     * Creates a new brightness adjustment dialog.
     *
     * @param parent The parent frame
     * @param image  The image to adjust
     * @param panel  The panel displaying the image
     */
    public BrightnessDialog(JFrame parent, BufferedImage image, ImagePanel panel) {
        super(parent, "Adjust Brightness", true);
        this.originalImage = image;
        this.workingImage = deepCopyImage(image);
        this.imagePanel = panel;

        setLayout(new BorderLayout());

        // Create Slider from -100 to 100
        brightnessSlider = new JSlider(JSlider.HORIZONTAL, -40, 40, 0);
        brightnessSlider.setMajorTickSpacing(10);
        brightnessSlider.setMinorTickSpacing(50);
        brightnessSlider.setPaintTicks(true);
        brightnessSlider.setPaintLabels(true);

        // Change Listener for real-time preview
        brightnessSlider.addChangeListener(e -> {
            // Create new copy of original image for each adjustment
            BufferedImage previewImage = deepCopyImage(originalImage);
            ImageProcessorProgram.adjustBrightness(previewImage, brightnessSlider.getValue());
            imagePanel.setImage(previewImage);
        });

        // Create Button panel
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(e -> {
            ImageProcessorProgram.adjustBrightness(workingImage, brightnessSlider.getValue());
            imagePanel.setImage(workingImage);
            dispose();
        });

        cancelButton.addActionListener(e -> {
            imagePanel.setImage(originalImage);
            dispose();
        });

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        // Components to Dialog
        add(new JLabel("Brightness:", SwingConstants.CENTER), BorderLayout.NORTH);
        add(brightnessSlider, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);
    }


    /**
     * Creates a deep copy of the provided BufferedImage.
     *
     * @param image The source image to copy
     * @return A new BufferedImage containing a copy of the source
     */
    private BufferedImage deepCopyImage(BufferedImage image) {
        BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D g2d = copy.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        return copy;
    }
}
