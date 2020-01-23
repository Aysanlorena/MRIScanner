package reading.wp017668.MRIReader;

import com.ericbarnhill.niftijio.FourDimensionalArray;
import com.ericbarnhill.niftijio.NiftiVolume;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Frame extends JFrame {

    private Dimension screenSize;
    private int width;
    private int height;
    private int currentX, currentY, currentZ;
    private int labelSize, labelStartX, labelStartY;
    private JLayeredPane pane;
    private JMenuBar menu;
    private JMenu file, help;
    private JMenuItem open, export, about;
    private JLabel axial, sagittal, coronal;
    private BufferedImage[] axialImage, sagittalImage, coronalImage;
    private InfoBar ib;
    private boolean niftiSet;
    private final int dimention = 0;
    FourDimensionalArray data;
    NiftiVolume niftiVolume;

    public Frame() {
        niftiSet = false;
        currentX = 0;
        currentY = 0;
        currentZ = 0;
        pane = new JLayeredPane();
        screenSize = Toolkit.getDefaultToolkit().getScreenSize(); //Retrieving screen size
        width = (((int)screenSize.getWidth())/5)*3; //Setting width to 3/5 of the screen
        height = (width/5)*4; //Setting height to be 4/5 of the width
        ImageIcon icon = new ImageIcon("res/icon.png");
        setIconImage(icon.getImage());
        setTitle("MRI Reader");
        setSize(width, height);//setting size of frame
        setLocationRelativeTo(null);//setting frame to be in centre of screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //setting all threads to close on JFrame close
        setContentPane(pane);
        setBackground();
        setGUI();
        setVisible(true);
        checkIntegerThread();
    }

    private void setBackground() {
        JLabel bg = new JLabel();
        bg.setBackground(Color.BLACK);
        bg.setOpaque(true);
        bg.setBounds(0, 0, screenSize.width, screenSize.height);
        pane.add(bg, new Integer(-10));
    }

    private void setGUI() {
        addMenu();
        setDefaultImages();
        ib = new InfoBar(width, height/13);
        ib.addItems(this);
    }

    private void addMenu() {
        menu = new JMenuBar();
        file = new JMenu("File");
        help = new JMenu("Help");
        open = new JMenuItem(new AbstractAction("Open") {
            public void actionPerformed(ActionEvent e) {
                openPressed();
            }
        });
        export = new JMenuItem(new AbstractAction("Export Images") {
            public void actionPerformed(ActionEvent e) {
                exportPressed();
            }
        });
        about = new JMenuItem(new AbstractAction("About") {
            public void actionPerformed(ActionEvent e) {
                aboutPressed();
            }
        });
        menu.add(file);
        menu.add(help);
        file.add(open);
        file.addSeparator();
        file.add(export);
        help.add(about);
        setJMenuBar(menu);
    }

    private void setDefaultImages() {
        axial = new JLabel();
        sagittal = new JLabel();
        coronal = new JLabel();
        labelSize = (height/2) - (height/10);
        labelStartX = (width/2) - labelSize;
        labelStartY = height/10;
        axial.setLocation(labelStartX, labelStartY);
        sagittal.setLocation(labelStartX+labelSize, labelStartY);
        coronal.setLocation(labelStartX, labelStartY+labelSize);
        axial.setSize(labelSize, labelSize);
        sagittal.setSize(labelSize, labelSize);
        coronal.setSize(labelSize, labelSize);

        /**TODO: remove
        axial.setOpaque(true);
        sagittal.setOpaque(true);
        coronal.setOpaque(true);
        axial.setBackground(Color.BLACK);
        sagittal.setBackground(Color.BLUE);
        coronal.setBackground(Color.RED);
        **/

        pane.add(axial, new Integer(0));
        pane.add(sagittal, new Integer(1));
        pane.add(coronal, new Integer(2));

    }

    private void openNiftiFiles() {
        data = niftiVolume.data;
        axialImage = new BufferedImage[data.sizeY()];
        sagittalImage = new BufferedImage[data.sizeZ()];
        coronalImage = new BufferedImage[data.sizeX()];
        fillAxial();
        fillSagittal();
        fillCoronal();
        niftiSet = true;
    }

    private void fillSagittal() {
        for(int z = 0; z < data.sizeZ(); z++) {
            sagittalImage[z] = new BufferedImage(data.sizeX(), data.sizeY(),
                    BufferedImage.TYPE_INT_RGB);
            for(int x = 0; x < data.sizeX(); x++) {
                for(int y = 0; y < data.sizeY(); y++) {
                    sagittalImage[z].setRGB(x, data.sizeY()-1-y, convertToPixel(data.get(x, y, z, dimention)));
                }
            }
            sagittalImage[z] = resize(sagittalImage[z], labelSize, labelSize);
        }
    }

    private void fillCoronal() {
        for(int x = 0; x < data.sizeX(); x++) {
            coronalImage[x] = new BufferedImage(data.sizeZ(), data.sizeY(), BufferedImage.TYPE_INT_RGB);
            for(int z = 0; z < data.sizeZ(); z++) {
                for(int y = 0; y < data.sizeY(); y++) {
                    coronalImage[x].setRGB(z, data.sizeY()-1-y, convertToPixel(data.get(x, y, z, dimention)));
                }
            }
            coronalImage[x] = resize(coronalImage[x], labelSize, labelSize);
        }
    }

    private void fillAxial() {
        for(int y = 0; y < data.sizeY(); y++) {
            axialImage[y] = new BufferedImage(data.sizeX(), data.sizeZ(), BufferedImage.TYPE_INT_RGB);
            for(int z = 0; z < data.sizeZ(); z++) {
                for(int x = 0; x < data.sizeX(); x++) {
                    axialImage[y].setRGB(x, z, convertToPixel(data.get(x, y,z, dimention)));
                }
            }
            axialImage[y] = resize(axialImage[y], labelSize, labelSize);
        }
    }

    private int convertToPixel(double value) {
        int valueSquared = (int)(Math.pow(value, 0.7));
        if(valueSquared>255) valueSquared = 255;
        Color c = new Color(valueSquared, valueSquared, valueSquared);
        return c.getRGB();
    }

    private void openPressed() {
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fc.getSelectedFile();
                niftiVolume = niftiVolume.read(file.getName());
                openNiftiFiles();
                setNiftFiles();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setNiftFiles() {
        axial.setIcon(new ImageIcon(axialImage[currentY]));
        sagittal.setIcon(new ImageIcon(sagittalImage[currentZ]));
        coronal.setIcon(new ImageIcon(coronalImage[currentX]));
    }

    private void exportPressed() {
        JFrame f = new JFrame("Export Image");
        f.setSize(400, 150);
        f.setLocationRelativeTo(null);
        f.setContentPane(new JLayeredPane());
        JLabel bg = new JLabel();
        bg.setBounds(0, 0, 500, 200);
        bg.setBackground(Color.LIGHT_GRAY);
        bg.setOpaque(true);
        f.getContentPane().add(bg, new Integer(0));
        JLabel label = new JLabel("Please select which images you would like to save");
        label.setBounds(10, 10, 300, 50);
        f.getContentPane().add(label, new Integer(1));
        JCheckBox ax = new JCheckBox("Axial", true);
        JCheckBox sag = new JCheckBox("Saggital", true);
        JCheckBox cor = new JCheckBox("Coronal", true);
        ax.setBounds(10, 50, 100, 50);
        ax.setBackground(Color.LIGHT_GRAY);
        sag.setBounds(100, 50, 100, 50);
        sag.setBackground(Color.LIGHT_GRAY);
        cor.setBounds(200, 50, 100, 50);
        cor.setBackground(Color.LIGHT_GRAY);
        f.getContentPane().add(ax, new Integer(1));
        f.getContentPane().add(sag, new Integer(1));
        f.getContentPane().add(cor, new Integer(1));
        JButton save = new JButton("Export");
        save.setBounds(300, 60, 80 ,40);
        f.getContentPane().add(save, new Integer(1));
        f.setVisible(true);

        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fc.showSaveDialog(null);
                File file = fc.getSelectedFile();
                System.out.println(file.getPath());
                try {
                    File fileax = new File(file.getPath() + "\\axial.png");
                    File filesag = new File(file.getPath() + "\\sagittal.png");
                    File filecor = new File(file.getPath() + "\\coronal.png");
                    System.out.println(fileax.getPath());
                    if(ax.isSelected()) ImageIO.write(axialImage[currentY], "png", fileax);
                    if(sag.isSelected()) ImageIO.write(sagittalImage[currentZ], "png", filesag);
                    if(cor.isSelected()) ImageIO.write(coronalImage[currentX], "png", filecor);
                } catch(IOException ex) { ex.printStackTrace(); }
            }
        } );

    }

    private void aboutPressed() {

    }

    private void checkIntegerThread() {
        Thread t = new Thread() {
            @Override
            public void start() {
                while(true) {
                    ib.checkForStrings();
                    currentX = ib.getxVal();
                    currentY = ib.getyVal();
                    currentZ = ib.getzVal();
                    if(niftiSet)
                       setNiftFiles();
                    try {
                        Thread.sleep(100);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }

    private static BufferedImage resize(BufferedImage img, int height, int width) {
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }

}
