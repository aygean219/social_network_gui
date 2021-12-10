package com.example.social_network_gui.service;

import com.example.social_network_gui.domain.Friendship;
import com.example.social_network_gui.domain.Tuple;
import com.example.social_network_gui.domain.User;
import com.example.social_network_gui.repository.Repository;
import com.example.social_network_gui.repository.memory.RepositoryException;
import com.example.social_network_gui.utils.Graph;
import com.example.social_network_gui.validators.FriendshipValidator;
import com.example.social_network_gui.validators.Validator;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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
