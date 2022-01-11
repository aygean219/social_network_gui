package com.example.social_network_gui.service;

import com.example.social_network_gui.domain.Event;
import com.example.social_network_gui.domain.Friendship;
import com.example.social_network_gui.domain.Tuple;
import com.example.social_network_gui.domain.User;
import com.example.social_network_gui.repository.Repository;
import com.example.social_network_gui.repository.database.EventsDatabaseRepository;
import com.example.social_network_gui.repository.memory.RepositoryException;
import com.example.social_network_gui.utils.EventSubscription;
import com.example.social_network_gui.validators.ValidationException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class EventService {
    private final Repository<Long, Event> eventRepository;

    public EventService(Repository<Long, Event> eventRepository) {
        this.eventRepository = eventRepository;
    }


    public Event addEvent(String title, String description, String location, LocalDateTime date) {
        Event event = new Event(title, description, location, date);
        Optional<Event> op = eventRepository.save(event);
        return op.orElse(null);
    }


    public ArrayList<Event> getAllEvents() {
        return eventRepository.findAll();
    }


    public List<Event> getEventsForUser(User user) {
//        return StreamSupport.stream(eventRepository.findAll().spliterator(), false)
//                .filter(x -> x.getUsers()
//                        .entrySet()
//                        .stream()
//                        .anyMatch(y -> (y.getKey().getId().equals(user.getId()) && y.getValue() == (EventSubscription.SUBSCRIBE))))
//                .collect(Collectors.toList());
        ArrayList<Event> all = eventRepository.findAll();
        ArrayList<Event> sorted = new ArrayList<>();
        for (Event e : all) {
            Map<User, EventSubscription> users = e.getUsers();
            for (Map.Entry<User, EventSubscription> entry : users.entrySet()) {
                if (user.getId().equals(entry.getKey().getId()) && entry.getValue().equals(EventSubscription.SUBSCRIBE)) {
                    sorted.add(e);
                }
            }
        }
        return sorted;
    }

    public List<Event> getSuggestedEventsForUser(User user) {
        ArrayList<Event> all = eventRepository.findAll();
        ArrayList<Event> sorted = new ArrayList<>();
        for (Event e : all) {
            sorted.add(e);
            Map<User, EventSubscription> users = e.getUsers();
            for (Map.Entry<User, EventSubscription> entry : users.entrySet()) {
                if (entry.getValue().equals(EventSubscription.SUBSCRIBE) && user.getId().equals(entry.getKey().getId())) {
                    sorted.remove(e);
                }
            }
        }
        return sorted;
    }

    public void subscribeUser(Event event, User user) {
        event.subscribeUser(user);
        ((EventsDatabaseRepository) eventRepository).subscribeUser(user, event);
    }


    public void unsubscribeUser(Event event, User user) {
        event.unsubscribeUser(user);
        ((EventsDatabaseRepository) eventRepository).unsubscribeUser(user, event);
    }


}