package com.example.social_network_gui.repository.database;

import com.example.social_network_gui.domain.*;
import com.example.social_network_gui.repository.Repository;
import com.example.social_network_gui.utils.EventSubscription;

import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

public class EventsDatabaseRepository implements Repository<Long, Event> {
    private final String url;
    private final String username;
    private final String password;

    public EventsDatabaseRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }


    @Override
    public Optional<Event> findOne(Long aLong) {
        String query = "SELECT * FROM events WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setLong(1, aLong);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                String location = resultSet.getString("location");
                LocalDateTime date = LocalDateTime.parse(resultSet.getString("date"));
                Event event = new Event(title, description, location, date);
                event.setId(id);

                String query2 = "SELECT * FROM userr INNER JOIN events_subscriptions ON events_subscriptions.user_id = userr.id AND events_subscriptions.event_id = ?";
                statement = connection.prepareStatement(query2);
                statement.setLong(1, event.getId());
                resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    User user = new User(resultSet.getString("first_name"), resultSet.getString("last_name"),
                            resultSet.getString("date"), resultSet.getString("gender"),
                            resultSet.getString("email"), resultSet.getString("password"));
                    user.setId(resultSet.getLong("id"));
                    if ("SUBSCRIBE".equals(resultSet.getString("subscription_status"))) {
                        event.addUser(user, EventSubscription.SUBSCRIBE);
                    }
                    event.addUser(user, EventSubscription.UNSUBSCRIBE);
                }

                return Optional.of(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public ArrayList<Event> findAll() {
        ArrayList<Event> events = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement("SELECT * from events ORDER BY date DESC");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                String location = resultSet.getString("location");
                LocalDateTime date = LocalDateTime.parse(resultSet.getString("date"));
                Event event = new Event(title, description, location, date);
                event.setId(id);

                String query2 = "SELECT * FROM userr INNER JOIN events_subscriptions ON events_subscriptions.user_id = userr.id AND events_subscriptions.event_id = ?";
                PreparedStatement statement1 = connection.prepareStatement(query2);
                statement1.setLong(1, event.getId());
                ResultSet resultSet2 = statement1.executeQuery();

                while (resultSet2.next()) {
                    User user = new User(resultSet2.getString("first_name"), resultSet2.getString("last_name"),
                            resultSet2.getString("date"), resultSet2.getString("gender"),
                            resultSet2.getString("email"), resultSet2.getString("password"));
                    user.setId(resultSet2.getLong("id"));

                    if ("SUBSCRIBE".equals(resultSet2.getString("subscription_status"))) {
                        event.addUser(user, EventSubscription.SUBSCRIBE);
                    }
                    else{
                    event.addUser(user, EventSubscription.UNSUBSCRIBE);}

                }

                events.add(event);
            }
            return events;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    @Override
    public Optional<Event> save(Event entity) {
        String query = "INSERT INTO events (title,description,location, date)  VALUES(?,?,?,?)";
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, entity.getTitle());
            statement.setString(2, entity.getDescription());
            statement.setString(3, entity.getLocation());
            statement.setString(4, entity.getDateTime().toString());

            statement.executeUpdate();
            for (Map.Entry<User, EventSubscription> user : entity.getUsers().entrySet()) {
                String query2 = "INSERT INTO events_subscriptions VALUES(?,?,?)";
                statement = connection.prepareStatement(query2);
                statement.setInt(1, entity.getId().intValue());
                statement.setInt(2, user.getKey().getId().intValue());
                statement.setString(3, user.getValue().toString());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.of(entity);
    }

    @Override
    public Optional<Event> delete(Long aLong) {
        String query = "DELETE FROM events WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, aLong.intValue());
            statement.execute();
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Event> update(Event entity) {

        String query = "UPDATE events SET title = ?, description = ?, location=?, date=?  WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, entity.getTitle());
            statement.setString(2, entity.getDescription());
            statement.setString(3, entity.getLocation());
            statement.setString(4, entity.getDateTime().toString());
            statement.setLong(5, entity.getId());


            statement.execute();


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.of(entity);
    }

    public void subscribeUser2(User user, Event event) {
        String query = "INSERT INTO events_subscriptions VALUES (?,?,?) ";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setLong(1, event.getId());
            statement.setLong(2, user.getId());
            statement.setString(3, EventSubscription.SUBSCRIBE.toString());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                String query2 = "UPDATE events_subscriptions SET subscription_status =  ? WHERE event_id = ? AND user_id = ?";

                try (Connection connection2 = DriverManager.getConnection(url, username, password)) {
                    PreparedStatement statement2 = connection.prepareStatement(query2);

                    statement2.setLong(2, event.getId());
                    statement2.setLong(3, user.getId());
                    statement2.setString(1, EventSubscription.SUBSCRIBE.toString());
                    statement2.execute();

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void subscribeUser(User user, Event event) {
        String query = "UPDATE events_subscriptions SET subscription_status =  ? WHERE event_id = ? AND user_id = ?";


        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setLong(2, event.getId());
            statement.setLong(3, user.getId());
            statement.setString(1, EventSubscription.SUBSCRIBE.toString());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                String query2 = "INSERT INTO events_subscriptions VALUES (?,?,?) ";

                try (Connection connection2 = DriverManager.getConnection(url, username, password)) {
                    PreparedStatement statement2 = connection.prepareStatement(query2);

                    statement2.setLong(1, event.getId());
                    statement2.setLong(2, user.getId());
                    statement2.setString(3, EventSubscription.SUBSCRIBE.toString());
                    statement2.execute();

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unsubscribeUser(User user, Event event) {
        String query = "UPDATE events_subscriptions SET subscription_status =  ? WHERE event_id = ? AND user_id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setLong(2, event.getId());
            statement.setLong(3, user.getId());
            statement.setString(1, EventSubscription.UNSUBSCRIBE.toString());
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public ArrayList<Notification> findEvents(User loggedUser){
        ArrayList<Notification> notifications = new ArrayList<>();
        String query="SELECT events.id,events.title,events.description,events.location,events.date FROM events " +
                "INNER JOIN events_subscriptions ON events_subscriptions.event_id=events.id " +
                "WHERE events_subscriptions.user_id = ? AND events_subscriptions.subscription_status='SUBSCRIBE'";
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1,loggedUser.getId());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                String location = resultSet.getString("location");
                LocalDateTime date = LocalDateTime.parse(resultSet.getString("date"));
                Event event = new Event(title, description, location, date);
                event.setId(id);

                String query2 = "SELECT * FROM userr INNER JOIN events_subscriptions ON events_subscriptions.user_id = userr.id AND events_subscriptions.event_id = ?";
                PreparedStatement statement1 = connection.prepareStatement(query2);
                statement1.setLong(1, event.getId());
                ResultSet resultSet2 = statement1.executeQuery();

                while (resultSet2.next()) {
                    User user = new User(resultSet2.getString("first_name"), resultSet2.getString("last_name"),
                            resultSet2.getString("date"), resultSet2.getString("gender"),
                            resultSet2.getString("email"), resultSet2.getString("password"));
                    user.setId(resultSet2.getLong("id"));

                    if ("SUBSCRIBE".equals(resultSet2.getString("subscription_status"))) {
                        event.addUser(user, EventSubscription.SUBSCRIBE);
                    }
                    else{
                        event.addUser(user, EventSubscription.UNSUBSCRIBE);}

                }
                Instant now = Instant.now();
                Duration diff = Duration.between(now, date.toInstant(ZoneOffset.UTC));
                String time="Hours left "+diff.toHours();
                Notification notification=new Notification(loggedUser,event,time);
                notifications.add(notification);
            }
            return notifications;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }
}
