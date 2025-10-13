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

    public void setCurrentStroke(Stroke currentStroke) {
        this.currentStroke = currentStroke;
    }

    public void setCurrentTextBlock(TextBlock currentTextBlock) {
        this.currentTextBlock = currentTextBlock;
    }

    private Color annotationColor = Color.BLACK;

    public Color getAnnotationColor() {
        return annotationColor;
    }

    public void setAnnotationColor(Color annotationColor) {
        this.annotationColor = annotationColor;
    }
    public void changeSelectedAnnotationColor(Color newColor) {
        if (selectedAnnotation == null) {
            return;
        }

        if (selectedAnnotation instanceof Stroke) {
            ((Stroke) selectedAnnotation).setColor(newColor);
        } else if (selectedAnnotation instanceof TextBlock) {
            ((TextBlock) selectedAnnotation).setColor(newColor);
        }
    }
    private List<Stroke> strokes = new ArrayList<>();
    private List<TextBlock> textBlocks = new ArrayList<>();
    private Stroke currentStroke = null;
    private TextBlock currentTextBlock = null;
    private Object selectedAnnotation = null;
/// //////////
    public void setSelectedAnnotation(Object annotation) {
        this.selectedAnnotation = annotation;
    }

    public Object getSelectedAnnotation() {
        return selectedAnnotation;
    }

    public void clearSelection() {
        this.selectedAnnotation = null;
    }

    public boolean isSelected(Object annotation) {
        return selectedAnnotation == annotation;
    }
    /// ///////////////////


    public PhotoComponentModel() {
        this.image = null;
    }


    public static class Stroke {
        public List<Point> points = new ArrayList<>();
        public Color color = Color.BLACK;

        public void addPoint(Point p) {
            points.add(new Point(p));
        }

        public void setColor(Color color) {
            this.color = color;
        }
        public boolean isEmpty() {
            return points.isEmpty();
        }

        public int size() {
            return points.size();
        }
    }

    public static class TextBlock {
        public Point position;
        public StringBuilder text = new StringBuilder();
        public Color color = Color.BLACK;

        public TextBlock(Point pos) {
            this.position = new Point(pos);
        }

        public void addCharacter(char c) {
            text.append(c);
        }

        public String getText() {
            return text.toString();
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public boolean isEmpty() {
            return text.length() == 0;
        }
    }

    public List<Stroke> getStrokes() {
        return strokes;
    }

    public void startNewStroke(Point startPoint) {
        currentStroke = new Stroke();
        currentStroke.color = annotationColor;
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


    public List<TextBlock> getTextBlocks() {
        return textBlocks;
    }

    public void startNewTextBlock(Point position) {
        currentTextBlock = new TextBlock(position);
        currentTextBlock.color = annotationColor;
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


    public Object hitTest(Point p) {
        // Check text blocks first
        for (TextBlock block : textBlocks) {
            int textWidth = block.text.length() * 8;  // rough estimate
            if (p.x >= block.position.x &&
                    p.x <= block.position.x + textWidth &&
                    p.y >= block.position.y - 20 &&
                    p.y <= block.position.y) {
                return block;
            }
        }

        // Check strokes - see if click is near any stroke point
        for (Stroke stroke : strokes) {
            for (Point sp : stroke.points) {
                if (p.distance(sp) < 30) {  // 20 pixels
                    return stroke;
                }
            }
        }

        return null;
    }
}
