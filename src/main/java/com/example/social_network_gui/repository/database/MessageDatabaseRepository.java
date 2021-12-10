package com.example.social_network_gui.repository.database;

import com.example.social_network_gui.domain.Message;
import com.example.social_network_gui.domain.User;
import com.example.social_network_gui.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageDatabaseRepository implements Repository<Long, Message> {
    private final String url;
    private final String username;
    private final String password;

    public MessageDatabaseRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }


    @Override
    public Optional<Message> findOne(Long aLong) {
        String query = "SELECT * FROM message WHERE id_message=?";
        List<User> userList = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1,aLong);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                Long id_message = resultSet.getLong("id_message");
                Long id_sending = resultSet.getLong("id_sending");
                String message = resultSet.getString("message");
                LocalDateTime date1 = LocalDateTime.parse(resultSet.getString("date"));
                Long id_reply = resultSet.getLong("reply");

                ArrayList<Long> id_users = new ArrayList<>();

                //receivers-id
                String query2 = "SELECT * FROM receiver WHERE id_message=?";
                PreparedStatement statement1 = connection.prepareStatement(query2);
                statement1.setLong(1, id_message.intValue());
                ResultSet resultSet1 = statement1.executeQuery();
                while (resultSet1.next()) {
                    Long id_user_receiver = resultSet1.getLong("id_user");
                    id_users.add(id_user_receiver);
                }
                //receivers-User
                for (Long id : id_users) {
                    String query3 = "SELECT * FROM userr WHERE id=?";
                    PreparedStatement statement2 = connection.prepareStatement(query3);
                    statement2.setLong(1, id);
                    ResultSet resultSet2 = statement2.executeQuery();
                    if (resultSet2.next()) {
                        User user_receiver = new User(resultSet2.getString("first_name"), resultSet2.getString("last_name"), resultSet2.getString("date"), resultSet2.getString("gender"), resultSet2.getString("email"), resultSet2.getString("password"));
                        user_receiver.setId(id);
                        userList.add(user_receiver);
                    }
                }

                //user sender
                String query3 = "SELECT * FROM userr WHERE id=?";
                PreparedStatement statement2 = connection.prepareStatement(query3);
                statement2.setLong(1, id_sending);
                ResultSet resultSet2 = statement2.executeQuery();
                User user_sender = new User();
                if (resultSet2.next()) {
                    user_sender = new User(resultSet2.getString("first_name"), resultSet2.getString("last_name"), resultSet2.getString("date"), resultSet2.getString("gender"), resultSet2.getString("email"), resultSet2.getString("password"));
                    user_sender.setId(id_sending);
                }
                Message message1 = new Message(user_sender, userList, message);
                message1.setId(id_message);
                message1.setDate(date1);
                Message reply_message;
                if (id_reply == 0) {
                    reply_message= null;
                }
                else {
                    reply_message=findOne(id_reply).get();
                }
                message1.setReply(reply_message);
                return Optional.of(message1);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public ArrayList<Message> findAll() {
        String query = "SELECT * FROM message";
        ArrayList<Message> messageList = new ArrayList<>();

        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                Long id_message = resultSet.getLong("id_message");
                Long id_sending = resultSet.getLong("id_sending");
                String message = resultSet.getString("message");
                LocalDateTime date1 = LocalDateTime.parse(resultSet.getString("date"));
                Long id_reply = resultSet.getLong("reply");
                ArrayList<Long> id_users = new ArrayList<>();
                List<User> userList = new ArrayList<>();

                //receivers-id
                String query2 = "SELECT * FROM receiver WHERE id_message=?";
                PreparedStatement statement1 = connection.prepareStatement(query2);
                statement1.setLong(1,id_message.intValue());
                ResultSet resultSet1 = statement1.executeQuery();
                while (resultSet1.next()){
                    Long id_user_receiver = resultSet1.getLong("id_user");
                    id_users.add(id_user_receiver);
                }
                //receivers-User
                for(Long id:id_users) {
                    String query3 = "SELECT * FROM userr WHERE id=?";
                    PreparedStatement statement2 = connection.prepareStatement(query3);
                    statement2.setLong(1,id);
                    ResultSet resultSet2 = statement2.executeQuery();
                    if (resultSet2.next()) {
                        User user_receiver = new User(resultSet2.getString("first_name"),resultSet2.getString("last_name"),resultSet2.getString("date"),resultSet2.getString("gender"),resultSet2.getString("email"),resultSet2.getString("password"));
                        user_receiver.setId(id);
                        userList.add(user_receiver);
                    }
                }

                //user sender
                String query3 = "SELECT * FROM userr WHERE id=?";
                PreparedStatement statement2 = connection.prepareStatement(query3);
                statement2.setLong(1,id_sending);
                ResultSet resultSet2 = statement2.executeQuery();
                User user_sender = new User();
                if (resultSet2.next()) {
                    user_sender = new User(resultSet2.getString("first_name"),resultSet2.getString("last_name"),resultSet2.getString("date"),resultSet2.getString("gender"),resultSet2.getString("email"),resultSet2.getString("password"));
                    user_sender.setId(id_sending);
                }
                Message message1 = new Message(user_sender,userList,message);
                message1.setId(id_message);
                message1.setDate(date1);
                Message reply_message;
                if (id_reply == 0) {
                    reply_message= null;
                }
                else {
                    reply_message=findOne(id_reply).get();
                }
                message1.setReply(reply_message);
                messageList.add(message1);

            }
            return messageList;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Optional<Message> save(Message entity) {
        String query = "INSERT INTO message  VALUES(?,?,?,?,?)";
        try(Connection connection= DriverManager.getConnection(url,username,password)){
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1,entity.getId().intValue());
            statement.setInt(2,entity.getFrom().getId().intValue());
            statement.setString(3,entity.getMessage());
            statement.setString(4,entity.getDate().toString());
            if(entity.getReply()==null){
                statement.setInt(5,0);
            }
            else{
                statement.setInt(5,entity.getReply().getId().intValue());
            }

            statement.executeUpdate();
            for(User user:entity.getTo()){
                String query2 = "INSERT INTO receiver VALUES(?,?)";
                statement = connection.prepareStatement(query2);
                statement.setInt(1,user.getId().intValue());
                statement.setInt(2,entity.getId().intValue());
                statement.executeUpdate();
            }
        } catch (SQLException e){
        e.printStackTrace();
        }
        return Optional.of(entity);
    }

    @Override
    public Optional<Message> delete(Long aLong) {
        return Optional.empty();
    }

    @Override
    public Optional<Message> update(Message entity) {
        return Optional.empty();
    }
}
