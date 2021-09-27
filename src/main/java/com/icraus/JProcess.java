package com.icraus;

import com.icraus.utils.ObservableProcess;
import com.icraus.utils.ObservableValue;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class JProcess implements ObservableProcess, ObserverProcess {
    public static final int RUNNING = 0;
    public static final int STOPPED = 1;
    public static final int FAILURE = 2;

    @Override
    public int hashCode() {
        return Objects.hash(getPid());
    }

    private long pid;
    private List<JProcess> peers = new ArrayList<>(); // We could have used singleton to manage the peers addition
    private ObservableValue<Integer> state = new ObservableValue<>(RUNNING);
    private ObservableValue<JProcess> coordinator = new ObservableValue<>(null);
    private CallableEvent<JProcess, Future<Message>> electEvent;
    private CallableEvent<JProcess, Message> initElectEvent;

    public JProcess(long pid) {
        this.setPid(pid);
        this.setElectEvent((p)-> CompletableFuture.completedFuture(new Message(p, Message.VICTORY)) );
        this.setInitElectEvent((p)-> {
            if(this.getState().getValue() == JProcess.RUNNING){
                return new Message(this, Message.START_ELECT);
            }
            return new Message(this, Message.FAILED_ELECTION);
        });
        this.coordinator.addListener(((oldValue, newValue) -> {
            for(JProcess p: this.getPeers()){
                if(!p.getCoordinator().getValue().equals(newValue)){
                    p.coordinator.setValue(newValue);
                }
            }
        }));
    }

    public JProcess(int pid, int state) {
        this(pid);
        this.setState(state);
    }

    public List<JProcess> getPeers() {
        return Collections.unmodifiableList(peers);
    }

    public void setState(int state) {
        this.state.setValue(state);
    }

    public ObservableValue<Integer> getState() {
        return state;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public JProcess electCoordinator(int timeout) {
        List<JProcess> jProcessList = getPeers().parallelStream().filter(p -> p.getPid() > this.getPid() && p.getState().getValue() == RUNNING).sorted(Comparator.comparingLong(JProcess::getPid)).collect(Collectors.toList());
        if(jProcessList.size() == 0){
            return markAsCoordinator();
        }
        Future<Message> electionResult = null;
        for(JProcess process : jProcessList){
            Message initElectMessage = process.initElect();
            if(initElectMessage.getMessage() == Message.START_ELECT){
                electionResult = process.elect();
                break;
            }
        }
        if(electionResult == null){
            return markAsCoordinator();
        }
        Message m = null;
        try {
            m = electionResult.get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            Logger.getAnonymousLogger().warning("Time out before Finishing.");
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
        return initElectEvent.execute(this);
    }

    public Future<Message> elect() {
        return electEvent.execute(this);
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

    public ObservableValue<JProcess> getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(JProcess coordinator) {
        this.coordinator.setValue(coordinator);
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

    public CallableEvent<JProcess, Future<Message>> getElectEvent() {
        return electEvent;
    }

    public void setElectEvent(CallableEvent<JProcess, Future<Message>> electEvent) {
        this.electEvent = electEvent;
    }

    public void setInitElectEvent(CallableEvent<JProcess, Message> initElectEvent) {
        this.initElectEvent = initElectEvent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JProcess process = (JProcess) o;
        return getPid() == process.getPid();
    }

    @Override
    public String toString() {
        return "JProcess{" +
                "pid=" + pid +
                ", state=" + state.getValue() +
                ", coordinator=" + coordinator.getValue().pid +
                '}';
    }
}
