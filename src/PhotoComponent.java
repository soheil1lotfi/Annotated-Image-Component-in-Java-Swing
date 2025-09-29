import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PhotoComponent extends JComponent {

    private PhotoComponentModel model;
    private PhotoComponentView view;
    private Color backgroundColor = Color.PINK;
    private Color strokeColor = Color.GRAY;
    private int componentWidth;
    private int componentHeight;


    private boolean isDrawing = false;
    private Point lastMousePosition = null;


    public PhotoComponent(String file) {
        this.model = new PhotoComponentModel(file);
        this.view = new PhotoComponentView();
        setPreferredSize(new Dimension(400, 300));
        setMaximumSize(new Dimension(400, 300));
        setSize(new Dimension(400, 300));

        setFocusable(true);
        
        setupMouseListeners();
        setupMouseMotionListeners();
        setupKeyboardListeners();
    }

    private void setupMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Double-click flips the photo
                if (e.getClickCount() == 2 && model.hasPhoto()) {
                    model.flip();
                    repaint();
//                    return;
                }
                // Single click in flipped mode sets text insertion point
                if (model.isFlipped() && e.getClickCount() == 1) {
                    if (isWithinPhotoBounds(e.getPoint())) {
                        // Start new text block at click position
                        model.startNewTextBlock(e.getPoint());

                        // Request focus for keyboard input
                        requestFocusInWindow();

                        repaint();
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // Start potential drawing stroke
                if (model.isFlipped() && isWithinPhotoBounds(e.getPoint())) {
                    lastMousePosition = e.getPoint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // End drawing stroke
                if (isDrawing) {
                    isDrawing = false;
                    model.finishCurrentStroke();
                    lastMousePosition = null;
                }
            }
        });
    }

    // Mouse motion handling for drawing
    private void setupMouseMotionListeners() {
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // Only draw on flipped side
                if (!model.isFlipped() || lastMousePosition == null) {
                    return;
                }

                // Check if we're within bounds
                if (!isWithinPhotoBounds(e.getPoint())) {
                    return;
                }

                // Start new stroke if needed
                if (!isDrawing) {
                    isDrawing = true;
                    model.startNewStroke(lastMousePosition);
                }

                // Add point to current stroke
                model.addPointToCurrentStroke(e.getPoint());
                lastMousePosition = e.getPoint();

                repaint();
            }
        });
    }

    // Keyboard handling for text input
    private void setupKeyboardListeners() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                // Only accept input when flipped and have active text block
                if (!model.isFlipped() || model.getCurrentTextBlock() == null) {
                    return;
                }

                char c = e.getKeyChar();

                // Ignore control characters except Enter
                if (Character.isISOControl(c)) {
                    if (c == '\n' || c == '\r') {
                        // Add space for line break
                        model.addCharacterToCurrentTextBlock(' ');
                    }
                    // Ignore other control characters
                    return;
                }

                // Add the character
                model.addCharacterToCurrentTextBlock(c);
                repaint();
            }
        });
    }

    // Utility method to check if point is within photo bounds
    private boolean isWithinPhotoBounds(Point p) {
        if (!model.hasPhoto()) {
            return false;
        }

        int width = model.getPhotoWidth();
        int height = model.getPhotoHeight();

        return p.x >= 0 && p.x < width && p.y >= 0 && p.y < height;
    }


    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Color getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
    }

    public PhotoComponentModel getModel() {
        return model;
    }

    @Override
    public void paintComponent(Graphics pen) {
        this.view.paint(pen, this);
    }

    public int getComponentHeight() {
        return componentHeight;
    }

    public void setComponentHeight(int componentHeight) {
        this.componentHeight = componentHeight;
    }

    public int getComponentWidth() {
        return componentWidth;
    }

    public void setComponentWidth(int componentWidth) {
        this.componentWidth = componentWidth;
    }

}
