import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class PhotoComponentModel {
    private boolean isFlipped = false;
    private BufferedImage image;

    public void setCurrentTextBlock(TextBlock currentTextBlock) {
        this.currentTextBlock = currentTextBlock;
    }

    private Color annotationColor = Color.BLACK;

    public void setAnnotationColor(Color annotationColor) {
        this.annotationColor = annotationColor;
    }

    public Color getAnnotationColor() {
        return annotationColor;
    }

    public void changeSelectedAnnotationColor(Color newColor) {
        for (Object annotation : selectedAnnotations) {
            if (annotation instanceof Stroke) {
                ((Stroke) annotation).setColor(newColor);
            } else if (annotation instanceof TextBlock) {
                ((TextBlock) annotation).setColor(newColor);
            }
        }
    }
    private Set<Object> selectedAnnotations = new HashSet<>();
    public void setSelectedAnnotation(Object annotation) {
        this.selectedAnnotations.clear();
        this.selectedAnnotations.add(annotation);
    }
    public void addToSelection(Object annotation) {
        this.selectedAnnotations.add(annotation);
    }
    public void removeFromSelection(Object annotation) {
        this.selectedAnnotations.remove(annotation);
    }
    public Set<Object> getSelectedAnnotations() {
        return selectedAnnotations;
    }
    public void clearSelection() {
        this.selectedAnnotations.clear();
    }

    public boolean isSelected(Object annotation) {
        return selectedAnnotations.contains(annotation);
    }

    private Object selectedAnnotation = null;
    private List<Stroke> strokes = new ArrayList<>();
    private List<TextBlock> textBlocks = new ArrayList<>();
    private Stroke currentStroke = null;

    private TextBlock currentTextBlock = null;

    public PhotoComponentModel() {
        this.image = null;
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


    public Object hitTest(Point p, FontMetrics fm) {

        for (TextBlock block : textBlocks) {
            String text = block.getText();
            if (text.isEmpty()) continue;
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();

            if (p.x >= block.position.x &&
                    p.x <= block.position.x + textWidth &&
                    p.y >= block.position.y - textHeight &&
                    p.y <= block.position.y) {
                return block;
            }
        }

        for (Stroke stroke : strokes) {
            for (Point sp : stroke.points) {
                // 20 pixels
                if (p.distance(sp) < 20) {
                    return stroke;
                }
            }
        }

        return null;
    }
}
