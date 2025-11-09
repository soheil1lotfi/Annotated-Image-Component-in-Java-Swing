import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
public class PhotoComponent extends JComponent implements ChangeListener {

    private PhotoComponentModel model;
    private PhotoComponentView view;

    private Color backgroundColor = Color.WHITE;
    private boolean isDrawing = false;
    private Point lastMousePosition = null;
    private Point dragStart = null;
    private Object draggedAnnotation = null;

    public PhotoComponent() {
        this.model = new PhotoComponentModel();
        this.view = new PhotoComponentView();

        this.model.addChangeListener(this);

        setLayout(new BorderLayout());
        add(view, BorderLayout.CENTER);

        installListeners();
        updateView();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        updateView();
    }

    private void updateView() {
        view.updateDisplay(
                model.getImage(),
                model.isFlipped(),
                model.getStrokes(),
                model.getTextBlocks(),
                model.getSelectedAnnotations(),
                model.getFONT_SIZE(),
                model.getSTROKE_WIDTH()
        );
    }
    private void installListeners() {
        view.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClicked(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                handleMouseReleased(e);
            }
        });

        view.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDragged(e);
            }
        });

        view.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                handleKeyTyped(e);
            }
        });
    }

    public void handleMouseClicked(MouseEvent e) {
        if (!model.hasPhoto()) {
            return;
        }

        Point imagePoint = view.transformMouseToImageCoordinates(
                e.getPoint(),
                model.getPhotoWidth(),
                model.getPhotoHeight()
        );

        if (e.getClickCount() == 2) {
            model.flip();
            return;
        }

        if (model.isFlipped() && e.getClickCount() == 1) {
            if (isWithinPhotoBounds(imagePoint)) {
                Font font = new Font("Arial", Font.PLAIN, model.getFONT_SIZE());
                FontMetrics fm = view.getFontMetrics(font);
                Object hit = model.hitTest(imagePoint, fm);

                if (hit != null) {
                    if (e.isShiftDown()) {
                        if (model.isSelected(hit)) {
                            model.removeFromSelection(hit);
                        } else {
                            model.addToSelection(hit);
                        }
                    } else {
                        model.clearSelection();
                        model.setSelectedAnnotation(hit);
                    }

                    if (hit instanceof TextBlock && model.getSelectedAnnotations().size() == 1) {
                        model.setCurrentTextBlock((TextBlock) hit);
                        view.requestFocusInWindow();
                    }
                } else {
                    if (!e.isShiftDown()) {
                        model.clearSelection();
                    }
                    model.startNewTextBlock(imagePoint);
                    view.requestFocusInWindow();
                }
            }
        }
    }

    public void handleMousePressed(MouseEvent e) {
        if (!model.isFlipped() || !model.hasPhoto()) {
            return;
        }

        Point imagePoint = view.transformMouseToImageCoordinates(
                e.getPoint(),
                model.getPhotoWidth(),
                model.getPhotoHeight()
        );

        if (!isWithinPhotoBounds(imagePoint)) {
            return;
        }

        Font font = new Font("Arial", Font.PLAIN, model.getFONT_SIZE());
        FontMetrics fm = view.getFontMetrics(font);
        Object hit = model.hitTest(imagePoint, fm);

        if (hit != null && model.isSelected(hit)) {
            dragStart = imagePoint;
            draggedAnnotation = hit;
        } else if (!e.isShiftDown()) {
            model.clearSelection();
            lastMousePosition = imagePoint;
            repaint();
        } else {
            lastMousePosition = imagePoint;
            repaint();
        }
    }

    public void handleMouseReleased(MouseEvent e) {
        if (isDrawing) {
            isDrawing = false;
            model.finishCurrentStroke();
            lastMousePosition = null;
        }
        dragStart = null;
        draggedAnnotation = null;
    }

    public void handleMouseDragged(MouseEvent e) {
        if (!model.isFlipped() || !model.hasPhoto()) {
            return;
        }

        Point imagePoint = view.transformMouseToImageCoordinates(
                e.getPoint(),
                model.getPhotoWidth(),
                model.getPhotoHeight()
        );

        if (!isWithinPhotoBounds(imagePoint)) {
            if (isDrawing) {
                isDrawing = false;
                model.finishCurrentStroke();
            }
            return;
        }

        if (dragStart != null && draggedAnnotation != null) {
            int dx = imagePoint.x - dragStart.x;
            int dy = imagePoint.y - dragStart.y;
            model.moveSelectedAnnotations(dx, dy);
            dragStart = imagePoint;
        } else if (lastMousePosition != null) {
            if (!isDrawing) {
                isDrawing = true;
                model.startNewStroke(lastMousePosition);
            }
            model.addPointToCurrentStroke(imagePoint);
            lastMousePosition = imagePoint;
        }
    }

    public void handleKeyTyped(KeyEvent e) {
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


    public void setPhoto(BufferedImage image) {
        model.setImage(image);

    }

    public void setAnnotationCanvasWide (Color annotationColor) {
        model.setAnnotationColor(annotationColor);
    }

    public void changeAnnotationColor(Color color){
        model.changeSelectedAnnotationColor(color);
    }

    private Point transformMouseToImageCoordinates(Point mousePoint) {
        if (!model.hasPhoto()) {
            return mousePoint;
        }

        // calculating the same scaling as view
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

