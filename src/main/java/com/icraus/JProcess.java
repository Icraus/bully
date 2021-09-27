package com.icraus;

import com.icraus.utils.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class JProcess implements ObservableProcess, ObserverProcess {
    public static final int RUNNING = 0;
    public static final int STOPPED = 1;
    public static final int FAILURE = 2;
    private static ExecutorService threadPool = Executors.newCachedThreadPool();

    @Override
    public int hashCode() {
        return Objects.hash(getPid());
    }

    private long pid;
    private List<JProcess> peers = new ArrayList<>(); // We could have used singleton to manage the peers addition
    private ObservableValue<Integer> state = new ObservableValue<>(RUNNING);
    private ObservableValue<JProcess> coordinator = new ObservableValue<>(null);
    private CallableEvent<JProcess, Message> initElectEvent;

    public JProcess(long pid) {
        this.setPid(pid);
        this.setInitElectEvent((p)-> {
            if(this.getState().getValue() == JProcess.RUNNING){
                return new Message(p, Message.VICTORY);
            }
            return new Message(p, Message.FAILED_ELECTION);
        });
        this.getCoordinator().addListener(((oldValue, newValue) -> {
            getPeers().forEach(p ->{
                p.setCoordinator(newValue);
            });
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
        List<JProcess> jProcessList = getPeers().stream()
                .filter(p -> p.getPid() > this.getPid() && p.getState().getValue() == RUNNING)
                .sorted(Comparator.comparingLong(JProcess::getPid)
                        .reversed())
                .collect(Collectors.toList());
        if(jProcessList.size() == 0){
            return markAsCoordinator();
        }
        Message electionResult = getElectionResult(jProcessList, timeout);
        if(electionResult == null){
            return markAsCoordinator();
        }

        if(electionResult.getMessage() == Message.VICTORY){
            electionResult.getProcess().markAsCoordinator();
            this.setCoordinator(electionResult.getProcess());
            return electionResult.getProcess();
        }
        return markAsCoordinator();
    }

    private Message getElectionResult(List<JProcess> jProcessList, long timeout) {
        List<Future<Message>> futureList = jProcessList.stream().map(p -> threadPool.submit(() -> p.initElect())).collect(Collectors.toList());
        Message electionResult = futureList.parallelStream().map(f -> {
            try {
                return f.get(timeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                Logger.getAnonymousLogger().warning("Interruption Occurred Here.");
                e.printStackTrace();
                return new Message(null, Message.FAILED_ELECTION);
            }
        }).filter(m -> m != null && m.getMessage() == Message.VICTORY)
                .sorted(Comparator.comparingLong((Message value) -> value.getProcess().getPid()).reversed())
                .findFirst()
                .orElse(new Message(this, Message.VICTORY));
        return electionResult;
    }

    private JProcess markAsCoordinator() {
        this.setCoordinator(this);
        this.sendMessage(new Message(this, Message.VICTORY));
        return this;
    }

    public Message initElect(){
        return initElectEvent.execute(this);
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
    public void sendMessage(Message message) {
        update(message);
    }

    @Override
    public void update(Message message) {
        getPeers().forEach(p -> {
            p.accepts(this, message);
        });
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
        Optional <JProcess> result = Optional.ofNullable(coordinator.getValue());
        return "JProcess{" +
                "pid=" + pid +
                ", state=" + state.getValue() +
                ", coordinator=" + (result.isPresent() ? result.get().getPid() : "") +
                '}';
    }
}
