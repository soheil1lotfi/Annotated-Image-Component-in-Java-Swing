import java.awt.*;
import java.awt.event.*;
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

            var originalTransform = pen2.getTransform();

            pen2.translate(imageX, imageY);

            pen2.scale(scale, scale);

            if (!model.isFlipped()) {
                pen.drawImage(image, 0, 0, originalWidth, originalHeight, null);
            } else {
                pen.drawImage(image, 0, 0, originalWidth, originalHeight, null);
                drawPhotoBackAnnotations(pen2, photoComponent, originalWidth);
            }

            pen2.setTransform(originalTransform);
        }
    }

//  From the last assignment. I let it be here for now :)
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

    private void drawPhotoBackAnnotations(Graphics2D g, PhotoComponent photoComponent, int imageWidth) {
//        drawStrokes(g, photoComponent);
//        drawTextBlocks(g, photoComponent, imageWidth);
        var model = photoComponent.getModel();
        FontMetrics fm = g.getFontMetrics(new Font("Arial", Font.PLAIN, model.getFONT_SIZE()));

        for (Stroke stroke : model.getStrokes()) {
            stroke.draw(g);
        }

        for (TextBlock block : model.getTextBlocks()) {
            block.draw(g, fm, imageWidth);
        }
    }

    public void installUI(PhotoComponent component) {
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                component.handleMouseClicked(event);
            }
            @Override
            public void mousePressed(MouseEvent event) {
                component.handleMousePressed(event);
            }
            @Override
            public void mouseReleased(MouseEvent event) {
                component.handleMouseReleased(event);
            }
        });
        component.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent event) {
                component.handleMouseDragged(event);
            }
        });

        component.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent event) {
                component.handleKeyTyped(event);
            }
        });

    }

}
