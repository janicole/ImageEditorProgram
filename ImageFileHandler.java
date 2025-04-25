import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Utility class for loading and saving images as BufferedImages.
 *
 * @author Jana Johnson and Delaney Tobin
 * @date Apr 24 2025
 */
public class ImageFileHandler {

    /**
     * Loads a BufferedImage from the specified file.
     *
     * @param filename the file path to load the image from.
     * @return a BufferedImage of the loaded image.
     * @throws IOException if the file cannot be read.
     */
    public static BufferedImage load(String filename) throws IOException {
        return ImageIO.read(new File(filename));
    }

    /**
     * Saves a BufferedImage to the specified file path.
     *
     * @param img      the BufferedImage to save.
     * @param filename the file path to save the image to.
     * @throws IOException if the image cannot be saved.
     */
    public static void save(BufferedImage img, String filename) throws IOException {
        // Ensure we have a valid image
        if (img == null) {
            throw new IllegalArgumentException("Cannot save null image");
        }

        // Create file and parent directories if they don't exist
        File file = new File(filename);
        file.getParentFile().mkdirs();

        // Get the file extension, default to PNG if none provided
        String formatName = "png";
        int lastDot = filename.lastIndexOf(".");
        if (lastDot > 0 && lastDot < filename.length() - 1) {
            formatName = filename.substring(lastDot + 1).toLowerCase();
        }

        // Validate format is supported
        if (!isFormatSupported(formatName)) {
            throw new IOException("Unsupported image format: " + formatName);
        }

        // Try to write the image
        try {
            boolean success = ImageIO.write(img, formatName, file);
            if (!success) {
                throw new IOException("Failed to write image in " + formatName + " format");
            }
        } catch (Exception e) {
            throw new IOException("Error saving image: " + e.getMessage(), e);
        }
    }

    /**
     * Checks if the given format is supported for writing images.
     *
     * @param formatName the format to check
     * @return true if the format is supported, false otherwise
     */
    private static boolean isFormatSupported(String formatName) {
        String[] formats = ImageIO.getWriterFormatNames();
        for (String format : formats) {
            if (format.equalsIgnoreCase(formatName)) {
                return true;
            }
        }
        return false;
    }
}

