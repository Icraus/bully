package com.icraus.views;

import com.icraus.jprocess.JProcess;
import com.icraus.utils.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProcessDialog  extends JDialog {
    private final JButton okButton;
    private final JButton cancelButton;
    private final TextField pidTextField;
    private final TextField timeoutTextField;
    private final JComboBox statesComboBox;
    private final JComboBox messageComboBox;
    private JProcess currentProcess;
    final String[] states = {"Running", "Stopped", "Failed", "New"};
    private final String[] messages = {"VICTORY", "REQUEST_DATA", "FAILED_ELECTION", "REQUEST_ELECTION"};
    static int PID_COUNTER = 0;
    ProcessDialog(JProcess process){
        currentProcess = process;
        JPanel panel = new JPanel();
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        panel.setLayout(new GridLayout(4, 2));
        panel.add(new JLabel("PID"));
        pidTextField = new TextField("" + currentProcess.getPid());
        panel.add(pidTextField);
        panel.add(new JLabel("Timeout"));
        timeoutTextField = new TextField("1000");
        panel.add(timeoutTextField);
        panel.add(new JLabel("State"));
        statesComboBox = new JComboBox(states);
        statesComboBox.setSelectedItem(states[currentProcess.getState().getValue()]);
        panel.add(statesComboBox);
        panel.add(new JLabel("Message on Done"));
        messageComboBox = new JComboBox(messages);
        messageComboBox.setSelectedItem(messages[currentProcess.getState().getValue()]);
        panel.add(messageComboBox);
        JPanel buttonPanel = new JPanel();
        okButton = new JButton("Ok");
        cancelButton = new JButton("Cancel");
        buttonPanel.add(okButton);
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentProcess.setState(statesComboBox.getSelectedIndex());
                currentProcess.setInitElectEvent(p -> {
                    try {
                        Thread.sleep(Integer.parseInt(timeoutTextField.getText()));
                        return new Message(p, messageComboBox.getSelectedIndex() % 5);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                        return new Message(p, Message.FAILED_ELECTION);
                    }
                });
                System.out.println("HEllo world");
                setVisible(false);
            }
        });
        buttonPanel.add(cancelButton);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        add(buttonPanel, BorderLayout.SOUTH);
        setSize(200, 200);
    }


}
