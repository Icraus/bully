package com.icraus.views;

import com.icraus.jprocess.JProcess;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.NumberFormat;

public class View extends JFrame {
    private final JFormattedTextField timeoutTextField;
    private final JButton okButton;
    private final JButton cancelButton;
    private final JButton createProcessButton;
    private final JPanel mainPanel;
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
    public View() {
        setTitle("Process");
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(3, 2));
        mainPanel.setSize(300, 300);
        BorderLayout layout = new BorderLayout();
        setLayout(layout);
        add(mainPanel, BorderLayout.CENTER);
        okButton = new JButton("Ok");
        cancelButton = new JButton("Start Election");
        JPanel buttonPanel = new JPanel();
        timeoutTextField = createIntTextField();
        timeoutTextField.setValue(timeout);
        buttonPanel.add(timeoutTextField);
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        createProcessButton = new JButton("Create a new Process");
        buttonPanel.add(createProcessButton);
        add(buttonPanel, BorderLayout.SOUTH);
        setSize(500, 500);
        okButton.addActionListener(e -> {
            setTimeout ((Integer)timeoutTextField.getValue());
        });
        cancelButton.addActionListener(e ->{
            controller.getMainProcess().electCoordinator(getTimeout());
            repaint();
        });
        createProcessButton.addActionListener(e -> {
            String pidValue = (String)JOptionPane.showInputDialog(
                    this,
                    "Set Process ID:",
                    "0");
            try{
                Integer value = Integer.parseInt(pidValue);
                JProcess process = controller.createProcess(value, getTimeout());
                controller.addNewProcess(process);
                drawProcesses();

            }catch (Exception ex){
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid PID",
                        "Error.",
                        JOptionPane.ERROR_MESSAGE);
            }

        });
    }
    public void drawProcesses(){
        mainPanel.removeAll();
        mainPanel.add(new ProcessComponent(controller.getMainProcess(), this));
        for (JProcess p: controller.getMainProcess().getPeers()){
            mainPanel.add(new ProcessComponent(p, this));
        }

        repaint();
        revalidate();
    }
    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
