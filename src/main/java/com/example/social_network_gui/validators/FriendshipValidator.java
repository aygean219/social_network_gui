package com.example.social_network_gui.validators;

import com.example.social_network_gui.domain.Friendship;

public class FriendshipValidator implements Validator<Friendship>{
    @Override
    public void validate(Friendship entity) throws ValidationException {
        if(entity.getId().getE1().equals(entity.getId().getE2())){
            throw new ValidationException("id of users must be different/");
        }
    }

    public static Long validateId(String id1){
        try {
            return Long.parseLong(id1);
        }catch (NumberFormatException e){
            throw  new ValidationException(id1+ " must be a number/");
        }
    }
}
