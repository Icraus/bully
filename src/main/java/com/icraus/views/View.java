package com.icraus.views;

import com.icraus.jprocess.JProcess;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.NumberFormat;

public class View extends JFrame {
    private final JFormattedTextField timeoutTextField;
    private final JButton okButton;
    private final JButton cancelButton;
    private Controller controller = Controller.getController();
    private int timeout = 3000;

    public static JFormattedTextField createIntTextField(){
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        return new JFormattedTextField(formatter);
    }
    View() {
        setTitle("Process");
        setVisible(true);
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));
        panel.add(new ProcessComponent(controller.getMainProcess(), this));
        for (JProcess p: controller.getMainProcess().getPeers()){
            panel.add(new ProcessComponent(p, this));
        }
        panel.setSize(300, 300);
        BorderLayout layout = new BorderLayout();
        setLayout(layout);
        add(panel, BorderLayout.CENTER);
        okButton = new JButton("Ok");
        cancelButton = new JButton("Start Election");
        JPanel buttonPanel = new JPanel();
        timeoutTextField = createIntTextField();
        timeoutTextField.setValue(timeout);
        buttonPanel.add(timeoutTextField);
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
        setSize(500, 500);
        okButton.addActionListener(e -> {
            setTimeout ((Integer)timeoutTextField.getValue());
        });
        cancelButton.addActionListener(e ->{
            controller.getMainProcess().electCoordinator(getTimeout());
            repaint();
        });
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
