import java.awt.*;

public class TextBlock {
    public Point position;
    public StringBuilder text = new StringBuilder();
    public Color color = Color.BLACK;

    public TextBlock(Point p) {
        this.position = new Point(p);
    }

    public void addCharacter(char c) {
        text.append(c);
    }

    public String getText() {
        return text.toString();
    }

    public boolean isEmpty() {
        return text.isEmpty();
    }

    public void setColor(Color color) {
        this.color = color;
    }
}