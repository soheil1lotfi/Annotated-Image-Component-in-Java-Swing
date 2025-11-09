import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Set;

import static javax.swing.text.StyleConstants.setBackground;

public class PhotoComponentView extends JPanel {


    private BufferedImage currentImage;
    private boolean currentFlipped;
    private ArrayList<Stroke> currentStrokes;
    private ArrayList<TextBlock> currentTextBlocks;
    private Set<Object> currentSelectedAnnotations;
    private int fontSize = 20;
    private float strokeWidth = 6.0f;

    public PhotoComponentView() {
        setFocusable(true);
        setBackground(Color.WHITE);
    }

    public void updateDisplay(BufferedImage image,
                              boolean isFlipped,
                              ArrayList<Stroke> strokes,
                              ArrayList<TextBlock> textBlocks,
                              Set<Object> selectedAnnotations,
                              int fontSize,
                              float strokeWidth) {
        this.currentImage = image;
        this.currentFlipped = isFlipped;
        this.currentStrokes = strokes;
        this.currentTextBlocks = textBlocks;
        this.currentSelectedAnnotations = selectedAnnotations;
        this.fontSize = fontSize;
        this.strokeWidth = strokeWidth;
        repaint();
    }

    public void paintComponent(Graphics pen) {
        super.paintComponent(pen);

        Graphics2D pen2 = (Graphics2D) pen;
        pen2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        pen2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


        if (currentImage != null) {

//           Changed from the last assignment to avoid the padding around the photo and scale it to the componant
            int availableWidth = getWidth();
            int availableHeight = getHeight();

            int originalWidth = currentImage.getWidth();
            int originalHeight = currentImage.getHeight();

            double scaleX = (double) availableWidth / originalWidth;
            double scaleY = (double) availableHeight / originalHeight;
            double scale = Math.min(scaleX, scaleY);

            int scaledWidth = (int) (originalWidth * scale);
            int scaledHeight = (int) (originalHeight * scale);

            int imageX = (availableWidth - scaledWidth) / 2;
            int imageY = (availableHeight - scaledHeight) / 2;

            var originalTransform = pen2.getTransform();

            pen2.translate(imageX, imageY);

            pen2.scale(scale, scale);

            if (!currentFlipped) {
                pen.drawImage(currentImage, 0, 0, originalWidth, originalHeight, null);
            } else {
                pen.drawImage(currentImage, 0, 0, originalWidth, originalHeight, null);
                drawPhotoBackAnnotations(pen2, originalWidth);
            }

            pen2.setTransform(originalTransform);
        }
    }

    private void drawPhotoBackAnnotations(Graphics2D g, int imageWidth) {

        FontMetrics fm = g.getFontMetrics(new Font("Arial", Font.PLAIN, fontSize));

        for (Stroke stroke : currentStrokes) {
            stroke.draw(g);
        }

        for (TextBlock block : currentTextBlocks) {
            block.draw(g, fm, imageWidth);
        }
    }


    public Point transformMouseToImageCoordinates(Point mousePoint, int imageWidth, int imageHeight) {
        int availableWidth = getWidth();
        int availableHeight = getHeight();

        double scaleX = (double) availableWidth / imageWidth;
        double scaleY = (double) availableHeight / imageHeight;
        double scale = Math.min(scaleX, scaleY);

        int scaledWidth = (int) (imageWidth * scale);
        int scaledHeight = (int) (imageHeight * scale);
        int imageX = (availableWidth - scaledWidth) / 2;
        int imageY = (availableHeight - scaledHeight) / 2;

        int transformedX = (int) ((mousePoint.x - imageX) / scale);
        int transformedY = (int) ((mousePoint.y - imageY) / scale);

        return new Point(transformedX, transformedY);
    }
}

