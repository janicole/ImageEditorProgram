
import javax.swing.*;
import java.awt.*;


/**
 * Main entry point for the Image Processing Application.
 * Initializes the GUI and sets up the application window.
 *
 * @author Jana Johnson and Delaney Tobin
 * @date Apr 24 2025
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ImageProcessorFrame frame = new ImageProcessorFrame();

            // Add title and creator information
            JLabel titleLabel = new JLabel("Image Processing");
            titleLabel.setFont(new Font("Helvetica Nue", Font.PLAIN, 18));
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

            JLabel creatorLabel = new JLabel("Created by Jana Johnson and Delaney Tobin");
            creatorLabel.setFont(new Font("Helvetica Nue", Font.PLAIN, 12));
            creatorLabel.setHorizontalAlignment(SwingConstants.CENTER);

            JPanel titlePanel = new JPanel();
            titlePanel.setBackground(Color.WHITE);
            titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
            titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
            titlePanel.add(titleLabel);
            titlePanel.add(creatorLabel);

            frame.add(titlePanel, BorderLayout.NORTH);
            frame.setVisible(true);
        });
    }
}

