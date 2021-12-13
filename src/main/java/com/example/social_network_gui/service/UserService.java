package com.example.social_network_gui.service;

import com.example.social_network_gui.domain.User;
import com.example.social_network_gui.repository.Repository;
import com.example.social_network_gui.validators.FriendshipValidator;
import com.example.social_network_gui.validators.Validator;

import java.util.ArrayList;
import java.util.List;

public class UserService {
    private final Repository<Long, User> repositoryUser;
    private Long FreeId;
    private Validator<User> validator;

    public UserService(Repository<Long, User> repositoryUser,Validator<User> validator) {
        this.repositoryUser = repositoryUser;
        FreeId = 0L;
        this.validator = validator;
    }

    /**
     * Set the id used for saving a user with the first id possible
     */
    private void checkId(){
        FreeId = 0L;
        int nr = 0;
        for(User user: repositoryUser.findAll()) {
            FreeId++;
            nr++;
            if (!FreeId.equals(user.getId())) {
                break;
            }

        }
        if( nr == repositoryUser.findAll().size())
                FreeId++;
    }

    /**
     * Create an user with the parameters and save it
     * @param firstName - The first name of the user
     * @param lastName - The last name of the user
     * @param date - The birthday of the user
     * @param gender - The gender of the user
     * @return the user saved
     */
    public void addUser(String firstName,String lastName,String date,String gender,String email,String password){
        User user = new User(firstName,lastName,date,gender,email,password);
        validator.validate(user);
        checkId();
        user.setId(FreeId);
        repositoryUser.save(user);
    }

    /**
     * @return all the users
     * */
    public ArrayList<User> getAll(){
        return repositoryUser.findAll();
    }

    /**
     * Deletes the user with the provided id
     * @param id1 -id of the user to be deleted
     */
    public void deleteUser(String id1){
        Long id= FriendshipValidator.validateId(id1);
        repositoryUser.delete(id);
        FreeId = 0L;
    }

    public User getUser( Long id){
        List<User> list = repositoryUser.findAll();
        for(User u: list){
            if(u.getId() == id){
                return u;
            }
        }
        return null;
    }

}
