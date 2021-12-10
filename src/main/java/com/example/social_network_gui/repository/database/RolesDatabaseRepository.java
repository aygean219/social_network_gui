package com.example.social_network_gui.repository.database;

import com.example.social_network_gui.domain.RoleType;
import com.example.social_network_gui.domain.User;
import com.example.social_network_gui.repository.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class RolesDatabaseRepository implements Repository<Long, RoleType> {
    private final String url;
    private final String username;
    private final String password;

    public RolesDatabaseRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }


    @Override
    public Optional<RoleType> findOne(Long aLong) {
        String query= "SELCET * FROM role_types WHERE id=" + aLong +";";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement(query)){
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                int userId = resultSet.getInt(2);
                String role = resultSet.getString(3);

                String query2="SELECT * FROM userr WHERE id=?";
                PreparedStatement statement1 = connection.prepareStatement(query2);
                statement1.setInt(1,userId);
                ResultSet resultSet1 = statement1.executeQuery();
                if(resultSet1.next()){
                    User user = new User(resultSet1.getString("first_name"), resultSet1.getString("last_name"), resultSet1.getString("date"), resultSet1.getString("gender"),resultSet1.getString("email"),resultSet1.getString("password"));
                    user.setId(Long.valueOf(userId));

                    RoleType roleType = new RoleType(user,role);
                    return Optional.of(roleType);
                }
            }

        }catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public ArrayList<RoleType> findAll() {
        ArrayList<RoleType> roleTypes = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(url,username,password);
        PreparedStatement statement= connection.prepareStatement("SELECT * FROM role_types");
        ResultSet resultSet = statement.executeQuery()){
            while (resultSet.next()){
                int userId = resultSet.getInt(3);
                String role = resultSet.getString(2);

                String query2="SELECT * FROM userr WHERE id=?";
                PreparedStatement statement1 = connection.prepareStatement(query2);
                statement1.setInt(1,userId);
                ResultSet resultSet1 = statement1.executeQuery();
                if(resultSet1.next()){
                    User user = new User(resultSet1.getString("first_name"), resultSet1.getString("last_name"), resultSet1.getString("date"), resultSet1.getString("gender"),resultSet1.getString("email"),resultSet1.getString("password"));
                    user.setId(Long.valueOf(userId));

                    RoleType roleType = new RoleType(user,role);
                    roleTypes.add(roleType);
                }
            }
            return roleTypes;
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Optional<RoleType> save(RoleType entity) {
        String query = "INSERT INTO role_types (id_user,role) VALUES(?,?)";
        try(Connection connection= DriverManager.getConnection(url,username,password)){
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1,entity.getUser().getId().intValue());
        statement.setString(2,entity.getRole());
        statement.execute();
        return Optional.of(entity);
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<RoleType> delete(Long aLong) {
        String query = "DELETE FROM role_types WHERE id_role=?";
        try(Connection connection= DriverManager.getConnection(url,username,password)){
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1,aLong.intValue());
            statement.execute();
            return Optional.empty();
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<RoleType> update(RoleType entity) {
        String query = "UPDATE role_types SET role=? WHERE id_role=?";
        try(Connection connection= DriverManager.getConnection(url,username,password)){
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1,entity.getRole());
            statement.setInt(2,entity.getId().intValue());
            statement.execute();
            return Optional.empty();
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public ArrayList<RoleType> findRoles(User user){
        ArrayList<RoleType> roleTypes = new ArrayList<>();
        String query= "SELECT * FROM role_types WHERE id_user=?";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement= connection.prepareStatement(query)){
            statement.setInt(1,user.getId().intValue());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                String role = resultSet.getString(3);
                RoleType roleType = new RoleType(user,role);
                    roleTypes.add(roleType);
                }
            return roleTypes;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
