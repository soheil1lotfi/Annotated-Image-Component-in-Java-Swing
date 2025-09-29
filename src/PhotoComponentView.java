import java.awt.*;
import java.awt.image.BufferedImage;

public class PhotoComponentView {

    public void paint(Graphics pen, PhotoComponent photoComponent) {
        Graphics2D pen2 = (Graphics2D) pen;

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
//        drawStrokes(g, model);
//        drawTextBlocks(g, model);

        // Draw current text cursor if applicable
//        if (model.getCurrentTextBlock() != null &&
//                model.getCurrentTextBlock().getText().isEmpty()) {
//            drawTextCursor(g, model.getCurrentTextBlock().position);
//        }
    }

}
