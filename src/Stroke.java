import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Stroke implements Drawable {
    private List<Point> points = new ArrayList<>();
    private Color color = Color.BLACK;

    public Color getColor() {
        return color;
    }

    public List<Point> getPoints() {
        return points;
    }

    @Override
    public void draw(Graphics g) {
        if (points.size() < 2) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(color);
        g2.setStroke(new BasicStroke(6.0f,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));

        Point prev = points.get(0);
        for (int i = 1; i < points.size(); i++) {
            Point curr = points.get(i);
            g2.drawLine(prev.x, prev.y, curr.x, curr.y);
            prev = curr;
        }
    }
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