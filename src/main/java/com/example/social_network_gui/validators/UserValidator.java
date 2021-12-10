package com.example.social_network_gui.validators;

import com.example.social_network_gui.domain.User;

public class UserValidator implements Validator<User>{

    @Override
    public void validate(User entity) throws ValidationException {
        String firstName = entity.getFirstName();
        String lastName = entity.getLastName();
        String date = entity.getDate();
        String gender = entity.getGender();
        StringBuilder errors = new StringBuilder();

        if(firstName.equals(""))
            errors.append("First name cannot be empty");

        if(firstName.length() <= 1)
            errors.append("First name should have at least two characters/");

        char[] chars= firstName.toCharArray();
        for(char c: chars){
            if(Character.isDigit(c)){
                errors.append("First name cannot contain digits/");
                break;
            }
        }

        if(lastName.equals(""))
        errors.append("Last name cannot be empty");

        if(lastName.length() <= 1)
            errors.append("Last name should have at least two characters/");

        chars= lastName.toCharArray();
        for(char c: chars){
            if(Character.isDigit(c)){
                errors.append("Last name cannot contain digits/");
                break;
            }
        }

        if(!date.equals("")){
            if(!date.matches("[0-9][0-9][0-9][0-9][-][0-9][0-9][-][0-9][0-9]"))
                errors.append("Date format invalid!The format used is yyyy-mm-dd/");

            String[] tokens = date.split("-");
            if(Integer.parseInt(tokens[0])>2020 || Integer.parseInt(tokens[0]) <1900)
                errors.append("The year must be greater than 1900 and less than 2021/");
            int month = Integer.parseInt(tokens[1]);
            int day = Integer.parseInt(tokens[2]);
            if ( month < 1 || month >12)
                errors.append("The month must be greater than 0 and less than 13/");
            if ( day < 1 || day >31)
                errors.append("The day must be greater than 0 and less than 32/");
        }
        else
            errors.append("Date cannot be empty!/");

        if( !("MNF".contains(gender) || gender.equals("")))
            errors.append("Gender invalid");

        if(!errors.toString().equals("")){
            throw new ValidationException(errors.toString());
        }

    }
}
