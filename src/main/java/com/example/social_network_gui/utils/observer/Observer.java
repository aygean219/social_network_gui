package com.example.social_network_gui.utils.observer;

import com.example.social_network_gui.utils.events.Eventt;

public interface Observer<E extends Eventt> {
    void update(E e);
}

