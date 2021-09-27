package com.icraus;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.instrument.UnmodifiableClassException;


class ProcessTest {
    JProcess createProcess(int id){
        return new JProcess(id);
    }
    private JProcess createProcess(int i, int state) {
        return new JProcess(i, state);
    }

    @Test
    public void testGetCoordinator(){
        JProcess p1 = createProcess(5);
        JProcess p2 = createProcess(1);
        JProcess p3 = createProcess(2, JProcess.FAILURE);
        JProcess p4 = createProcess(3);
        JProcess p5 = createProcess(18, JProcess.RUNNING);
        p1.addPeer(p2);
        p1.addPeer(p3);
        p1.addPeer(p4);
        p1.addPeer(p5);
        p5.setState(JProcess.FAILURE);
        JProcess result = p1.electCoordinator(5);
        Assertions.assertEquals(p1.getPid(), result.getPid());
        Assertions.assertNotNull(result.getCoordinator());
        Assertions.assertEquals(p1.getPid(), result.getCoordinator().getValue().getPid());
        Assertions.assertEquals(p1.getPid(), p4.getCoordinator().getValue().getPid());
    }
    @Test
    public void testAddPeers(){
        JProcess p1 = createProcess(1);
        JProcess p2 = createProcess(2, JProcess.FAILURE);
        JProcess p3 = createProcess(3);
        p1.addPeer(p2);
        p2.addPeer(p1);
        Assertions.assertEquals(1, p2.getPeers().size());
        Assertions.assertEquals(1, p2.getPeers().size());
        p2.addPeer(p3);
        Assertions.assertEquals(2, p2.getPeers().size());
        Assertions.assertEquals(2, p1.getPeers().size());
    }
    @Test
    public void testWillThrowExceptionOnEditList(){
        Assertions.assertThrows(UnsupportedOperationException.class, ()->{
            JProcess p1 = createProcess(1);
            JProcess p2 = createProcess(2, JProcess.FAILURE);
            p1.getPeers().add(p2);
        });

    }
    @Test
    public void testGetCoordinatorLowerProcessId(){
        JProcess p1 = createProcess(1);
        JProcess p2 = createProcess(2, JProcess.STOPPED);
        JProcess p3 = createProcess(3);
        JProcess p5 = createProcess(5);
        JProcess p18 = createProcess(18, JProcess.RUNNING);
        p2.addPeer(p1);
        p2.addPeer(p5);
        p2.addPeer(p3);
        p2.addPeer(p18);
        p18.setState(JProcess.FAILURE);
        JProcess result = p2.electCoordinator(5);
        Assertions.assertEquals(p5.getPid(), result.getPid());
        Assertions.assertNotNull(result.getCoordinator());
        Assertions.assertEquals(p5.getPid(), result.getCoordinator().getValue().getPid());
        Assertions.assertEquals(p5.getPid(), p3.getCoordinator().getValue().getPid());
    }

}