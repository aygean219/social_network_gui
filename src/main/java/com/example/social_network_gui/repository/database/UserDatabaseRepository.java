package com.example.social_network_gui.repository.database;

import com.example.social_network_gui.domain.ConvertPassword;
import com.example.social_network_gui.domain.User;
import com.example.social_network_gui.repository.Repository;
import com.example.social_network_gui.repository.memory.RepositoryException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class UserDatabaseRepository implements Repository<Long, User> {
    private final String url;
    private final String username;
    private final String password;

    public UserDatabaseRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<User> findOne(Long id) {
        String query = "SELECT * FROM userr WHERE id=" + id + ";";
        try (
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()) {

            Optional<User> f = Optional.empty();
            while (resultSet.next()) {
                Long id1 = Long.parseLong(resultSet.getString(1));
                String first_name = resultSet.getString(2);
                String last_name = resultSet.getString(3);
                String date = resultSet.getString(4);
                String gender = resultSet.getString(5);
                String email = resultSet.getString(6);
                String password = resultSet.getString(7);
                User user = new User(first_name, last_name, date, gender,email,password);
                user.setId(id1);
                f = Optional.of(user);
            }
            return f;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public ArrayList<User> findAll() {
        ArrayList<User> users = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from userr");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong(1);
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(3);
                String date = resultSet.getString(4);
                String gender = resultSet.getString(5);
                String email = resultSet.getString(6);
                String password = resultSet.getString(7);

                User user = new User(firstName, lastName, date, gender,email,password);
                user.setId(id);
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public Optional<User> save(User entity) {
        //validator.validate(entity);
        ConvertPassword convertPassword= new ConvertPassword();

        String query = "INSERT INTO userr VALUES('" + entity.getId().intValue() + "','"
                + entity.getFirstName() + "','" + entity.getLastName() + "','"
                + entity.getDate() + "','" + entity.getGender() + "','"
                + entity.getEmail() + "','" + convertPassword.encrypt(entity.getPassword())+ "')";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.execute();
            return Optional.of(entity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> delete(Long id) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM userr WHERE id=? ");
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();
            if (!rs.next())
                throw new RepositoryException("User does not exist!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String query = "DELETE FROM userr WHERE id=?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            statement.execute();
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> update(User entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM userr WHERE id=? ");
            statement.setLong(1, entity.getId());
            ResultSet rs = statement.executeQuery();
            if (!rs.next())
                throw new RepositoryException("User does not exist!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String sql = "update userr set first_name=?, last_name=?, birth_date=?,email=?,password=? where id=?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ConvertPassword convertPassword= new ConvertPassword();
            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());
            ps.setString(3, entity.getDate());
            ps.setString(4,entity.getEmail());
            ps.setString(5,convertPassword.encrypt(entity.getPassword()));
            ps.setLong(6, entity.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findUser(String email) {
        String query = "SELECT * FROM userr WHERE email = ? ";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Long id1 = resultSet.getLong(1);
                String first_name = resultSet.getString(2);
                String last_name = resultSet.getString(3);
                String date = resultSet.getString(4);
                String gender = resultSet.getString(5);
                String password = resultSet.getString(7);
                User user = new User(first_name, last_name, date, gender,email,password);
                user.setId(id1);
                return Optional.of(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    @Override
    public Optional<User> findloggedUser(String email1, String password1){
        ConvertPassword convertPassword= new ConvertPassword();
        String query = "SELECT * FROM userr WHERE email= ? AND password= ?";
        try(Connection connection = DriverManager.getConnection(url,username,password)){
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1,email1);
            statement.setString(2,convertPassword.encrypt(password1));
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
            Long id1 = resultSet.getLong(1);
            String first_name = resultSet.getString(2);
            String last_name = resultSet.getString(3);
            String date = resultSet.getString(4);
            String gender = resultSet.getString(5);
            String email = resultSet.getString(6);
            String password = resultSet.getString(7);
            User user = new User(first_name, last_name, date, gender,email,password);
            user.setId(id1);
            return Optional.of(user);
            }
        } catch (SQLException e) {
        e.printStackTrace();
        }
        return Optional.empty();
    }
}


