package com.icraus.views;

import com.icraus.jprocess.JProcess;
import com.icraus.utils.Message;

public class Controller {
    private JProcess mainProcess;
    private static Controller controller = new Controller();
    private int electionWaitTime = 3000;
    static int PID_COUNTER = 0;

    public static Controller getController(){
        return controller;
    }
    protected Controller() {
    }

    public JProcess getMainProcess() {
        return mainProcess;
    }

    public void setMainProcess(JProcess mainProcess) {
        this.mainProcess = mainProcess;
    }

    public void addNewProcess(JProcess process){
        if(mainProcess == null){
            setMainProcess(process);
        }else {
            mainProcess.addPeer(process);
        }
    }

    public int getElectionWaitTime() {
        return electionWaitTime;
    }

    public void setElectionWaitTime(int electionWaitTime) {
        this.electionWaitTime = electionWaitTime;
    }
    public JProcess createProcess(){

        return createProcess(PID_COUNTER++, electionWaitTime);
    }

    public JProcess createProcess(long pid, int waitTime){
        JProcess process = new JProcess(pid);
        process.setInitElectEvent(p -> {
            try {
                Thread.sleep(waitTime);
                return new Message(p, Message.VICTORY);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
                return new Message(p, Message.FAILED_ELECTION);
            }
        });
        return process;
    }

}
