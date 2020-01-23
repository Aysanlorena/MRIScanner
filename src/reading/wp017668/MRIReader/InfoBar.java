package reading.wp017668.MRIReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class InfoBar extends JLabel {

    private IntegerSelection xSelect, ySelect, zSelect;
    private JLabel xLabel, yLabel, zLabel;
    private int xVal, yVal, zVal, xmax, ymax, zmax;
    private final int baseNumberIntegerSelection = 0, coordsStartX = 50, coordsY = 30, integerSelectionWidth = 60, integerSelectionHeight = 30, breakSize = 3, labelX = 30, labelY = 30;

    public InfoBar(int width, int height) {
        Font font = new Font("SansSerif", Font.BOLD, 20);
        setBounds(0, 0, width, height);
        setOpaque(true);
        setBackground(Color.DARK_GRAY);
        xLabel = new JLabel("X: ");
        yLabel = new JLabel("Y: ");
        zLabel = new JLabel("Z: ");
        xLabel.setBounds(coordsStartX-labelX, coordsY, labelX, labelY);
        yLabel.setBounds(coordsStartX + (int)(integerSelectionWidth*breakSize) - labelX, coordsY, labelX, labelY);
        zLabel.setBounds(coordsStartX + (int)(integerSelectionWidth*breakSize*2)-labelX, coordsY, labelX, labelY);
        xLabel.setFont(font);
        yLabel.setFont(font);
        zLabel.setFont(font);
        xLabel.setForeground(Color.BLACK);
        yLabel.setForeground(Color.BLACK);
        zLabel.setForeground(Color.BLACK);
        xSelect = new IntegerSelection(baseNumberIntegerSelection, coordsStartX, coordsY, integerSelectionWidth, integerSelectionHeight);
        ySelect = new IntegerSelection(baseNumberIntegerSelection, coordsStartX + (int)(integerSelectionWidth*breakSize), coordsY, integerSelectionWidth, integerSelectionHeight);
        zSelect = new IntegerSelection(baseNumberIntegerSelection, coordsStartX + (int)(integerSelectionWidth*breakSize*2), coordsY, integerSelectionWidth, integerSelectionHeight);
        xSelect.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                //Your code here
            }

            @Override
            public void focusLost(FocusEvent e) {
                //if(updateX() > xmax) xSelect.setText(Integer.toString(xmax));
                xVal = updateX();
            }
        });

        ySelect.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                //Your code here
            }

            @Override
            public void focusLost(FocusEvent e) {
                //if(updateY() > ymax) ySelect.setText(Integer.toString(ymax));
                yVal = updateY();
            }
        });

        zSelect.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                //Your code here
            }

            @Override
            public void focusLost(FocusEvent e) {
                //if(updateZ() > zmax) zSelect.setText(Integer.toString(zmax));
                zVal = updateZ();
            }
        });

    }

    public void checkForStrings() {
        xSelect.checkForString();
        ySelect.checkForString();
        zSelect.checkForString();
    }

    public void addItems(JFrame f) {
        f.add(xLabel);
        f.add(yLabel);
        f.add(zLabel);
        xSelect.addItems(f);
        ySelect.addItems(f);
        zSelect.addItems(f);
        f.add(this);
    }

    public int updateX() {
        return Integer.parseInt(xSelect.getText());
    }

    public int updateY() {
        return Integer.parseInt(ySelect.getText());
    }

    public int updateZ() {
        return Integer.parseInt(zSelect.getText());
    }

    public int getxVal() { return xVal; }

    public int getyVal() { return yVal; }

    public int getzVal() { return zVal; }

    public void setMax(int xm, int ym, int zm) {
        xmax = xm;
        ymax = ym;
        zmax = zm;
    }

}
