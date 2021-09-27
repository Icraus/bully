package com.icraus.utils;

import com.icraus.ObserverProcess;

import java.util.ArrayList;
import java.util.List;

public interface ObservableProcess {

    public void sendMessage(int message);
    public void update(Object message);

}
