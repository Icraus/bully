package com.icraus.views;

import com.icraus.jprocess.JProcess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;

public class ProcessComponent extends JPanel {
    private JProcess process;
    private View currentView;
    private int timeout = 3000;

    ProcessComponent(JProcess process, View v){
        setOpaque(true);
        this.process = process;
        this.process.getState().addListener((oldValue, newValue) -> {
            if(newValue != JProcess.RUNNING && process.isCoordinator()){
                process.electCoordinator(v.getTimeout());
                currentView.repaint();
            }
            repaint();
        });
        currentView = v;
        ProcessComponent currentComponent = this;
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2){
                    SwingUtilities.invokeLater(() ->{
                        ProcessDialog processDialog = new ProcessDialog(currentComponent);
                        processDialog.setVisible(true);
                    });
                }
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

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;
        switch (this.process.getState().getValue()){
            case JProcess.RUNNING:
                g2d.setColor(Color.GREEN);
                break;
            case JProcess.FAILURE:
                g2d.setColor(Color.RED);
                break;
            case JProcess.STOPPED:
                g2d.setColor(Color.LIGHT_GRAY);
                break;
            case JProcess.NEW:
                g2d.setColor(Color.YELLOW);
                break;

        }
        Shape s = new Ellipse2D.Double(0, 0, 50, 50);
        g2d.fill(s);
        g2d.draw(s);
        g2d.setColor(Color.BLACK);
        g2d.drawString(process.getPid() + "", 15, 15);
        if(process.isCoordinator()){
            g2d.drawString("The Coordinator", 20, 30);
        }
    }

    public JProcess getProcess() {
        return process;
    }

    public void setProcess(JProcess process) {
        this.process = process;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
