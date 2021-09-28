package com.icraus.views;

import com.icraus.jprocess.JProcess;
import com.icraus.utils.Message;

public class Controller {
    private JProcess mainProcess;
    private static Controller controller = new Controller();
    private int electionWaitTime = 3000;
    public static Controller getController(){
        return controller;
    }
    protected Controller() {
        JProcess p = new JProcess(0);
        JProcess p2 = new JProcess(2);
        JProcess p3 = new JProcess(3);
        addNewProcess(new JProcess(4));
        addNewProcess(new JProcess(5));
        addNewProcess(new JProcess(6));
        JProcess p16 = new JProcess(20);
        p3.setInitElectEvent(pr ->{
            try{
                Thread.sleep(10000);
                return new Message(pr, Message.VICTORY);
            }catch (Exception e){
                return new Message(pr, Message.FAILED_ELECTION);
            }
        });
        JProcess p18 = new JProcess(18);
        p18.setInitElectEvent(pr ->{
            try{
                Thread.sleep(2000);
                return new Message(pr, Message.VICTORY);
            }catch (Exception e){
                return new Message(pr, Message.FAILED_ELECTION);
            }
        });
        addNewProcess(p);
        addNewProcess(p2);
        addNewProcess(p3);
        addNewProcess(p18);
        p.electCoordinator(4000);
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
}
