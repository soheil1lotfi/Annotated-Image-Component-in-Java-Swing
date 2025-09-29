import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
//        var GalleryWindow = new GalleryWindow();
//        GalleryWindow.setVisible(true);

//        var window = new JFrame("Window");
//        var photoComponent = new PhotoComponent("assets/Touch_ID_Logo.svg.png");
//
//// Create a panel with FlowLayout to hold your component
//        JPanel panel = new JPanel(new FlowLayout());
//        panel.add(photoComponent);
//
//        window.add(panel); // Add the panel instead
//        window.setPreferredSize(new Dimension(800, 600));
//        window.pack();
//        window.setVisible(true);

        var window = new JFrame("Window");
        window.setLayout(new FlowLayout()); // This respects preferred sizes
        var photoComponent = new PhotoComponent("assets/Touch_ID_Logo.svg.png");
        window.add(photoComponent);
        window.setPreferredSize(new Dimension(800, 600));
        window.pack();
        window.setVisible(true);

    }
}
