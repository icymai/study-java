package guidemo;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * A frame that displays a multi-line text, possibly with a background image
 * and with added icon images, in a DrawPanel, along with a variety of controlls.
 */
public class GuiDemo extends JFrame {

    /**
     * Creates a GuiDemo frame and makes it visible.
     */
    public static void main(String[] args) {
        JFrame frame = new GuiDemo();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private final DrawPanel drawPanel;
    private final SimpleFileChooser fileChooser;
    private final TextMenu textMenu;
    private final JCheckBoxMenuItem gradientOverlayCheckbox = new JCheckBoxMenuItem("Gradient Overlay", true);


    /**
     * This constructor creates the frame, sizes it, and centers it horizontally on the screen.
     */
    public GuiDemo() {

        super("Sayings");  // Specifies the string for the title bar of the window.

        // Create and customize the file chooser that is used for file operations.

        fileChooser = new SimpleFileChooser();
        try { // I'd like to use the Desktop folder as the initial folder in the file chooser.
            String userDir = System.getProperty("user.home");
            if (userDir != null) {
                File desktop = new File(userDir, "Desktop");
                if (desktop.isDirectory())
                    fileChooser.setDefaultDirectory(desktop);
            }
        } catch (Exception ignored) {
        }

        JPanel content = new JPanel();  // To hold the content of the window.
        content.setBackground(Color.LIGHT_GRAY);
        content.setLayout(new BorderLayout());
        setContentPane(content);

        // Create the DrawPanel that fills most of the window, and customize it.

        drawPanel = new DrawPanel();
        drawPanel.getTextItem().setText(
        		"Learning is the beginning of wealth.\n"
        		+ "Learning is the beginning of health.\n"
        		+ "Learning is the beginning of spirituality.\n"
        		+ "Searching and learning is where the miracle process all begins."
        );
        drawPanel.getTextItem().setFontSize(20);
        drawPanel.getTextItem().setJustify(TextItem.CENTER);
        drawPanel.setBackgroundImage(Util.getImageResource("resources/images/cloud.jpeg"));
        content.add(drawPanel, BorderLayout.CENTER);

        // Add change background toolbar to the NORTH position of the layout
        BackgroundSupport bkSupport = new BackgroundSupport(drawPanel, fileChooser, gradientOverlayCheckbox);
        content.add(bkSupport.makeToolbar(), BorderLayout.NORTH);

        // Add an icon toolbar to the SOUTH position of the layout

        IconSupport iconSupport = new IconSupport(drawPanel);
        content.add(iconSupport.createToolbar(true), BorderLayout.SOUTH);

        // Create the menu bar and add it to the frame.  The TextMenu is defined by
        // a separate class. The other menus are created in this class.

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(makeFileMenu());
        textMenu = new TextMenu(drawPanel);
        menuBar.add(textMenu);
        JMenu backgroundMenu = new BackgroundSupport(drawPanel, fileChooser, gradientOverlayCheckbox).makeMenu();
        menuBar.add(backgroundMenu);
        JMenu stampersMenu = new IconSupport(drawPanel).createMenu();
        menuBar.add(stampersMenu);
        setJMenuBar(menuBar);

        // Set the size of the window and its position.

        pack();  // Size the window to fit its content.
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - getWidth()) / 2, 50);
    } // end constructor

    /**
     * Create the "File" menu from actions that are defined later in this class.
     */
    private JMenu makeFileMenu() {
        JMenu menu = new JMenu("File");
        menu.add(newPictureAction);
        menu.add(saveImageAction);
        menu.addSeparator();
        menu.add(quitAction);
        return menu;
    }

    /**
     * Create the "Background" menu, using objects of type ChooseBackgroundAction,
     * a class that is defined later in this file.
     */

    private AbstractAction newPictureAction = new AbstractAction("New", Util.iconFromResource("resources/action_icons/fileopen.png")) {
        public void actionPerformed(ActionEvent evt) {
            drawPanel.clear();
            gradientOverlayCheckbox.setSelected(true);
            textMenu.setDefaults();
        }
    };

    private AbstractAction quitAction = new AbstractAction("Quit", Util.iconFromResource("resources/action_icons/exit.png")) {
        public void actionPerformed(ActionEvent evt) {
            System.exit(0);
        }
    };

    private AbstractAction saveImageAction = new AbstractAction("Save Image...", Util.iconFromResource("resources/action_icons/filesave.png")) {
        public void actionPerformed(ActionEvent evt) {
            File f = fileChooser.getOutputFile(drawPanel, "Save File To...", "myimage.jpeg");
            if (f != null) {
                try {
                    BufferedImage img = drawPanel.copyImage();
                    String format;
                    String fileName = f.getName().toLowerCase();
                    if (fileName.endsWith(".png"))
                        format = "PNG";
                    else if (fileName.endsWith(".jpeg") || fileName.endsWith(".jpg"))
                        format = "JPEG";
                    else {
                        JOptionPane.showMessageDialog(drawPanel,
                                "The file name must end with\n.png or .jpeg.");
                        return;
                    }
                    ImageIO.write(img, format, f);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(drawPanel, "Sorry, the image could not be saved.");
                }
            }
        }
    };
}

