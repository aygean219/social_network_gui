package com.example.social_network_gui.repository.database;

import com.example.social_network_gui.domain.Friendship;
import com.example.social_network_gui.domain.Tuple;
import com.example.social_network_gui.domain.User;
import com.example.social_network_gui.repository.Repository;
import java.sql.*;
import com.example.social_network_gui.repository.memory.RepositoryException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.Optional;

public class FriendshipDatabaseRepository implements Repository<Tuple<User,User>, Friendship> {

    private final String url;
    private final String username;
    private final String password;


    public FriendshipDatabaseRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;

    }

    @Override
    public Optional<Friendship> findOne(Tuple<User, User> entity) {
        String quarry="SELECT * FROM friendship WHERE id1="+entity.getE2().getId()+" AND id2="+entity.getE1().getId()+";";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(quarry);
             ResultSet resultSet = statement.executeQuery()) {

            Long id1;
            Long id2;
            Optional<Friendship> f = Optional.empty();
            while(resultSet.next()) {
                id1 = Long.parseLong(resultSet.getString("id1"));
                id2 = Long.parseLong(resultSet.getString("id2"));
                String date = resultSet.getString(3);

                String sql = "SELECT * FROM userr WHERE id = ?";
                PreparedStatement statement1 = connection.prepareStatement(sql);
                statement1.setLong(1,id1);
                ResultSet resultSet1 = statement1.executeQuery();
                resultSet1.next();
                User user1 = new User(resultSet1.getString("first_name"),resultSet1.getString("last_name"),resultSet1.getString("date"),resultSet1.getString("gender"),resultSet1.getString("email"),resultSet1.getString("password"));

                user1.setId(resultSet1.getLong("id"));

                String sql2 = "SELECT * FROM userr WHERE id = ?";
                PreparedStatement statement2 = connection.prepareStatement(sql2);
                statement2.setLong(1,id2);
                ResultSet resultSet2 = statement2.executeQuery();
                resultSet2.next();
                User user2 = new User(resultSet2.getString("first_name"),resultSet2.getString("last_name"),resultSet2.getString("date"),resultSet2.getString("gender"),resultSet2.getString("email"),resultSet2.getString("password"));
                user2.setId(resultSet2.getLong("id"));

                Tuple<User,User> t = new Tuple<>(user1,user2);
                f = Optional.of(new Friendship(t,date));
            }
            return f;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public ArrayList<Friendship> findAll() {
        ArrayList<Friendship> friendships = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendship");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                String date = resultSet.getString(3);

                String sql = "SELECT * FROM userr WHERE id = ?";
                PreparedStatement statement1 = connection.prepareStatement(sql);
                statement1.setLong(1,id1);
                ResultSet resultSet1 = statement1.executeQuery();
                resultSet1.next();
                User user1 = new User(resultSet1.getString("first_name"),resultSet1.getString("last_name"),resultSet1.getString("date"),resultSet1.getString("gender"),resultSet1.getString("email"),resultSet1.getString("password"));
                user1.setId(resultSet1.getLong("id"));

                String sql2 = "SELECT * FROM userr WHERE id = ?";
                PreparedStatement statement2 = connection.prepareStatement(sql2);
                statement2.setLong(1,id2);
                ResultSet resultSet2 = statement2.executeQuery();
                resultSet2.next();
                User user2 = new User(resultSet2.getString("first_name"),resultSet2.getString("last_name"),resultSet2.getString("date"),resultSet2.getString("gender"),resultSet2.getString("email"),resultSet2.getString("password"));
                user2.setId(resultSet2.getLong("id"));

                Tuple<User,User> t = new Tuple<>(user1,user2);

                Friendship friendship = new Friendship(t,date);
                friendships.add(friendship);
            }
            return friendships;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }

    @Override
    public Optional<Friendship> save(Friendship entity) {

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendship WHERE id1=? AND id2=?");
            statement.setLong(1, entity.getId().getE1().getId());
            statement.setLong(2, entity.getId().getE2().getId());
            ResultSet rs = statement.executeQuery();
            if (rs.next())
                throw new RepositoryException("Friendship already exists!");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String querry = "INSERT INTO friendship VALUES('"+entity.getId().getE2().getId()+"','"
                +entity.getId().getE1().getId()+"','"
                +LocalDateTime.now().format(formatter)+"')";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(querry)) {
            statement.execute();
            return Optional.of(entity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Friendship> delete(Tuple<User, User> entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendship WHERE id1=? AND id2=?");
            statement.setLong(1, entity.getE2().getId());
            statement.setLong(2, entity.getE1().getId());
            ResultSet rs = statement.executeQuery();
            if (!rs.next())
                throw new RepositoryException("Friendship does not exist!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String querry = "DELETE FROM friendship WHERE id1="+entity.getE2().getId()+" and id2="
                +entity.getE1().getId()+";";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(querry);
        ) {
            statement.execute();
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Friendship> update(Friendship entity) {
        return Optional.empty();
    }
}

