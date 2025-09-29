import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PhotoComponentModel {
    private boolean isFlipped = false;
    private BufferedImage image;


    public PhotoComponentModel(String file) {
        try {
            image = ImageIO.read(new File(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    public boolean isFlipped() {
        return isFlipped;
    }

    public void flip() {
        isFlipped = !isFlipped;
    }
    public boolean hasPhoto() {
        return image != null;
    }
    public int getPhotoWidth() {
        return image != null ? image.getWidth() : 0;
    }

    public int getPhotoHeight() {
        return image != null ? image.getHeight() : 0;
    }
}
