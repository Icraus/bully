package com.icraus.views;

import com.icraus.jprocess.JProcess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class View extends JFrame {
    private Controller controller = Controller.getController();
    View() {
        setTitle("Process");
        setVisible(true);
        setLayout(new GridLayout(3, 2));
        add(new ProcessComponent(controller.getMainProcess(), this));
        for (JProcess p: controller.getMainProcess().getPeers()){
            add(new ProcessComponent(p, this));
        }
        setSize(300, 300);
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }
}
