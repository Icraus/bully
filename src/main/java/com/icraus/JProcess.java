package com.icraus;

import com.icraus.utils.ObservableProcess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class JProcess implements ObservableProcess, ObserverProcess {
    public static final int RUNNING = 0;
    public static final int STOPPED = 1;
    public static final int FAILURE = 2;

    private long pid;
    private List<JProcess> peers = new ArrayList<>(); // We could have used singleton to manage the peers addation
    private int state = RUNNING;
    private JProcess coordinator = null;

    public JProcess(long pid) {
        this.setPid(pid);
    }

    public JProcess(int pid, int state) {
        this.setPid(pid);
        this.setState(state);
    }

    public List<JProcess> getPeers() {
        return Collections.unmodifiableList(peers);
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public JProcess electCoordinator(int timeout) {
        List<JProcess> jProcessList = getPeers().parallelStream().filter(p -> p.getPid() > this.getPid() && p.getState() == RUNNING).collect(Collectors.toList());
        if(jProcessList.size() == 0){
            return markAsCoordinator();
        }
        Future<Message> electionResult = null;
        for(JProcess process : jProcessList){
            Message initElectMessage = process.initElect();
            if(initElectMessage.getMessage() == Message.START_ELECT){
                electionResult = process.elect(timeout);
                break;
            }
        }
        if(electionResult == null){
            return markAsCoordinator();
        }
        Message m = null;
        try {
            m = electionResult.get(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        if(m == null){
            return markAsCoordinator();
        }
        if(m.getMessage() == Message.VICTORY){
            m.getProcess().markAsCoordinator();
            this.setCoordinator(m.getProcess());
            return m.getProcess();
        }
        return markAsCoordinator();
    }

    private JProcess markAsCoordinator() {
        this.setCoordinator(this);
        this.sendMessage(Message.VICTORY);
        return this;
    }

    public Message initElect(){
        if(this.getState() == JProcess.RUNNING){
            return new Message(this, Message.START_ELECT);
        }
        return new Message(this, Message.FAILED_ELECTION);
    }

    public Future<Message> elect(int timeout) {
        return CompletableFuture.completedFuture(new Message(this, Message.VICTORY));
    }

    public int getState() {
        return state;
    }

    @Override
    public void accepts(ObservableProcess observableProcess, Object message) {
        if(message.getClass() == Integer.class){
           int msg = (Integer) message;
           switch (msg){
               case Message.VICTORY:
                   this.setCoordinator((JProcess) observableProcess);

           }
        }
    }

    @Override
    public void sendMessage(int message) {
        update(message);
    }

    @Override
    public void update(Object message) {
        for(JProcess p : getPeers()){
            p.accepts(this, message);
        }
    }

    public JProcess getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(JProcess coordinator) {
        this.coordinator = coordinator;
    }

    public void addPeer(JProcess p) {
        if(p == this){
            return;
        }
        if(this.getPeers().contains(p)){
            return;
        }
        this.peers.add(p);
        for(JProcess peer: this.getPeers()){
            peer.addPeer(p);
        }
    }
}
