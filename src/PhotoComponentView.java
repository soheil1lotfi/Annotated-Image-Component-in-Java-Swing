import java.awt.*;
import java.awt.image.BufferedImage;

public class PhotoComponentView {

    public void paint(Graphics pen, PhotoComponent photoComponent) {

        Graphics2D pen2 = (Graphics2D) pen;
        pen2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        pen2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        var model = photoComponent.getModel();
        pen.setColor(photoComponent.getBackgroundColor());
        var bounds = photoComponent.getBounds();
        pen.fillRect(0, 0, photoComponent.getWidth(), photoComponent.getHeight());

        BufferedImage image = model.getImage();
        if (image != null) {

//           Changed from the last assignment to avoid the padding around the photo and scale it to the componant
            int availableWidth = bounds.width;
            int availableHeight = bounds.height;

            int originalWidth = image.getWidth();
            int originalHeight = image.getHeight();

            double scaleX = (double) availableWidth / originalWidth;
            double scaleY = (double) availableHeight / originalHeight;
            double scale = Math.min(scaleX, scaleY);

            int scaledWidth = (int) (originalWidth * scale);
            int scaledHeight = (int) (originalHeight * scale);

            int imageX = (availableWidth - scaledWidth) / 2;
            int imageY = (availableHeight - scaledHeight) / 2;

            // Save the original transform
            var originalTransform = pen2.getTransform();

            // Apply translation to center the image
            pen2.translate(imageX, imageY);

            // Apply scaling - now everything drawn will be scaled!
            pen2.scale(scale, scale);

            if (!model.isFlipped()) {
                // Draw image at 0,0 in the scaled coordinate system
                pen.drawImage(image, 0, 0, originalWidth, originalHeight, null);
            } else {
                // Draw image and annotations - all automatically scaled!
                pen.drawImage(image, 0, 0, originalWidth, originalHeight, null);
                drawPhotoBackAnnotations(pen2, model, originalWidth);  // ← Pass original width now
            }

            // Restore original transform
            pen2.setTransform(originalTransform);
        }
    }
//    private void drawPhotoBack(Graphics2D g, PhotoComponentModel model, PhotoComponent photoComponent) {
//        int width = photoComponent.getWidth();
//        int height = photoComponent.getHeight();
//
//        g.setColor(Color.WHITE);
//        g.fillRect(0, 0, width, height);
//
//        g.setColor(Color.BLACK);
//        g.drawRect(0, 0, width - 1, height - 1);
//
//        drawStrokes(g, model);
//        drawTextBlocks(g, model, photoComponent);
//    }

    private void drawPhotoBackAnnotations(Graphics2D g, PhotoComponentModel model, int imageWidth) {
//        int width = photoComponent.getWidth();
//        int height = photoComponent.getHeight();
//        g.setColor(Color.WHITE);
//        g.fillRect(0, 0, width, height);
        drawStrokes(g, model);
        drawTextBlocks(g, model, imageWidth);
    }

    private void drawStrokes(Graphics2D g, PhotoComponentModel model) {
        g.setColor(model.getAnnotationColor());
        g.setStroke(new BasicStroke(2.0f,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));

        for (Stroke stroke : model.getStrokes()) {
            if (stroke.points.size() < 2) continue;

            g.setColor(stroke.color);

            Point prev = stroke.points.get(0);
            for (int i = 1; i < stroke.points.size(); i++) {
                Point curr = stroke.points.get(i);
                g.drawLine(prev.x, prev.y, curr.x, curr.y);
                prev = curr;
            }
        }
    }

    private void drawTextBlocks(Graphics2D g, PhotoComponentModel model, int maxWidth) {
        g.setColor(model.getAnnotationColor());
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        FontMetrics fm = g.getFontMetrics();

        for (TextBlock block : model.getTextBlocks()) {
            g.setColor(block.color);
            drawTextBlockWithWrap(g, block, fm, maxWidth);
        }
    }

//    private void drawTextBlockWithWrap(Graphics2D g,
//                                       TextBlock block,
//                                       FontMetrics fm,
//                                       int maxWidth) {
//        String text = block.getText();
//        if (text.isEmpty()) return;
//
//        int startX = block.position.x;
//        int y = block.position.y;
//        int lineHeight = fm.getHeight();
//        int availableWidth = maxWidth - startX;  // Calculate available space once
//
//        String[] words = text.split("(?<=\\s)|(?=\\s)");
//        StringBuilder currentLine = new StringBuilder();
//
//        for (String word : words) {
//            String testLine = currentLine.toString() + word;
//            int lineWidth = fm.stringWidth(testLine);
//
//            // Check if the line width exceeds available space
//            if (lineWidth > availableWidth && currentLine.length() > 0) {
//                // Draw current line before adding the word
//                g.drawString(currentLine.toString(), startX, y);
//
//                // Move to next line and start fresh with the word that didn't fit
//                y += lineHeight;
//                currentLine = new StringBuilder(word);
//            } else {
//                currentLine.append(word);
//            }
//
//            if (y > block.position.y + 500) {
//                break;
//            }
//        }
//
//        // Draw the last line
//        if (currentLine.length() > 0 && y < block.position.y + 500) {
//            g.drawString(currentLine.toString(), startX, y);
//        }
//    }

    private void drawTextBlockWithWrap(Graphics2D g,
                                       TextBlock block,
                                       FontMetrics fm,
                                       int maxWidth) {
        String text = block.getText();
        if (text.isEmpty()) return;

        int startX = block.position.x;
        int y = block.position.y;
        int lineHeight = fm.getHeight();
        int availableWidth = maxWidth - startX;

        String[] words = text.split("(?<=\\s)|(?=\\s)");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.toString() + word;
            int lineWidth = fm.stringWidth(testLine);

            if (lineWidth > availableWidth && currentLine.length() > 0) {
                // Draw current line
                g.drawString(currentLine.toString(), startX, y);
                y += lineHeight;
                currentLine = new StringBuilder(word);
            } else {
                currentLine.append(word);
            }

            // Check if the current line (even just the word alone) is too long
            while (fm.stringWidth(currentLine.toString()) > availableWidth) {
                // Break the word character by character
                String line = currentLine.toString();
                int breakPoint = line.length() - 1;

                // Find how many characters fit
                while (breakPoint > 0 && fm.stringWidth(line.substring(0, breakPoint)) > availableWidth) {
                    breakPoint--;
                }

                if (breakPoint == 0) breakPoint = 1; // Always take at least one character

                // Draw what fits
                g.drawString(line.substring(0, breakPoint) + "-", startX, y);
                y += lineHeight;

                // Keep the rest for the next line
                currentLine = new StringBuilder(line.substring(breakPoint));
            }

            if (y > block.position.y + 500) {
                break;
            }
        }

        // Draw the last line
        if (currentLine.length() > 0 && y < block.position.y + 500) {
            g.drawString(currentLine.toString(), startX, y);
        }
    }
}
