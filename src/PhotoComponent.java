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

                Point imagePoint = transformMouseToImageCoordinates(e.getPoint());
                if (model.isFlipped() && e.getClickCount() == 1) {
                    if (isWithinPhotoBounds(imagePoint)) {
                        FontMetrics fm = getFontMetrics(new Font("Arial", Font.PLAIN, 20));
                        Object hit = model.hitTest(imagePoint, fm);

                        if (hit != null) {
                            // Shift key enables multi-selection
                            if (e.isShiftDown()) {
                                // Toggle selection
                                if (model.isSelected(hit)) {
                                    model.removeFromSelection(hit);
                                } else {
                                    model.addToSelection(hit);
                                }
                            } else {
                                // Normal click - select only this one
                                model.clearSelection();
                                model.setSelectedAnnotation(hit);
                            }

                            // If it's text, allow editing (only if single selection)
                            if (hit instanceof TextBlock && model.getSelectedAnnotations().size() == 1) {
                                model.setCurrentTextBlock((TextBlock) hit);
                                requestFocusInWindow();
                            }
                            repaint();
                            return;
                        } else {
                            // Clicked on empty space
                            if (!e.isShiftDown()) {
                                model.clearSelection();
                            }
                            model.startNewTextBlock(imagePoint);
                            requestFocusInWindow();
                            repaint();
                            return;
                        }
                    } else {
                        return;
                    }
                }

                if (!e.isShiftDown()) {
                    model.clearSelection();
                }
                model.startNewTextBlock(imagePoint);
                requestFocusInWindow();
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                Point imagePoint = transformMouseToImageCoordinates(e.getPoint());
                if (!model.isFlipped() || !isWithinPhotoBounds(imagePoint)) {
                    return;
                }
                FontMetrics fm = getFontMetrics(new Font("Arial", Font.PLAIN, 20));

                Object hit = model.hitTest(imagePoint, fm);

                if (hit != null && model.isSelected(hit)) {
                    // Start dragging all selected annotations
                    dragStart = imagePoint;
                    draggedAnnotation = hit;  // Keep for reference
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
                if (!model.isFlipped()) {
                    return;
                }

                Point imagePoint = transformMouseToImageCoordinates(e.getPoint());

                if (!isWithinPhotoBounds(imagePoint)) {
                    if (isDrawing) {
                        isDrawing = false;
                    }
                    return;
                }

                if (dragStart != null && draggedAnnotation != null) {
                    int dx = imagePoint.x - dragStart.x;
                    int dy = imagePoint.y - dragStart.y;

                    for (Object annotation : model.getSelectedAnnotations()) {
                        if (annotation instanceof Stroke) {
                            Stroke stroke = (Stroke) annotation;
                            for (Point p : stroke.points) {
                                p.translate(dx, dy);
                            }
                        } else if (annotation instanceof TextBlock) {
                            TextBlock block = (TextBlock) annotation;
                            block.position.translate(dx, dy);
                        }
                    }

                    dragStart = imagePoint;
                    repaint();
                }
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

        revalidate();
        repaint();
    }

    public void setAnnotationCanvasWide (Color annotationColor) {
        model.setAnnotationColor(annotationColor);
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

        // calculatin the same scaling as view
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

        //(mousePoint - translation) / scale
        int transformedX = (int) ((mousePoint.x - imageX) / scale);
        int transformedY = (int) ((mousePoint.y - imageY) / scale);

        return new Point(transformedX, transformedY);
    }
}