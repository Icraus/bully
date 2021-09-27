package com.icraus.utils;

import com.icraus.utils.ObservableProcess;

public interface ObserverProcess {
    void accepts(ObservableProcess observableProcess, Object message);
}
