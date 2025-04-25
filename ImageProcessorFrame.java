import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * The main GUI for the Image Processor program.
 * Provides a user interface for loading, adjusting, and saving images.
 *
 * @author Jana Johnson and Delaney Tobin
 * @date Apr 24 2025
 */
public class ImageProcessorFrame extends JFrame {
    private final ImagePanel imagePanel = new ImagePanel();
    private BufferedImage currentImage;
    private boolean isCropModeEnabled = false;
    private JButton cropButton;
    private JButton cropModeButton;
    private JButton cancelCropButton;
    private JButton undoButton;
    private JButton redoButton;

    private static final String ERROR_NO_IMAGE = "No image loaded.";
    private final ImageHistoryManager historyManager = new ImageHistoryManager();


    /**
     * The 5 methods below create our Program GUI
     * ------------------------------------------
     * 1) ImageProcessorFrame
     * 2) FILE MENU TOOLTIPS
     * 3) createBottomPanel
     * 4) createMenuItem
     * 5) createButton
     *
     */


    // ----------------------------------------------------
    // 1) Creates the main panel for all others to rest on.
    // ----------------------------------------------------
    public ImageProcessorFrame() {
        setTitle("Image Processor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Add Image Panel (main content)
        add(imagePanel, BorderLayout.CENTER);

        // Create and add bottom panel (essential controls)
        JPanel bottomPanel = bottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        // Create and Set Menu Bar
        setJMenuBar(createMenuBar());

        // Setup tooltips and shortcuts
        setupTooltipsAndShortcuts();

        setSize(1024, 768);
        setLocationRelativeTo(null);
    }




    // -----------------------------------------------------
    // 2) TOOL TIPS
    // -----------------------------------------------------
    private void setupTooltipsAndShortcuts() {
        // Menu tooltips
        addMenuItemTooltip("Load Image", "Open an image file (Ctrl+O)", KeyStroke.getKeyStroke("control O"));
        addMenuItemTooltip("Save Image", "Save current image (Ctrl+S)", KeyStroke.getKeyStroke("control S"));
        addMenuItemTooltip("Brightness...", "Adjust image brightness (Ctrl+B)", KeyStroke.getKeyStroke("control B"));
        addMenuItemTooltip("Crop Mode", "Enter crop mode (Ctrl+X)", KeyStroke.getKeyStroke("control X"));

        // Button tooltips
        undoButton.setToolTipText("Undo last action (Ctrl+Z)");
        redoButton.setToolTipText("Redo last undone action (Ctrl+Y)");
        cropModeButton.setToolTipText("Enter crop mode to select area");
        cropButton.setToolTipText("Apply crop to selected area");
        cancelCropButton.setToolTipText("Cancel crop operation");

    }


    private void addMenuItemTooltip(String itemText, String tooltip, KeyStroke shortcut) {
        for (int i = 0; i < getJMenuBar().getMenuCount(); i++) {
            JMenu menu = getJMenuBar().getMenu(i);
            if (menu != null) {
                for (int j = 0; j < menu.getItemCount(); j++) {
                    JMenuItem item = menu.getItem(j);
                    if (item != null && item.getText().equals(itemText)) {
                        item.setToolTipText(tooltip);
                        item.setAccelerator(shortcut);
                        break;
                    }
                }
            }
        }
    }





    // -----------------------------------------------------
    // 3) Bottom panel for Quick Crop Access Buttons
    // -----------------------------------------------------
    private JPanel bottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Create essential buttons
        undoButton = createButton("Undo", e -> undo());
        redoButton = createButton("Redo", e -> redo());
        cropModeButton = createButton("Crop Mode", e -> enterCropMode());
        cropButton = createButton("Apply Crop", e -> applyCrop());
        cancelCropButton = createButton("Cancel Crop", e -> cancelCrop());

        // Initially hide crop-related buttons
        cropButton.setVisible(false);
        cancelCropButton.setVisible(false);

        // Add buttons to panel
        bottomPanel.add(undoButton);
        bottomPanel.add(redoButton);
        bottomPanel.add(cropModeButton);
        bottomPanel.add(cropButton);
        bottomPanel.add(cancelCropButton);

        return bottomPanel;
    }



    // -----------------------------
    // 4) Creating The Menu Panel
    // -----------------------------
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(createMenuItem("Load Image", this::loadImage));
        fileMenu.add(createMenuItem("Save Image", this::saveImage));
        menuBar.add(fileMenu);

        // Adjust Menu
        JMenu adjustMenu = new JMenu("Adjust");
        adjustMenu.add(createMenuItem("Brightness...", this::showBrightnessDialog));
        adjustMenu.add(createMenuItem("Crop", this::showCropDialog));
        menuBar.add(adjustMenu);

        // Filters Menu
        JMenu filterMenu = new JMenu("Filters");
        filterMenu.add(createMenuItem("Red/Blue Swap", () -> applyFilter("redBlueSwap")));
        filterMenu.add(createMenuItem("Black and White", () -> applyFilter("blackAndWhite")));
        filterMenu.add(createMenuItem("Sepia", () -> applyFilter("sepiaFilter")));
        filterMenu.add(createMenuItem("Waves", () -> applyFilter("createWaves")));
        menuBar.add(filterMenu);

