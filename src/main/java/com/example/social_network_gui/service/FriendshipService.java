package com.example.social_network_gui.service;

import com.example.social_network_gui.domain.Friendship;
import com.example.social_network_gui.domain.Tuple;
import com.example.social_network_gui.domain.User;
import com.example.social_network_gui.repository.Repository;
import com.example.social_network_gui.validators.Validator;

import java.util.ArrayList;
import java.util.Optional;

public class FriendshipService {
    private final Repository<Tuple<User,User>, Friendship> repo;
    private Validator<Friendship> validator;

    public FriendshipService(Repository<Tuple<User, User>, Friendship> repo,Validator<Friendship> validator) {
        this.repo = repo;
        this.validator = validator;
    }

    /**
     * @return all the friendhips
     * */
    public ArrayList<Friendship> getAll(){
        return repo.findAll();
    }

    public Optional<Friendship> getOne(Tuple<User,User> id){
        return repo.findOne(id);
    }

    /**
     * Add a friendship with the specified id
     * @param id1 - id of the first user
     * @param id2 - id of the second user
     */
    public void addFriendhip(Friendship friendship){
        validator.validate(friendship);
        repo.save(friendship);
    }

    /**
     * Delete a friendship that have the specified ids
     * @param id1 - id of the first user
     * @param id2 - id of the second user
     */
    public void deleteFriendShip(Tuple<User,User> tuple){
        repo.delete(tuple);
    }

}
