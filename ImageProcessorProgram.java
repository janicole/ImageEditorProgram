import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Handles all image-processing operations directly on BufferedImage.
 *
 * CODE FOR IMAGE FILTERS GO HERE
 * ------------------------------
 * 1) redBlueSwapFilter
 * 2) blackAndWhiteFilter
 * 3) rotateClockwiseFilter
 * 4) createWaves
 * 5) sepiaFilter
 * 6) cropImage
 * 7) adjustBrightness (Slider)
 *
 * @author Jana Johnson and Delaney Tobin
 * @date Apr 24 2025
 *
 */

public class ImageProcessorProgram {

    /**
     * Applies a filter that swaps red and blue values for each pixel of the image.
     *
     * @param img the BufferedImage to modify.
     */
    public static void redBlueSwapFilter(BufferedImage img) {
        for (int row = 0; row < img.getHeight(); row++) {
            for (int col = 0; col < img.getWidth(); col++) {
                int rgb = img.getRGB(col, row);

                // Extract RGB components
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                // Swap red and blue
                int swappedRgb = (blue << 16) | (green << 8) | red;
                img.setRGB(col, row, swappedRgb);
            }
        }
    }



    /**
     * Converts the image to black and white by averaging RGB values.
     *
     * @param img the BufferedImage to modify.
     */
    public static void blackAndWhiteFilter(BufferedImage img) {
        for (int row = 0; row < img.getHeight(); row++) {
            for (int col = 0; col < img.getWidth(); col++) {
                int rgb = img.getRGB(col, row);

                // Extract RGB components and calculate average
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;
                int average = (red + green + blue) / 3;

                // Set to grayscale (R=G=B=average)
                int grayRgb = (average << 16) | (average << 8) | average;
                img.setRGB(col, row, grayRgb);
            }
        }
    }


    /**
     * Rotates the image 90 degrees clockwise.
     *
     * @param img the BufferedImage to rotate, returns the rotated image.
     * @return a new BufferedImage representing the rotated image.
     */
    public static BufferedImage rotateClockwiseFilter(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage rotated = new BufferedImage(height, width, img.getType());

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                rotated.setRGB(height - row - 1, col, img.getRGB(col, row));
            }
        }

        return rotated;
    }

    /**
     * Applies a custom filter for a wavy effect.
     *
     * @param img the BufferedImage to modify.
     */
    public static void createWaves(BufferedImage img) {
        for (int row = 0; row < img.getHeight(); row++) {
            for (int col = 0; col < img.getWidth(); col++) {
                int rgb = img.getRGB(col, row);

                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                // Custom wavy adjustments
                red = Math.min(255, (int) (red + 50 * Math.sin(row % 10)));
                green = Math.min(255, (int) (green + 50 * Math.cos(col % 10)));
                blue = Math.max(0, Math.min(255, blue - (row + col) % 50));
                int finalRgb = (red << 16) | (green << 8) | blue;

                img.setRGB(col, row, finalRgb);
            }
        }
    }

    /**
     * Applies a sepia tone filter to the image.
     * This creates a warm, vintage look by adjusting RGB values using specific coefficients.
     *
     * @param img the BuggeredImage to modify.
     */
    public static void sepiaFilter (BufferedImage img) {
        for (int row = 0; row < img.getHeight(); row++) {
            for (int col = 0; col < img.getWidth(); col++) {
                int rgb = img.getRGB(col, row);

                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                // Calculate sepia values using standard sepia coefficients
                int sepiaRed = clamp((int)(0.393 * red + 0.769 * green + 0.189 * blue), 0, 255);
                int sepiaGreen = clamp((int)(0.349 * red + 0.686 * green + 0.168 * blue), 0, 255);
                int sepiaBlue = clamp((int)(0.272 * red + 0.534 * green + 0.131 * blue), 0, 255);


                int sepiaRgb = (sepiaRed << 16) | (sepiaGreen << 8) | sepiaBlue;
                img.setRGB(col, row, sepiaRgb);
            }
        }
    }


    /**
     * Adjusts the brightness of an image.
     *
     * @param img        The image to adjust
     * @param brightness The brightness adjustment value (-100 to 100)
     */
    public static void adjustBrightness(BufferedImage img, int brightness) {
        //Convert brightness from -100 to 100 range to -255 to 255 range
        int brightnessAdjust = (brightness * 255) / 100;

        for (int row = 0; row < img.getHeight(); row++) {
            for (int col = 0; col <img.getWidth(); col++) {
                int rgb = img.getRGB(col,row);

                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                // Adjust and clamp each color channel
                red = clamp(red + brightnessAdjust, 0, 255);
                green = clamp(green + brightnessAdjust, 0, 255);
                blue = clamp(blue + brightnessAdjust, 0, 255);

                int newRgb = (red << 16) | (green << 8) | blue;
                img.setRGB(col, row, newRgb);
            }

        }
    }


    public static BufferedImage cropImage(BufferedImage img, int x1, int y1, int x2, int y2) {
        // Validate the rectangle dimensions and ensure they're within bounds
        x1 = Math.max(0, Math.min(x1, img.getWidth()));
        y1 = Math.max(0, Math.min(y1, img.getHeight()));
        x2 = Math.max(0, Math.min(x2, img.getWidth()));
        y2 = Math.max(0, Math.min(y2, img.getHeight()));

        int width = x2 - x1;
        int height = y2 - y1;

        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Invalid crop dimensions.");
        }

        // Create the cropped image
        BufferedImage croppedImage = img.getSubimage(x1, y1, width, height);

        // Create a new BufferedImage to avoid sharing the same raster
        BufferedImage copy = new BufferedImage(croppedImage.getWidth(), croppedImage.getHeight(), img.getType());
        Graphics g = copy.getGraphics();
        g.drawImage(croppedImage, 0, 0, null);
        g.dispose();

        return copy;
    }


    /**
     * Clamps a value to a given range.
     *
     * @param val the value to clamp.
     * @param min the minimum value.
     * @param max the maximum value.
     * @return the clamped value.
     */
    private static int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }
}
