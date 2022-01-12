package com.example.social_network_gui.repository.database;

import com.example.social_network_gui.domain.Chat;
import com.example.social_network_gui.domain.Message;
import com.example.social_network_gui.domain.RoleType;
import com.example.social_network_gui.domain.User;
import com.example.social_network_gui.repository.Repository;

import java.sql.*;
import java.time.LocalDate;
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
                    LocalDateTime date2=LocalDateTime.now() ;
                    Long id_reply = resultSet.getLong("reply");
                    String chat_name="";
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

                    //chat name
                    query2 = "SELECT * FROM receiver WHERE id_message=?";
                    statement1 = connection.prepareStatement(query2);
                    statement1.setLong(1, id_message.intValue());
                    resultSet1 = statement1.executeQuery();
                    if (resultSet1.next()) {
                        chat_name=resultSet1.getString("chat_name");
                        date2=LocalDateTime.parse(resultSet1.getString("date"));
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
                    Chat to = new Chat(chat_name,userList);
                    to.setDate(date2);
                    Message message1 = new Message(user_sender, to, message);
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
                    LocalDateTime date2=LocalDateTime.now() ;
                    Long id_reply = resultSet.getLong("reply");
                    String chat_name="";
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

                    //chat name
                    query2 = "SELECT * FROM receiver WHERE id_message=?";
                    statement1 = connection.prepareStatement(query2);
                    statement1.setLong(1, id_message.intValue());
                    resultSet1 = statement1.executeQuery();
                    if (resultSet1.next()) {
                        chat_name=resultSet1.getString("chat_name");
                        date2=LocalDateTime.parse(resultSet1.getString("date"));
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

                    Chat to= new Chat(chat_name,userList);
                    to.setDate(date2);
                    Message message1 = new Message(user_sender,to,message);
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
                for(User user:entity.getTo().getUsers()){
                    String query2 = "INSERT INTO receiver VALUES(?,?,?,?)";
                    statement = connection.prepareStatement(query2);
                    statement.setInt(1,user.getId().intValue());
                    statement.setInt(2,entity.getId().intValue());
                    statement.setString(3,entity.getTo().getName());
                    statement.setString(4,entity.getTo().getDate().toString());
                    statement.executeUpdate();
                }
            } catch (SQLException e){
                e.printStackTrace();
            }
            return Optional.of(entity);
        }

        public ArrayList<Message> findMessages(Long id1,Long id2) {
            String query = "SELECT message.id_message,message.id_sending,message.message,message.date,message.date,message.reply FROM message\n" +
                    "INNER JOIN receiver ON receiver.id_message= message.id_message AND (receiver.id_user=? OR receiver.id_user=?)\n" +
                    "WHERE (id_sending =? OR id_sending=?)\n" +
                    "ORDER BY message.date ASC;";
            ArrayList<Message> messageList = new ArrayList<>();

            try(Connection connection = DriverManager.getConnection(url,username,password);
                PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setLong(1,id1);
                statement.setLong(2,id2);
                statement.setLong(3,id1);
                statement.setLong(4,id2);
                ResultSet resultSet = statement.executeQuery();

                while(resultSet.next()) {
                    Long id_message = resultSet.getLong("id_message");
                    Long id_sending = resultSet.getLong("id_sending");
                    String message = resultSet.getString("message");
                    LocalDateTime date1 = LocalDateTime.parse(resultSet.getString("date"));
                    LocalDateTime date2= LocalDateTime.now();
                    Long id_reply = resultSet.getLong("reply");
                    String chat_name="";
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

                    //chat name
                    query2 = "SELECT * FROM receiver WHERE id_message=?";
                    statement1 = connection.prepareStatement(query2);
                    statement1.setLong(1, id_message.intValue());
                    resultSet1 = statement1.executeQuery();
                    if (resultSet1.next()) {
                        chat_name=resultSet1.getString("chat_name");
                        date2=LocalDateTime.parse(resultSet1.getString("date"));
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

                    Chat to= new Chat(chat_name,userList);
                    to.setDate(date2);
                    Message message1 = new Message(user_sender,to,message);
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
        public ArrayList<Chat> findChats(Long id) {
            String query = "SELECT DISTINCT receiver.chat_name FROM receiver INNER JOIN message ON message.id_message = receiver.id_message AND (message.id_sending=? OR receiver.id_user = ?);";
            ArrayList<Chat> chatList = new ArrayList<>();
            try(Connection connection = DriverManager.getConnection(url,username,password);
                PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setLong(1,id);
                statement.setLong(2,id);
                ResultSet resultSet = statement.executeQuery();
                List<String> chat_names=new ArrayList<>();

                while(resultSet.next()) {
                    String chat_name = resultSet.getString(1);
                    chat_names.add(chat_name);
                }
                List<Long> id_of_users=new ArrayList<>();
                for(String chats: chat_names) {
                    String query2 = "SELECT * FROM receiver WHERE chat_name=?";
                    PreparedStatement statement1 = connection.prepareStatement(query2);
                    chats="'"+chats+"'";
                    statement1.setString(1, chats);
                    ResultSet resultSet1 = statement1.executeQuery();
                    while (resultSet1.next()) {
                        System.out.println(resultSet.getLong(1));
                        id_of_users.add(resultSet.getLong(1));
                    }
                    LocalDateTime date2 = LocalDateTime.now();
                    query2 = "SELECT * FROM receiver WHERE chat_name=?";
                    statement1 = connection.prepareStatement(query2);
                    statement1.setString(1, chats);
                    resultSet1 = statement1.executeQuery();
                    if (resultSet1.next()) {
                        date2 = LocalDateTime.parse(resultSet1.getString("date"));
                    }
                    List<User> userList = new ArrayList<>();
                    for (Long idd : id_of_users) {
                        String query3 = "SELECT * FROM userr WHERE id=?";
                        PreparedStatement statement2 = connection.prepareStatement(query3);
                        statement2.setLong(1, idd);
                        ResultSet resultSet2 = statement2.executeQuery();
                        if (resultSet2.next()) {
                            User user_receiver = new User(resultSet2.getString("first_name"), resultSet2.getString("last_name"), resultSet2.getString("date"), resultSet2.getString("gender"), resultSet2.getString("email"), resultSet2.getString("password"));
                            user_receiver.setId(idd);
                            userList.add(user_receiver);
                        }
                    }
                    Chat chat = new Chat(chats, userList);
                    chat.setDate(date2);
                    chatList.add(chat);
                }
                return chatList;
            }catch (SQLException e){
                e.printStackTrace();
            }
            return null;
        }

        public ArrayList<Message> findMessagesOfUser(LocalDate startDate, LocalDate endDate,User loggedUser){
            String query ="SELECT message.id_message,message.id_sending,message.message,message.date,message.reply" +
                    " FROM message INNER JOIN receiver ON receiver.id_message=message.id_message " +
                    "WHERE (receiver.id_user = ? OR message.id_sending=?) AND message.date BETWEEN ? AND ?;";
            ArrayList<Message> messageList = new ArrayList<>();

            try(Connection connection = DriverManager.getConnection(url,username,password);
                PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setLong(1,loggedUser.getId());
                statement.setLong(2,loggedUser.getId());
                statement.setString(3,startDate.toString());
                statement.setString(4,endDate.toString());
                ResultSet resultSet = statement.executeQuery();

                while(resultSet.next()) {
                    Long id_message = resultSet.getLong("id_message");
                    Long id_sending = resultSet.getLong("id_sending");
                    String message = resultSet.getString("message");
                    LocalDateTime date1 = LocalDateTime.parse(resultSet.getString("date"));
                    LocalDateTime date2= LocalDateTime.now();
                    Long id_reply = resultSet.getLong("reply");
                    String chat_name="";
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

                    //chat name
                    query2 = "SELECT * FROM receiver WHERE id_message=?";
                    statement1 = connection.prepareStatement(query2);
                    statement1.setLong(1, id_message.intValue());
                    resultSet1 = statement1.executeQuery();
                    if (resultSet1.next()) {
                        chat_name=resultSet1.getString("chat_name");
                        date2=LocalDateTime.parse(resultSet1.getString("date"));
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

                    Chat to= new Chat(chat_name,userList);
                    to.setDate(date2);
                    Message message1 = new Message(user_sender,to,message);
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
    public ArrayList<Message> findMessagesFromTo(LocalDate startDate,LocalDate endDate,User user,User loggedUser){
        String query ="SELECT DISTINCT message.id_message,message.id_sending,message.message,message.date,message.date,message.reply\n" +
                "FROM message\n" +
                "INNER JOIN receiver ON receiver.id_message=message.id_message\n" +
                "AND (receiver.id_user = ? OR message.id_sending=?)\n" +
                "AND message.date BETWEEN ? AND ?;";
        ArrayList<Message> messageList = new ArrayList<>();

        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1,loggedUser.getId());
            statement.setLong(2,user.getId());
            statement.setString(3,startDate.toString());
            statement.setString(4,endDate.toString());
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                Long id_message = resultSet.getLong("id_message");
                Long id_sending = resultSet.getLong("id_sending");
                String message = resultSet.getString("message");
                LocalDateTime date1 = LocalDateTime.parse(resultSet.getString("date"));
                LocalDateTime date2= LocalDateTime.now();
                Long id_reply = resultSet.getLong("reply");
                String chat_name="";
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

                //chat name
                query2 = "SELECT * FROM receiver WHERE id_message=?";
                statement1 = connection.prepareStatement(query2);
                statement1.setLong(1, id_message.intValue());
                resultSet1 = statement1.executeQuery();
                if (resultSet1.next()) {
                    chat_name=resultSet1.getString("chat_name");
                    date2=LocalDateTime.parse(resultSet1.getString("date"));
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

                Chat to= new Chat(chat_name,userList);
                to.setDate(date2);
                Message message1 = new Message(user_sender,to,message);
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
        public Optional<Message> delete(Long aLong) {
            return Optional.empty();
        }

        @Override
        public Optional<Message> update(Message entity) {
            return Optional.empty();
        }
    }
