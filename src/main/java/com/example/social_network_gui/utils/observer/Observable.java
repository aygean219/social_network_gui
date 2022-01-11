package com.example.social_network_gui.utils.observer;

import com.example.social_network_gui.utils.events.Eventt;

public interface Observable<E extends Eventt> {
    void addObserver(Observer<E> e);
    void removeObserver(Observer<E> e);
    void notifyObservers(E t);
}

