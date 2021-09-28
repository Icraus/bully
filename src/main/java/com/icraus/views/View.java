package com.icraus.views;

import com.icraus.jprocess.JProcess;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class View extends JFrame {
    View() {
        setTitle("Process");
        setVisible(true);
        for (int i = 0; i < 15; ++i) {
            Component b = new ProcessComponent(new JProcess(i));
            b.setBounds(i  * 50 + 50, i % 3 * 50 + 50, 50, 50);
            add(b);
        }
        setSize(500, 500);

    }
}
