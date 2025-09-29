import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PhotoComponentModel {
    private boolean isFlipped = false;
    private BufferedImage image;

    private List<Stroke> strokes = new ArrayList<>();
    private List<TextBlock> textBlocks = new ArrayList<>();
    private Stroke currentStroke = null;
    private TextBlock currentTextBlock = null;

    public PhotoComponentModel(String file) {
        try {
            image = ImageIO.read(new File(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    // Inner class for drawn strokes
    public static class Stroke {
        public List<Point> points = new ArrayList<>();
        public Color color = Color.BLACK;

        public void addPoint(Point p) {
            points.add(new Point(p)); // Create copy to avoid reference issues
        }

        public boolean isEmpty() {
            return points.isEmpty();
        }

        public int size() {
            return points.size();
        }
    }

    // Inner class for text annotations
    public static class TextBlock {
        public Point position;
        public StringBuilder text = new StringBuilder();

        public TextBlock(Point pos) {
            this.position = new Point(pos); // Create copy
        }

        public void addCharacter(char c) {
            text.append(c);
        }

        public String getText() {
            return text.toString();
        }

        public boolean isEmpty() {
            return text.length() == 0;
        }
    }

    // Stroke management methods
    public List<Stroke> getStrokes() {
        return strokes;
    }

    public void startNewStroke(Point startPoint) {
        currentStroke = new Stroke();
        currentStroke.addPoint(startPoint);
        strokes.add(currentStroke);
    }

    public void addPointToCurrentStroke(Point p) {
        if (currentStroke != null) {
            currentStroke.addPoint(p);
        }
    }

    public void finishCurrentStroke() {
        currentStroke = null;
    }

    public Stroke getCurrentStroke() {
        return currentStroke;
    }

    // Text management methods
    public List<TextBlock> getTextBlocks() {
        return textBlocks;
    }

    public void startNewTextBlock(Point position) {
        currentTextBlock = new TextBlock(position);
        textBlocks.add(currentTextBlock);
    }

    public void addCharacterToCurrentTextBlock(char c) {
        if (currentTextBlock != null) {
            currentTextBlock.addCharacter(c);
        }
    }

    public TextBlock getCurrentTextBlock() {
        return currentTextBlock;
    }
//    TODO for editing or backspace
//    public void setCurrentTextBlock(TextBlock block) {
//        this.currentTextBlock = block;
//    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        // reset all em variables
        isFlipped = false;
        strokes.clear();
        textBlocks.clear();
        currentStroke = null;
        currentTextBlock = null;

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
