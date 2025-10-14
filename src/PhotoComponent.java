import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.List;
public class PhotoComponent extends JComponent {

    private PhotoComponentModel model;
    private PhotoComponentView view;
    private Color backgroundColor = Color.PINK;

    private boolean isDrawing = false;
    private Point lastMousePosition = null;


    public PhotoComponent() {
        this.model = new PhotoComponentModel();
        this.view = new PhotoComponentView();

//        setPreferredSize(new Dimension(400, 300));
//        setMinimumSize(new Dimension(400, 300));
//        setMaximumSize(new Dimension(400, 300));
//        setSize(new Dimension(400, 300));

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
                    return;
                }

                Point imagePoint = transformMouseToImageCoordinates(e.getPoint());  // ← Transform!
                if (model.isFlipped() && e.getClickCount() == 1) {

                    if (isWithinPhotoBounds(imagePoint)) {
                        model.clearSelection();

                        Object hit = model.hitTest(imagePoint);

                        if (hit != null) {
                            // Clicked on an annotation - select it
                            model.setSelectedAnnotation(hit);

                            // If it's text, allow editing
                            if (hit instanceof TextBlock) {
                                model.setCurrentTextBlock((TextBlock) hit);
                                requestFocusInWindow();
                            }
                            return;
                        } else {
                            // Clicked on empty space - create new text
                            model.clearSelection();
                            model.startNewTextBlock(imagePoint);
                            requestFocusInWindow();
                        }
                        return;
                    }
                    else {
                        return;
                    }
                }
                model.clearSelection();
                model.startNewTextBlock(imagePoint);
                requestFocusInWindow();
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
//                if (model.isFlipped() && isWithinPhotoBounds(e.getPoint())) {
//                    lastMousePosition = e.getPoint();
//                }
                Point imagePoint = transformMouseToImageCoordinates(e.getPoint());  // ← Transform!
                if (!model.isFlipped() || !isWithinPhotoBounds(imagePoint)) {
                    return;
                }
                if (!isWithinPhotoBounds(imagePoint)) {
                    return;
                }

                // Check if pressing on selected annotation
                Object hit = model.hitTest(imagePoint);

                if (hit != null && model.isSelected(hit)) {
                    // Start dragging the selected annotation
                    dragStart = imagePoint;
                    draggedAnnotation = hit;
                } else {
                    // Start drawing a stroke
                    lastMousePosition = imagePoint;
                }
                
            }

            @Override
            public void mouseReleased(MouseEvent e) {
//                if (isDrawing) {
//                    isDrawing = false;
//                    model.finishCurrentStroke();
//                    lastMousePosition = null;
//                }
                if (isDrawing) {
                    isDrawing = false;
                    model.finishCurrentStroke();
                    lastMousePosition = null;
                }

                // Finish dragging
                dragStart = null;
                draggedAnnotation = null;
//                model.clearSelection();

            }
        });
    }

    private void setupMouseMotionListeners() {
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!model.isFlipped() || !isWithinPhotoBounds(e.getPoint())) {
                    return;
                }
                Point imagePoint = transformMouseToImageCoordinates(e.getPoint());


                if (!isWithinPhotoBounds(imagePoint)) {
                    if (isDrawing) {
                        isDrawing = false;
                    }
                    return;
                }
                // Are we dragging a selected annotation?
                if (dragStart != null && draggedAnnotation != null) {
                    int dx = imagePoint.x - dragStart.x;
                    int dy = imagePoint.y - dragStart.y;

                    // Move the annotation
                    if (draggedAnnotation instanceof Stroke) {
                        Stroke stroke =
                                (Stroke) draggedAnnotation;
                        for (Point p : stroke.points) {
                            p.translate(dx, dy);
                        }
                    } else if (draggedAnnotation instanceof TextBlock) {
                        TextBlock block =
                                (TextBlock) draggedAnnotation;
                        block.position.translate(dx, dy);
                    }

                    dragStart = imagePoint;
                    repaint();
                }
                // Otherwise, draw a stroke
                else if (lastMousePosition != null) {
                    if (!isDrawing) {
                        isDrawing = true;
                        model.startNewStroke(lastMousePosition);
                    }

                    model.addPointToCurrentStroke(imagePoint);
                    lastMousePosition = imagePoint;
                    repaint();
                }
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

//        if (image != null) {
//            int width = image.getWidth();
//            int height = image.getHeight();
//            setPreferredSize(new Dimension(width, height));
//            setSize(new Dimension(width, height));
//        }

        revalidate();
        repaint();
    }


    private Point dragStart = null;
    private Object draggedAnnotation = null;

    public void changeAnnotationColor(){
        Color color = GalleryWindow.annotationColor;
//        model.setAnnotationColor(color);
        model.changeSelectedAnnotationColor(color);
        repaint();
    }

    private Point transformMouseToImageCoordinates(Point mousePoint) {
        if (!model.hasPhoto()) {
            return mousePoint;
        }

        // Calculate the same scaling as in the view
        int availableWidth = getWidth();
        int availableHeight = getHeight();

        int originalWidth = model.getPhotoWidth();
        int originalHeight = model.getPhotoHeight();

        double scaleX = (double) availableWidth / originalWidth;
        double scaleY = (double) availableHeight / originalHeight;
        double scale = Math.min(scaleX, scaleY);

        int scaledWidth = (int) (originalWidth * scale);
        int scaledHeight = (int) (originalHeight * scale);

        int imageX = (availableWidth - scaledWidth) / 2;
        int imageY = (availableHeight - scaledHeight) / 2;

        // Transform: (mousePoint - translation) / scale
        int transformedX = (int) ((mousePoint.x - imageX) / scale);
        int transformedY = (int) ((mousePoint.y - imageY) / scale);

        return new Point(transformedX, transformedY);
    }
}
