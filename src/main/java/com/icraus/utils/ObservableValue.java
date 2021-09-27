package com.icraus.utils;

import java.util.ArrayList;
import java.util.List;

public class ObservableValue<T> {
    private T value;
    private List<ValueObserverListener<T>> listenerList = new ArrayList<>();

    public ObservableValue(T value){
        this.setValue(value);
    }
    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        if(this.value == null){
            this.value = value;
            return;
        }
        if(this.value.equals(value)){
            return;
        }
        T oldValue = this.value;
        this.value = value;
        listenerList.forEach(e -> e.onValueChanged(oldValue, this.value));
    }
    public void addListener(ValueObserverListener<T> listener){
        if(!listenerList.contains(listener)){
            listenerList.add(listener);
        }
    }
}
