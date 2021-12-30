package com.example.social_network_gui.utils.observer;

import com.example.social_network_gui.utils.events.Event;

public interface Observer<E extends Event> {
    void update(E e);
}