        return menuBar;
    }




    // 4) Creating Items within our Menu
    private JMenuItem createMenuItem(String text, Runnable action) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.addActionListener(e -> action.run());
        return menuItem;
    }


    // 5) Creating Buttons
    private JButton createButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.addActionListener(actionListener);
        return button;
    }



    /**
     * --------------------------------------------------------------------------
     * The Methods below Go in order of how the user would interact with our GUI.
     * 1) Loading an Image
     * 2) Editing an Image (Apply Filters)
     * 3) Editing an Image (Brightness Slider)
     * 4) Save Image
     * --------------------------------------------------------------------------
      */

    // 1) Load Image
    private void loadImage() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                currentImage = ImageFileHandler.load(fileChooser.getSelectedFile().getPath());
                historyManager.clearHistory();
                historyManager.saveState(currentImage);
                imagePanel.setImage(currentImage);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading image: " + e.getMessage());
            }
        }
    }



    // 2) Edit Image (Brightness Slider)
    private void showBrightnessDialog() {
        if (currentImage == null) {
            JOptionPane.showMessageDialog(this, ERROR_NO_IMAGE);
            return;
        }
        historyManager.saveState(currentImage);
        BrightnessDialog dialog = new BrightnessDialog(this, currentImage, imagePanel);
        dialog.setVisible(true);
    }


    private void applyFilter(String filterName) {
        if (currentImage == null) {
            JOptionPane.showMessageDialog(this, ERROR_NO_IMAGE);
            return;
        }
        historyManager.saveState(currentImage);
        switch (filterName) {
            case "redBlueSwap" -> ImageProcessorProgram.redBlueSwapFilter(currentImage);
            case "blackAndWhite" -> ImageProcessorProgram.blackAndWhiteFilter(currentImage);
            case "rotateClockwiseFilter" -> currentImage = ImageProcessorProgram.rotateClockwiseFilter(currentImage);
            case "sepiaFilter" -> ImageProcessorProgram.sepiaFilter(currentImage);
            case "createWaves" -> ImageProcessorProgram.createWaves(currentImage);
            default -> JOptionPane.showMessageDialog(this, "Invalid filter name: " + filterName);
        }
        imagePanel.setImage(currentImage);
        updateHistoryButtons();
    }




    // 3) Save Image
    private void saveImage() {
        if (currentImage == null) {
            JOptionPane.showMessageDialog(this, ERROR_NO_IMAGE, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String filePath = fileChooser.getSelectedFile().getPath();
                if (!filePath.toLowerCase().endsWith(".png") && !filePath.toLowerCase().endsWith(".jpg")) {
                    filePath += ".png"; // Default to PNG if no extension provided
                }
                ImageFileHandler.save(currentImage, filePath);
                JOptionPane.showMessageDialog(this, "Image saved successfully!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // --------------------------------
    //
    // CROP MODE
    //
    // --------------------------------

    private void enterCropMode(){
        if(currentImage == null) {
            JOptionPane.showMessageDialog(this, ERROR_NO_IMAGE);
            return;
        }
        isCropModeEnabled = true;
        cropModeButton.setVisible(false);
        cropButton.setVisible(true);
        cancelCropButton.setVisible(true);
        imagePanel.addSelectionChangeListener(hasSelection -> cropButton.setEnabled(hasSelection));
        cropButton.setEnabled(false); // Initially disabled until selected
    }

    private void applyCrop() {
        Rectangle selection = imagePanel.getSelectionRect();
        if (selection != null && !selection.isEmpty()) {
            historyManager.saveState(currentImage);
            try {
                BufferedImage croppedImage = ImageProcessorProgram.cropImage(
                        currentImage,
                        selection.x,
                        selection.y,
                        selection.x + selection.width,
                        selection.y + selection.height
                );
                currentImage = croppedImage;
                imagePanel.setImage(currentImage);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this,
                        "Invalid Crop Area: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        exitCropMode();
    }

    private void cancelCrop () {
        isCropModeEnabled = false;
        cropModeButton.setVisible(true);
        cropButton.setVisible(false);
        cancelCropButton.setVisible(false);
        imagePanel.resetSelectionRectangle();
    }

    private void exitCropMode() {
        isCropModeEnabled = false;
        cropModeButton.setVisible(true);
        cropButton.setVisible(false);
        cancelCropButton.setVisible(false);
        imagePanel.resetSelectionRectangle();
    }

    private void showCropDialog() {
        enterCropMode();
    }



    // --------------------------------
    //
    // UNDO AND REDO MODE
    //
    // --------------------------------

    private void undo() {
        if (currentImage == null) {
            return;
        }
        try {
            currentImage = historyManager.undo(currentImage);
            imagePanel.setImage(currentImage);
            updateHistoryButtons();
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, "No more undo actions available");
        }
    }
    private void redo() {
        if (currentImage == null) {
            return;
        }
        try {
            currentImage = historyManager.redo(currentImage);
            imagePanel.setImage(currentImage);
            updateHistoryButtons();
        } catch(IllegalStateException e) {
            JOptionPane.showMessageDialog(this, "No more redo actions available");
        }
    }

    private void updateHistoryButtons() {
        // Is called after any operation that changes image.
        undoButton.setEnabled(!historyManager.undoStack.isEmpty());
        redoButton.setEnabled(!historyManager.redoStack.isEmpty());
    }

}
