package com.example.social_network_gui.repository;

import com.example.social_network_gui.domain.Chat;
import com.example.social_network_gui.domain.Entity;
import com.example.social_network_gui.domain.User;
import com.example.social_network_gui.validators.ValidationException;

import java.util.ArrayList;
import java.util.Optional;

public interface Repository<ID, E extends Entity<ID>> {
    /**
     * @param id -the id of the entity to be returned
     *           id must not be null
     * @return the entity with the specified id
     * or null - if there is no entity with the given id
     * @throws IllegalArgumentException if id is null.
     */
    Optional<E> findOne(ID id);

    /**
     * @return all entities
     */
    ArrayList<E> findAll();

    /**
     * @param entity entity must be not null
     * @return null- if the given entity is saved
     * otherwise returns the entity (id already exists)
     * @throws ValidationException      if the entity is not valid
     * @throws IllegalArgumentException if the given entity is null.     *
     */
    Optional<E> save(E entity);


    /**
     * removes the entity with the specified id
     *
     * @param id id must be not null
     * @return the removed entity or null if there is no entity with the given id
     * @throws IllegalArgumentException if the given id is null.
     */
    Optional<E> delete(ID id);

    /**
     * @param entity entity must not be null
     * @return null - if the entity is updated,
     * otherwise  returns the entity  - (e.g id does not exist).
     * @throws IllegalArgumentException if the given entity is null.
     * @throws ValidationException      if the entity is not valid.
     */
    Optional<E> update(E entity);
    default Optional<E> findUser(String email){
        throw new UnsupportedOperationException();
    }

    default Optional<E> findloggedUser(String email,String password){
        throw new UnsupportedOperationException();
    }

    default ArrayList<E> findRoles(User user){
        throw new UnsupportedOperationException();
    }
    default ArrayList<E> findMessages(Long id1,Long id2){
        throw new UnsupportedOperationException();
    }

    default ArrayList<Chat> findChats(Long id){
        throw new UnsupportedOperationException();
    }
}
