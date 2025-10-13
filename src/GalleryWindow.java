import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class GalleryWindow extends javax.swing.JFrame {
    private JLabel statusBar;
    // I had an idea to put a border around the panel to see if it reacts correctly to resizes and checked the how-to of
    // making borders here: https://docs.oracle.com/javase/tutorial/uiswing/components/border.html
    private final Border blueline = BorderFactory.createLineBorder(Color.blue);

    public PhotoComponent photoComponent = null;
    private JPanel emptyPanel;
    private JScrollPane scrollPane;
    public static Color annotationColor = Color.BLACK;


    public GalleryWindow() {

//        var mainWindow = new JFrame("Gallery");
        super("Gallery");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize( 800, 600);
        setMinimumSize(new Dimension(400, 400));

        initializeComponents();

        addStatusBar();
        addTabs();
        addMenu();
        addCenterPanel();


    }
    private void initializeComponents() {
        emptyPanel = new JPanel();
        emptyPanel.setBorder(blueline);
        emptyPanel.setBackground(Color.LIGHT_GRAY);

        JLabel label = new JLabel("No photo loaded. Use File → Import to load a photo.");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        emptyPanel.add(label);

        scrollPane = new JScrollPane(emptyPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    }

    public void addCenterPanel() {
//        var panel = new JPanel();
//        panel.setBorder(blueline);
//
//        // I wanted to see if the wheels are there :)
//        JLabel label = new JLabel("Just tryna see if I have gotten the scrollables or not");
//        label.setPreferredSize(new Dimension(1200, 800));
//        label.setHorizontalAlignment(SwingConstants.CENTER);
//        panel.add(label);
//
//        JScrollPane scrollFrame = new JScrollPane(panel);
//        scrollFrame.setPreferredSize(new Dimension( 3000,3000));
//        add(scrollFrame, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.CENTER);

    }

    public void statusBarMessage(String message) {
        statusBar.setText(message);

    }

    public void addStatusBar() {
        statusBar = new JLabel("Status Bar");
        add(statusBar, BorderLayout.SOUTH);
    }

    public void addTabs() {

        // JToggleButton for the tabs
        JPanel tabsPanel = new JPanel();
        var toolBar1 = new JToggleButton("People", true);
        var toolBar2 = new JToggleButton("Places");

        // setting their color
        toolBar1.setBackground(Color.white);
        toolBar2.setBackground(Color.white);
        new JButton();
        var groupToolBar = new ButtonGroup();
        groupToolBar.add(toolBar1);
        groupToolBar.add(toolBar2);
        // Layout of the tab, https://stackoverflow.com/questions/22918890/make-jbutton-stretch-to-fit-jpanel
        tabsPanel.setLayout(new GridLayout(groupToolBar.getButtonCount(), 1));
        tabsPanel.add(toolBar1);
        tabsPanel.add(toolBar2);

        add(tabsPanel, BorderLayout.WEST);
        toolBar1.addActionListener(e -> {
            statusBarMessage("People selected");
        });
        toolBar2.addActionListener(e -> {
            statusBarMessage("Places selected");
        });
    }

    public void addMenu() {
        JPanel mainPanel = new JPanel();
        JMenuBar menuBar = new JMenuBar();

        JMenu menuFile = new JMenu("File");
        JMenu menuView = new JMenu("View");

        JMenuItem menuViewItem1 = new JRadioButtonMenuItem("Photo Viewer", true);
        JMenuItem menuViewItem2 = new JRadioButtonMenuItem("Browser");

        JButton colorChooser = new JButton();
        colorChooser.setBackground(Color.BLACK);

        JLabel colorChooserLabel = new JLabel("Choose Color: ");
        colorChooser.addActionListener(e -> {
            JColorChooser chooser = new JColorChooser();
            Color selectedColor = JColorChooser.showDialog(colorChooser, "Choose your color", Color.BLACK);
            colorChooser.setBackground(selectedColor);

            if (selectedColor != null) {
                colorChooser.setBackground(selectedColor);
                annotationColor = selectedColor;

                if (photoComponent != null) {
                    photoComponent.changeAnnotationColor();
                }
            }
        });
        // I wanted a way to group the buttons so they have information of eachother's states, so I asked
        // chatgpt to see if such thing exist then went to this doc: https://www.geeksforgeeks.org/java/jradiobutton-java-swing/
        ButtonGroup GroupMenuViewItem = new ButtonGroup();
        GroupMenuViewItem.add(menuViewItem1);
        GroupMenuViewItem.add(menuViewItem2);

        menuView.add(menuViewItem1);
        menuView.add(menuViewItem2);

        menuView.addActionListener(e -> {
            statusBarMessage("View selected");
        });

        menuFile.addActionListener(e -> {
            statusBarMessage("Files selected");
        });

//        I tried ActionListener on the JMenu but it wouldn't work so I found this: https://stackoverflow.com/a/69775167
        menuFile.addMenuListener(new MenuListener() {
            @Override
            public void menuDeselected(MenuEvent e) {

            }

            @Override
            public void menuCanceled(MenuEvent e) {

            }

            @Override
            public void menuSelected(MenuEvent e) {
                System.out.println("File menu opened");
                statusBarMessage("File menu opened");
            }
        });




        menuViewItem1.addActionListener(e -> {
            statusBarMessage("Photo viewer selected");
        });
        menuViewItem2.addActionListener(e -> {
            statusBarMessage("Browser viewer selected");
        });

        JMenuItem menuFileItem1 = new JMenuItem("Import");
        JMenuItem menuFileItem2 = new JMenuItem("Delete");
        JMenuItem menuFileItem3 = new JMenuItem("Quit");


        menuFileItem2.addActionListener(e -> {
            statusBarMessage("Item Deleted");
        });
        menuFileItem3.addActionListener(e -> {
            statusBarMessage("Quiting the application...");
            System.exit(0);
        });


        menuFileItem1.addActionListener(e -> {
            //From the doc: https://docs.oracle.com/javase/8/docs/api/javax/swing/JFileChooser.html

            statusBarMessage("Browser Opened");

            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Image Files", "jpg", "jpeg", "png");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(getParent());
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    BufferedImage image = ImageIO.read(chooser.getSelectedFile());

                    photoComponent = new PhotoComponent();
                    photoComponent.setPhoto(image);

                    Component center = ((BorderLayout)getContentPane().getLayout()).getLayoutComponent(BorderLayout.CENTER);
                    if (center instanceof JScrollPane) {
                        ((JScrollPane)center).setViewportView(photoComponent);
                    }

                    statusBarMessage("Loaded: " + chooser.getSelectedFile().getName());
                } catch (IOException ex) {
                    statusBarMessage("Error loading image: " + ex.getMessage());
                }
            }
        });
        menuFileItem2.addActionListener(e -> {

            if (photoComponent != null) {
                // Dialogue to confirm
                int result = JOptionPane.showConfirmDialog(this,
                        "Delete the current photo and all annotations?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION) {
                    photoComponent = null;

                    scrollPane.setViewportView(emptyPanel);

                    statusBarMessage("Photo deleted");
                }
            }
        });



        ButtonGroup GroupMenuFileItem = new ButtonGroup();
        GroupMenuFileItem.add(menuFileItem1);
        GroupMenuFileItem.add(menuFileItem2);
        GroupMenuFileItem.add(menuFileItem3);


        menuFile.add(menuFileItem1);
        menuFile.add(menuFileItem2);
        menuFile.add(menuFileItem3);

        menuBar.add(menuFile);
        menuBar.add(menuView);
        menuBar.add(colorChooserLabel);
        menuBar.add(colorChooser);

        setJMenuBar(menuBar);

        add(mainPanel, BorderLayout.CENTER);


    }

}