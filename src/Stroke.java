import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Stroke {
    public List<Point> points = new ArrayList<>();
    public Color color = Color.BLACK;

    public void addPoint(Point p) {
        points.add(new Point(p));
    }

    public int size() {
        return points.size();
    }

    public boolean isEmpty() {
        return points.isEmpty();
    }

    public void setColor(Color color) {
        this.color = color;
    }
}