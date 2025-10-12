import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class PhotoComponent extends JComponent {

    private PhotoComponentModel model;
    private PhotoComponentView view;
    private Color backgroundColor = Color.PINK;


    private boolean isDrawing = false;
    private Point lastMousePosition = null;


    public PhotoComponent() {
        this.model = new PhotoComponentModel();
        this.view = new PhotoComponentView();

        setPreferredSize(new Dimension(400, 300));
        setMinimumSize(new Dimension(400, 300));
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
                if (e.getClickCount() == 2 && model.hasPhoto()) {
                    model.flip();
                    repaint();
//                    return;
                }

                if (model.isFlipped() && e.getClickCount() == 1) {
                    if (isWithinPhotoBounds(e.getPoint())) {
                        model.startNewTextBlock(e.getPoint());

                        requestFocusInWindow();

                        repaint();
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (model.isFlipped() && isWithinPhotoBounds(e.getPoint())) {
                    lastMousePosition = e.getPoint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (isDrawing) {
                    isDrawing = false;
                    model.finishCurrentStroke();
                    lastMousePosition = null;
                }
            }
        });
    }

    private void setupMouseMotionListeners() {
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!model.isFlipped() || lastMousePosition == null) {
                    return;
                }

                if (!isWithinPhotoBounds(e.getPoint())) {
                    return;
                }

                if (!isDrawing) {
                    isDrawing = true;
                    model.startNewStroke(lastMousePosition);
                }

                model.addPointToCurrentStroke(e.getPoint());
                lastMousePosition = e.getPoint();

                repaint();
            }
        });
    }

    private void setupKeyboardListeners() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!model.isFlipped() || model.getCurrentTextBlock() == null) {
                    return;
                }

                char c = e.getKeyChar();

                if (Character.isISOControl(c)) {
                    if (c == '\n' || c == '\r') {
                        model.addCharacterToCurrentTextBlock(' ');
                    }
                    return;
                }

                model.addCharacterToCurrentTextBlock(c);
                repaint();
            }
        });
    }

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


    public PhotoComponentModel getModel() {
        return model;
    }

    @Override
    public void paintComponent(Graphics pen) {
        this.view.paint(pen, this);
    }

    public void setPhoto(BufferedImage image) {
        model.setImage(image);

        if (image != null) {
            int width = image.getWidth();
            int height = image.getHeight();
            setPreferredSize(new Dimension(width, height));
            setSize(new Dimension(width, height));
        }

        revalidate();
        repaint();
    }
}
