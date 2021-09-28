package com.icraus.utils;

import com.icraus.jprocess.JProcess;

public class Message {
    public static final int VICTORY = 5;
    public static final int REQUEST_DATA = 6;
    public static final int FAILED_ELECTION = 7;
    public static final int REQUEST_ELECTION = 8;
    private JProcess process;
    private int message;

    public Message(JProcess jProcess, int startElect) {
        this.setProcess(jProcess);
        this.setMessage(startElect);
    }

    private void setMessage(int startElect) {
        this.message = startElect;
    }

    public int getMessage() {
        return this.message;
    }

    public void setProcess(JProcess jProcess) {
        this.process = jProcess;
    }

    public JProcess getProcess() {
        return this.process;
    }
}
