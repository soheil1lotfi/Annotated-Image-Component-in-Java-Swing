import java.awt.*;

public class TextBlock implements Drawable {
    private Point position;
    private StringBuilder text = new StringBuilder();
    private Color color = Color.BLACK;

    public Point getPosition() {
        return position;
    }

    @Override
    public void draw(Graphics g) {
        draw(g, g.getFontMetrics(), Integer.MAX_VALUE);
    }

    public void draw(Graphics g, FontMetrics fm, int maxWidth) {
        String textStr = getText();
        if (textStr.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(color);

        int startX = position.x;
        int y = position.y;
        int lineHeight = fm.getHeight();
        int availableWidth = maxWidth - startX;

        String[] words = textStr.split("(?<=\\s)|(?=\\s)");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.toString() + word;
            int lineWidth = fm.stringWidth(testLine);

            if (lineWidth > availableWidth && currentLine.length() > 0) {
                g2.drawString(currentLine.toString(), startX, y);
                y += lineHeight;
                currentLine = new StringBuilder(word);
            } else {
                currentLine.append(word);
            }



            while (fm.stringWidth(currentLine.toString()) > availableWidth) {
                String line = currentLine.toString();
                int breakPoint = line.length() - 1;

                while (breakPoint > 0 && fm.stringWidth(line.substring(0, breakPoint)) > availableWidth) {
                    breakPoint--;
                }

                if (breakPoint == 0) breakPoint = 1;

                g.drawString(line.substring(0, breakPoint) + "-", startX, y);
                y += lineHeight;

                currentLine = new StringBuilder(line.substring(breakPoint));
            }

            if (y > this.position.y + 500) {
                break;
            }
        }

        if (currentLine.length() > 0 && y < this.position.y + 500) {
            g.drawString(currentLine.toString(), startX, y);
        }
    }
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