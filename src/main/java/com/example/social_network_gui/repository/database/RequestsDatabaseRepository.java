package com.example.social_network_gui.repository.database;

import com.example.social_network_gui.domain.FriendRequest;
import com.example.social_network_gui.domain.Tuple;
import com.example.social_network_gui.domain.User;
import com.example.social_network_gui.repository.Repository;
import com.example.social_network_gui.repository.memory.RepositoryException;
import com.example.social_network_gui.utils.Status;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class RequestsDatabaseRepository implements Repository<Tuple<User, User>, FriendRequest> {

    private final String url;
    private final String username;
    private final String password;


    public RequestsDatabaseRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;

    }

    @Override
    public Optional<FriendRequest> findOne(Tuple<User, User> entity) {
        String query = "SELECT * FROM requests WHERE from_id=? AND to_id=?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, entity.getE1().getId());
            statement.setLong(2, entity.getE2().getId());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int from = resultSet.getInt(1);
                int to = resultSet.getInt(2);
                Status status = Status.valueOf(resultSet.getString("status"));


                String sql = "SELECT * FROM userr WHERE id = ?";
                PreparedStatement statement1 = connection.prepareStatement(sql);
                statement1.setInt(1, from);
                ResultSet resultSet1 = statement1.executeQuery();
                if (resultSet1.next()) {
                    User user1 = new User(resultSet1.getString("first_name"), resultSet1.getString("last_name"), resultSet1.getString("date"), resultSet1.getString("gender"),resultSet1.getString("email"),resultSet1.getString("password"));
                    user1.setId(resultSet1.getLong("id"));

                    String sql2 = "SELECT * FROM userr WHERE id = ?";
                    PreparedStatement statement2 = connection.prepareStatement(sql2);
                    statement2.setInt(1, to);
                    ResultSet resultSet2 = statement2.executeQuery();
                    if (resultSet2.next()) {
                        User user2 = new User(resultSet2.getString("first_name"), resultSet2.getString("last_name"), resultSet2.getString("date"), resultSet2.getString("gender"),resultSet2.getString("email"),resultSet2.getString("password"));
                        user2.setId(resultSet2.getLong("id"));

                        Tuple<User, User> t = new Tuple<>(user1, user2);
                        return Optional.of(new FriendRequest(t, status));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public ArrayList<FriendRequest> findAll() {
        ArrayList<FriendRequest> requests = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from requests");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int from = resultSet.getInt("from_id");
                int to = resultSet.getInt("to_id");
                Status status = Status.valueOf(resultSet.getString("status"));

                String sql = "SELECT * FROM userr WHERE id = ?";
                PreparedStatement statement1 = connection.prepareStatement(sql);
                statement1.setInt(1, from);
                ResultSet resultSet1 = statement1.executeQuery();
                if (resultSet1.next()) {
                    User user1 = new User(resultSet1.getString("first_name"), resultSet1.getString("last_name"), resultSet1.getString("date"), resultSet1.getString("gender"),resultSet1.getString("email"),resultSet1.getString("password"));
                    user1.setId(resultSet1.getLong("id"));

                    String sql2 = "SELECT * FROM userr WHERE id = ?";
                    PreparedStatement statement2 = connection.prepareStatement(sql2);
                    statement2.setInt(1, to);
                    ResultSet resultSet2 = statement2.executeQuery();
                    if (resultSet2.next()) {
                        User user2 = new User(resultSet2.getString("first_name"), resultSet2.getString("last_name"), resultSet2.getString("date"), resultSet2.getString("gender"),resultSet2.getString("email"),resultSet2.getString("password"));
                        user2.setId(resultSet2.getLong("id"));

                        Tuple<User, User> t = new Tuple<>(user1, user2);

                        FriendRequest request = new FriendRequest(t, status);
                        requests.add(request);
                    }
                }
            }
            return requests;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    @Override
    public Optional<FriendRequest> save(FriendRequest entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM requests WHERE from_id=? AND to_id=?");
            statement.setInt(1, entity.getId().getE1().getId().intValue());
            statement.setInt(2, entity.getId().getE2().getId().intValue());
            ResultSet rs = statement.executeQuery();
            if (rs.next())
                throw new RepositoryException("FriendRequest already exists");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String sqlString = "INSERT INTO requests (from_id,to_id,status) VALUES (?,?,?)";
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setInt(1, entity.getId().getE1().getId().intValue());
            statement.setInt(2, entity.getId().getE2().getId().intValue());
            statement.setString(3, entity.getStatus().toString());
            statement.execute();
            return Optional.of(entity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<FriendRequest> delete(Tuple<User, User> entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM requests WHERE from_id=? AND to_id=?");
            statement.setInt(1, entity.getE1().getId().intValue());
            statement.setInt(2, entity.getE2().getId().intValue());
            ResultSet rs = statement.executeQuery();
            if (!rs.next())
                throw new RepositoryException("FriendRequest does not exist");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String query = "DELETE FROM requests WHERE from_id = ? AND to_id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, entity.getE1().getId().intValue());
            statement.setInt(2, entity.getE2().getId().intValue());
            statement.execute();
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<FriendRequest> update(FriendRequest entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM requests WHERE from_id=? AND to_id=?");
            statement.setInt(1, entity.getId().getE1().getId().intValue());
            statement.setInt(2, entity.getId().getE2().getId().intValue());
            ResultSet rs = statement.executeQuery();
            if (!rs.next())
                throw new RepositoryException("FriendRequest does not exist");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String sqlString = "UPDATE requests SET status= ? WHERE from_id=? AND to_id=?";
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement(sqlString);
            statement.setString(1, entity.getStatus().toString());
            statement.setInt(2, entity.getId().getE1().getId().intValue());
            statement.setInt(3, entity.getId().getE2().getId().intValue());

            statement.execute();
            return Optional.of(entity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
