package com.icraus.jprocess;

import com.icraus.utils.Message;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ProcessAdaptor extends JProcess{
    public static ProcessBuilder exec(Class clazz, List<String> jvmArgs, List<String> args) {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java.exe";
        String classpath = System.getProperty("java.class.path");
        String className = clazz.getName();

        List<String> command = new ArrayList<>();
        command.add(javaBin);
        command.addAll(jvmArgs);
        command.add("-cp");
        command.add(classpath);
        command.add(className);
        command.addAll(args);
        return new ProcessBuilder(command);
    }

    Process mainProcess;
    public ProcessAdaptor(long pid, String command, int timeout) {
        super(pid);
        getState().addListener((oldValue, newValue) -> {
            switch (newValue) {
                case JProcess.FAILURE:
                    break;
                case JProcess.STOPPED:
                    stopProcess();
                    break;
                case RUNNING:
                    break;
                case JProcess.NEW:
                    startProcess(command);
                    this.electCoordinator(timeout);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported state" + newValue);
            }
        });
    }

    private void stopProcess() {
        mainProcess.destroyForcibly();
    }

    private void startProcess(String command) {
        try {
            mainProcess = new ProcessBuilder(command).start();
            setState(JProcess.RUNNING);
        } catch (IOException e) {
            setState(JProcess.FAILURE);
        }
    }
}
