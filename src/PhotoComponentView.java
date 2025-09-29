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
//        pen.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

//        for (var drawable : model.getDrawables()) {
//            drawable.draw(pen);
//        }
        BufferedImage image = model.getImage();
        if (image != null) {
            // Draw the image centered or at your desired coordinates
//            int x = (bounds.width - image.getWidth()) / 2;
//            int y = (bounds.height - image.getHeight()) / 2;
//            image = image.getSubimage(x, y, image.getWidth() - 12, image.getHeight()-12);
//            pen.drawImage(image, x, y, null);


            int marginX = (int) (photoComponent.getWidth() * 0.1);
            int marginY = (int) (photoComponent.getHeight() * 0.1);
//
//            int imageX = marginX;
//            int imageY = marginY;
//            int imageWidth = photoComponent.getWidth() - (2 * marginX);
//            int imageHeight = photoComponent.getHeight() - (2 * marginY);
//
//            pen.drawImage(image, imageX, imageY, imageWidth, imageHeight, null);

//            int margin = 20; // Your desired border size

            // Available space for the image (with margins)
            int availableWidth = photoComponent.getWidth() - (2 * marginX);
            int availableHeight = photoComponent.getHeight() - (2 * marginY);

            // Original image dimensions
            int originalWidth = image.getWidth();
            int originalHeight = image.getHeight();

            // Calculate scaling factor to fit within available space while preserving aspect ratio
            double scaleX = (double) availableWidth / originalWidth;
            double scaleY = (double) availableHeight / originalHeight;
            double scale = Math.min(scaleX, scaleY); // Use smaller scale to ensure it fits

            // Calculate actual image dimensions after scaling
            int scaledWidth = (int) (originalWidth * scale);
            int scaledHeight = (int) (originalHeight * scale);

            // Center the image within the available space
            int imageX = marginX + (availableWidth - scaledWidth) / 2;
            int imageY = marginY + (availableHeight - scaledHeight) / 2;

            // Draw the image with preserved aspect ratio
//            pen.drawImage(image, imageX, imageY, scaledWidth, scaledHeight, null);

            // Draw based on flip state
            if (!model.isFlipped()) {
//                drawPhotoFront(pen, model.getPhoto());
                pen.drawImage(image, imageX, imageY, scaledWidth, scaledHeight, null);

            } else {
                drawPhotoBack(pen2, model);
            }
        }
    }
    private void drawPhotoBack(Graphics2D g, PhotoComponentModel model) {
        int width = model.getPhotoWidth();
        int height = model.getPhotoHeight();

        // Draw white surface
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // Draw border
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, width - 1, height - 1);

        // Draw all annotations
        drawStrokes(g, model);
        drawTextBlocks(g, model);

//         Draw current text cursor if applicable
        if (model.getCurrentTextBlock() != null &&
                model.getCurrentTextBlock().getText().isEmpty()) {
            drawTextCursor(g, model.getCurrentTextBlock().position);
        }
    }
    // Draw all strokes
    private void drawStrokes(Graphics2D g, PhotoComponentModel model) {
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2.0f,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));

        for (PhotoComponentModel.Stroke stroke : model.getStrokes()) {
            if (stroke.points.size() < 2) continue;

            Point prev = stroke.points.get(0);
            for (int i = 1; i < stroke.points.size(); i++) {
                Point curr = stroke.points.get(i);
                g.drawLine(prev.x, prev.y, curr.x, curr.y);
                prev = curr;
            }
        }
    }

    // Draw all text blocks with word wrapping
    private void drawTextBlocks(Graphics2D g, PhotoComponentModel model) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        FontMetrics fm = g.getFontMetrics();

        for (PhotoComponentModel.TextBlock block : model.getTextBlocks()) {
            drawTextBlockWithWrap(g, block, fm, model.getPhotoWidth());
        }
    }
    // Draw a single text block with word wrapping
    private void drawTextBlockWithWrap(Graphics2D g,
                                       PhotoComponentModel.TextBlock block,
                                       FontMetrics fm,
                                       int maxWidth) {
        String text = block.getText();
        if (text.isEmpty()) return;

        int x = block.position.x;
        int y = block.position.y;
        int lineHeight = fm.getHeight();

        // Split text into words, keeping spaces
        String[] words = text.split("(?<=\\s)|(?=\\s)");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.toString() + word;
            int lineWidth = fm.stringWidth(testLine);

            // Check if we need to wrap
            if (x + lineWidth > maxWidth && currentLine.length() > 0) {
                // Draw current line
                g.drawString(currentLine.toString(), x, y);

                // Move to next line
                y += lineHeight;
                x = 10; // Small left margin for wrapped lines
                currentLine = new StringBuilder(word);
            } else {
                currentLine.append(word);
            }

            // Check if we've gone past bottom of photo
            if (y > block.position.y + 500) { // Reasonable limit
                break;
            }
        }

        // Draw any remaining text
        if (currentLine.length() > 0 && y < block.position.y + 500) {
            g.drawString(currentLine.toString(), x, y);
        }
    }

    // Draw text insertion cursor
    private void drawTextCursor(Graphics2D g, Point position) {
        g.setColor(Color.BLACK);
        g.drawLine(position.x, position.y - 10, position.x, position.y + 2);
    }
}
