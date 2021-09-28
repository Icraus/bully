package com.icraus.views;

import com.icraus.jprocess.JProcess;
import com.icraus.utils.Message;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

public class ProcessDialog  extends JDialog {
    private final JButton okButton;
    private final JButton cancelButton;
    private final TextField pidTextField;
    private final JFormattedTextField timeoutTextField;
    private final JComboBox statesComboBox;
    private final JComboBox messageComboBox;
    private JProcess currentProcess;
    final String[] states = {"Running", "Stopped", "Failed", "New"};
    private final String[] messages = {"VICTORY", "REQUEST_DATA", "FAILED_ELECTION", "REQUEST_ELECTION"};
    ProcessDialog(ProcessComponent process){
        currentProcess = process.getProcess();
        JPanel panel = new JPanel();
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        panel.setLayout(new GridLayout(4, 2));
        panel.add(new JLabel("PID"));
        pidTextField = new TextField("" + currentProcess.getPid());
        panel.add(pidTextField);
        panel.add(new JLabel("Timeout"));
        timeoutTextField = createIntTextField();
        timeoutTextField.setValue(process.getTimeout());
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
        okButton.addActionListener(e -> {
                currentProcess.setState(statesComboBox.getSelectedIndex());
                int timeout = (Integer)timeoutTextField.getValue();
                process.setTimeout(timeout);
                currentProcess.setInitElectEvent(p -> {
                    try {
                        Thread.sleep(process.getTimeout());
                        return new Message(p, messageComboBox.getSelectedIndex() % 5 + 5);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                        return new Message(p, Message.FAILED_ELECTION);
                    }
                });
                setVisible(false);
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
    private JFormattedTextField createIntTextField(){
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        // If you want the value to be committed on each keystroke instead of focus lost
        return new JFormattedTextField(formatter);
    }

}
