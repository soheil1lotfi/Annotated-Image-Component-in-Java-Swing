import javax.imageio.ImageIO;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class PhotoComponentModel {

    private List<ChangeListener> changeListeners = new ArrayList<>();

    public void addChangeListener(ChangeListener listener) {
        changeListeners.add(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeListeners.remove(listener);
    }

    protected void notifyChangeListeners() {
        ChangeEvent event = new ChangeEvent(this);
        for (ChangeListener listener : changeListeners) {
            listener.stateChanged(event);
        }
    }

    private boolean isFlipped = false;
    private BufferedImage image;
    private Color annotationColor = Color.BLACK;
    private ArrayList<Stroke> strokes = new ArrayList<>();
    private ArrayList<TextBlock> textBlocks = new ArrayList<>();
    private Stroke currentStroke = null;
    private TextBlock currentTextBlock = null;
    private final int HIT_TEST_THRESHOLD = 25;
    private final float STROKE_WIDTH = 6.0f;
    private final int FONT_SIZE = 20;
    private Set<Object> selectedAnnotations = new HashSet<>();

    public PhotoComponentModel() {
        this.image = null;
    }
    public int getHIT_TEST_THRESHOLD() {
        return HIT_TEST_THRESHOLD;
    }

    public float getSTROKE_WIDTH() {
        return STROKE_WIDTH;
    }

    public int getFONT_SIZE() {
        return FONT_SIZE;
    }


    public void setCurrentTextBlock(TextBlock currentTextBlock) {
        this.currentTextBlock = currentTextBlock;
        /// /////////////
        notifyChangeListeners();

    }

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
        /// ///////
        notifyChangeListeners();

    }

    public void setSelectedAnnotation(Object annotation) {
        this.selectedAnnotations.clear();
        this.selectedAnnotations.add(annotation);
        /// /////////
        notifyChangeListeners();

    }

    public void addToSelection(Object annotation) {
        this.selectedAnnotations.add(annotation);
        /// /////
        notifyChangeListeners();

    }

    public void removeFromSelection(Object annotation) {
        this.selectedAnnotations.remove(annotation);
        /// /////////
        notifyChangeListeners();

    }

    public Set<Object> getSelectedAnnotations() {
        return selectedAnnotations;
    }

    public void clearSelection() {
        this.selectedAnnotations.clear();
        /// ////////////
        notifyChangeListeners();

    }

    public boolean isSelected(Object annotation) {
        return selectedAnnotations.contains(annotation);
    }


    public ArrayList<Stroke> getStrokes() {
        return strokes;
    }

    public void startNewStroke(Point startPoint) {
        currentStroke = new Stroke();
        currentStroke.setColor(annotationColor);
        currentStroke.addPoint(startPoint);
        strokes.add(currentStroke);
        /// ////////////
        notifyChangeListeners();

    }

    public void addPointToCurrentStroke(Point p) {
        if (currentStroke != null) {
            currentStroke.addPoint(p);
            /// /////////////
            notifyChangeListeners();
        }
    }

    public void finishCurrentStroke() {
        currentStroke = null;
        notifyChangeListeners();

    }

    public ArrayList<TextBlock> getTextBlocks() {
        return textBlocks;
    }

    public void startNewTextBlock(Point position) {
        currentTextBlock = new TextBlock(position);
        currentTextBlock.setColor(annotationColor);
        textBlocks.add(currentTextBlock);
        /// //////////
        notifyChangeListeners();

    }

    public void addCharacterToCurrentTextBlock(char c) {
        if (currentTextBlock != null) {
            currentTextBlock.addCharacter(c);
            /// //////////
            notifyChangeListeners();

        }
    }

    public TextBlock getCurrentTextBlock() {
        return currentTextBlock;
    }

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
        /// ///////
        selectedAnnotations.clear();
        notifyChangeListeners();
        /// ////////
    }

    public boolean isFlipped() {
        return isFlipped;
    }

    public void flip() {
        isFlipped = !isFlipped;
        /// /////
        notifyChangeListeners();

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


    public void moveSelectedAnnotations(int dx, int dy) {
        for (Object annotation : selectedAnnotations) {
            if (annotation instanceof Stroke) {
                Stroke stroke = (Stroke) annotation;
                for (Point p : stroke.getPoints()) {
                    p.translate(dx, dy);
                }
            } else if (annotation instanceof TextBlock) {
                TextBlock block = (TextBlock) annotation;
                block.getPosition().translate(dx, dy);
            }
        }
        notifyChangeListeners();
    }


    public Object hitTest(Point p, FontMetrics fm) {

        for (TextBlock block : textBlocks) {
            String text = block.getText();
            if (text.isEmpty()) continue;
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();

            if (p.x >= block.getPosition().x &&
                    p.x <= block.getPosition().x + textWidth &&
                    p.y >= block.getPosition().y - textHeight &&
                    p.y <= block.getPosition().y) {
                return block;
            }
        }

        for (Stroke stroke : strokes) {
            for (Point sp : stroke.getPoints()) {
                if (p.distance(sp) < this.getHIT_TEST_THRESHOLD()) {
                    return stroke;
                }
            }
        }

        return null;
    }
}
