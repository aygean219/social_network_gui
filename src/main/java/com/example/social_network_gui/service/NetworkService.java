package com.example.social_network_gui.service;

import com.example.social_network_gui.domain.*;
import com.example.social_network_gui.repository.Repository;
import com.example.social_network_gui.repository.memory.RepositoryException;
import com.example.social_network_gui.utils.Graph;
import com.example.social_network_gui.utils.Status;
import com.example.social_network_gui.utils.events.RequestsChangeEvent;
import com.example.social_network_gui.utils.observer.Observable;
import com.example.social_network_gui.utils.observer.Observer;
import com.example.social_network_gui.validators.FriendshipValidator;
import com.example.social_network_gui.validators.ValidationException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class NetworkService implements Observable<RequestsChangeEvent> {
    private final Repository<Tuple<User, User>, Friendship> repo;
    private final Repository<Long, User> repoUser;
    private final Repository<Tuple<User, User>, FriendRequest> repoRequests;
    private final Repository<Long, Message> messageRepository;
    private final Repository<Long, RoleType> roleTypeRepository;
    private User loggedUser;
    private Long FreeIdMessage;
    private RoleType roleType;

    public NetworkService(Repository<Tuple<User, User>, Friendship> repo, Repository<Long, User> repoUser,
                          Repository<Tuple<User, User>, FriendRequest> repoRequest,
                          Repository<Long, Message> messageRepository, Repository<Long, RoleType> roleTypeRepository) {
        this.repo = repo;
        this.repoUser = repoUser;
        this.repoRequests = repoRequest;
        this.messageRepository = messageRepository;
        this.FreeIdMessage = 0L;
        this.roleTypeRepository = roleTypeRepository;
    }

    public void getRoles() {
        ArrayList<RoleType> roles = roleTypeRepository.findRoles(loggedUser);
        if (roles.size() > 1 && roles.size() != 0) {
            throw new ValidationException("This account has more roles please choose just 1 role( member or administrator):");
        } else {
            // System.out.println("size:"+roles.size());

            roleType = roles.get(0);
        }
    }

    public void setRole(String role) {
        ArrayList<RoleType> roles = roleTypeRepository.findRoles(loggedUser);
        if (roles.size() > 1 && roles.size() != 0) {
            if (role.equals("member") || role.equals("administrator")) {
                roleType = new RoleType(loggedUser, role);
            }
        }
    }

    public void switchRoles() {
        ArrayList<RoleType> roles = roleTypeRepository.findRoles(loggedUser);
        if (roles.size() == 2) {
            if (roleType.getRole().equals("member")) {
                roleType.setRole("administrator");
            } else {
                roleType.setRole("member");
            }
        }
    }

    public void login(String email, String password) {
        if (repoUser.findloggedUser(email, password).isPresent()) {
            loggedUser = repoUser.findloggedUser(email, password).get();
        } else {
            throw new ValidationException("Incorrect email or password.");
        }
    }

    public void signup(String firstName, String lastName, String date, String gender, String email, String password) {
        if (repoUser.findUser(email).isPresent()) {
            throw new ValidationException("User exist already. Try to login.");
        } else {
            loggedUser = new User(firstName, lastName, date, gender, email, password);
            //free id for user
            Long FreeId = 0L;
            int nr = 0;
            for (User user : repoUser.findAll()) {
                FreeId++;
                nr++;
                if (!FreeId.equals(user.getId())) {
                    break;
                }

            }
            if (nr == repoUser.findAll().size())
                FreeId++;
            loggedUser.setId(FreeId);
            repoUser.save(loggedUser);
            //free id for role type
            FreeId = 0L;
            nr = 0;
            for (RoleType role : roleTypeRepository.findAll()) {
                FreeId++;
                nr++;
                if (!FreeId.equals(role.getId())) {
                    break;
                }

            }
            if (nr == roleTypeRepository.findAll().size())
                FreeId++;
            roleType = new RoleType(loggedUser, "member");
            roleType.setId(FreeId);
            roleTypeRepository.save(roleType);
        }
    }


    /**
     * @return the community number
     */
    public int numberCommunities() {
        int nod = 0;
        for (User x : repoUser.findAll()) {
            if (nod < x.getId().intValue()) {
                nod = x.getId().intValue();
            }
        }
        nod++;
        Graph graph = new Graph(nod);
        repo.findAll().forEach(x -> graph.addEdge(x.getId().getE1().getId().intValue(), x.getId().getE2().getId().intValue()));
        return graph.connectedComponents();
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    /**
     * @return an array of integers with the id of the community users
     */
    public ArrayList<Integer> longestCommunity() {
        int nod = 0;
        for (User x : repoUser.findAll()) {
            if (nod < x.getId().intValue()) {
                nod = x.getId().intValue();
            }
        }
        nod++;
        Graph graph = new Graph(nod);
        repo.findAll().forEach(x -> graph.addEdge(x.getId().getE1().getId().intValue(), x.getId().getE2().getId().intValue()));
        return graph.longestPath();

    }

    public void addFriendship(String id1, String id2) {
        Long first = FriendshipValidator.validateId(id1);
        Long second = FriendshipValidator.validateId(id2);
        if (first > second) {
            Long aux = first;
            first = second;
            second = aux;
        }
        Optional<User> user = repoUser.findOne(first);
        Optional<User> user1 = repoUser.findOne(second);
        if (user.isPresent() && user1.isPresent())
            repo.save(new Friendship(new Tuple<>(user.get(), user1.get()), ""));
        else
            throw new RepositoryException("Invalid users!");
    }

    public void deleteFriendship(String id1, String id2) {
        Long first = FriendshipValidator.validateId(id1);
        Long second = FriendshipValidator.validateId(id2);
        if (first > second) {
            Long aux = first;
            first = second;
            second = aux;
        }
        Optional<User> user1 = repoUser.findOne(first);
        Optional<User> user2 = repoUser.findOne(second);
        if (user1.isPresent() && user2.isPresent()) {
            repo.delete(new Tuple<>(user1.get(), user2.get()));
            repoRequests.delete(new Tuple<>(user1.get(), user2.get()));
        } else
            throw new RepositoryException("Invalid friendship!");
    }

    public void deleteMoreFriendships(String id) {
        ArrayList<Friendship> friendships = repo.findAll();
        for (Friendship f : friendships) {
            String left = f.getId().getE1().toString();
            String right = f.getId().getE2().toString();

            if (left.equals(id) || right.equals(id)) {
                try {
                    deleteFriendship(left, right);
                } catch (RepositoryException ignored) {

                }
            }
        }
    }

    public Iterable<FriendshipDTO> friendsListOfUser(String id) {
        Iterable<Friendship> friendships = repo.findAll();
        return StreamSupport.stream(friendships.spliterator(), false)
                .filter(p -> (p.getId().getE2().getId().toString().equals(id) || p.getId().getE1().getId().toString().equals(id)))
                .map(friendship -> {
                    User user;
                    if (friendship.getId().getE1().getId().toString().equals(id))
                        user = friendship.getId().getE2();
                    else user = friendship.getId().getE1();
                    return new FriendshipDTO(user.getFirstName(), user.getLastName(), friendship.getDate());
                }).collect(Collectors.toList());
    }

    public Iterable<FriendRequest> friendshipIterable() {
        return repoRequests.findAll();
    }

    public FriendRequest getRequest(Tuple<User, User> entity) {
        return repoRequests.findOne(entity).get();
    }

    public Iterable<FriendshipDTO> friendsListOfUserByDate(String id, String month) {
        Iterable<Friendship> friendships = repo.findAll();
        return StreamSupport.stream(friendships.spliterator(), false)
                .filter(friendship -> ((friendship.getId().getE2().getId().toString().equals(id) || friendship.getId().getE1().getId().toString().equals(id)) && friendship.getMonth().equals(month)))
                .map(friendship -> {
                    User user;
                    if (friendship.getId().getE1().getId().toString().equals(id))
                        user = friendship.getId().getE2();
                    else user = friendship.getId().getE1();
                    return new FriendshipDTO(user.getFirstName(), user.getLastName(), friendship.getDate());
                }).collect(Collectors.toList());
    }

    public void sendFriendRequest(String id) {
        if (Long.parseLong(id) == loggedUser.getId())
            throw new ValidationException("You cannot send a friend request to yourself!");
        User user;
        Long first = FriendshipValidator.validateId(id);
        Optional<User> user1 = repoUser.findOne(first);
        if (user1.isPresent()) {
            user = user1.get();
//            if (repo.findOne(new Tuple<>(user, loggedUser)).isPresent() || repo.findOne(new Tuple<>(loggedUser, user)).isPresent())
//                throw new RepositoryException("You are already friend with this user!");
//            if (repoRequests.findOne(new Tuple<>(user, loggedUser)).isPresent())
//                throw new RepositoryException("You have a friend request from " + user.getFirstName() + " " + user.getLastName());
//            if (repoRequests.findOne(new Tuple<>(loggedUser, user)).isPresent())
//                throw new RepositoryException("You have already sent a friend request to " + user.getFirstName() + " " + user.getLastName());
            repoRequests.save(new FriendRequest(new Tuple<>(loggedUser, user), Status.PENDING));
        } else {
            throw new ValidationException("Id does not exist!");
        }
    }

    public Iterable<FriendRequest> getFriendRequests() {
        Iterable<FriendRequest> requests = repoRequests.findAll();
        if (loggedUser.getId() == 0L)
            return requests;
        ArrayList<FriendRequest> result = new ArrayList<>();
        for (FriendRequest friendRequest : requests) {
            if (friendRequest.getId().getE2().getId().equals(loggedUser.getId()))
                result.add(friendRequest);
        }
        return result;
    }

    public void acceptFriendRequest(String id) {
        User user;
        Long first = FriendshipValidator.validateId(id);
        Optional<User> user1 = repoUser.findOne(first);
        if (user1.isPresent()) {
            user = user1.get();
            repoRequests.update(new FriendRequest(new Tuple<>(user, loggedUser), Status.APPROVED));
            repo.save(new Friendship(new Tuple<>(loggedUser, user), new SimpleDateFormat("yyyy-MM-dd").format(new Date())));
        } else {
            throw new ValidationException("You do not have a friend request from user with id " + id + "!");
        }
    }


    public void rejectFriendRequest(String id) {
        User user;
        Long first = FriendshipValidator.validateId(id);
        Optional<User> user1 = repoUser.findOne(first);
        if (user1.isPresent()) {
            user = user1.get();
            repoRequests.update(new FriendRequest(new Tuple<>(user, loggedUser), Status.REJECTED));
        } else {
            throw new ValidationException("You do not have a friend request from user with id " + id + "!");
        }
    }

    public Iterable<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    ////////////////////////modifica sa raspunda la ultimul mesaj de la un user////////////////////////////
    public List<MessageDTO> get_message_for_user_logged() {

        List<MessageDTO> listOfAllMessages = new ArrayList<>();
        List<Message> lista = messageRepository.findAll();
        for (Message message : lista) {
            //if(message.getTo().contains(loggedUser) && message.getReply() == 0){
            System.out.println(message);
            if (message.getTo().contains(loggedUser) && message.getReply() != null) {


                listOfAllMessages.add(new MessageDTO(message.getId(), message.getFrom().getFirstName(), message.getMessage()));
            }
        }
        //fa un sort si ia ultimul mesaj de la fiecare user
        return listOfAllMessages;
    }

    private void checkId() {
        FreeIdMessage = 0L;
        int nr = 0;
        for (Message user : messageRepository.findAll()) {
            FreeIdMessage++;
            nr++;
            if (!FreeIdMessage.equals(user.getId())) {
                break;
            }

        }
        if (nr == messageRepository.findAll().size())
            FreeIdMessage++;
    }

    public void send_message(List<Long> receivers, String message) {
        User sender = loggedUser;
        List<User> list_of_receivers = new ArrayList<>();
        for (Long receiver : receivers) {
            if (receiver == loggedUser.getId()) {
                throw new ValidationException("You cannot send message to yourself!!");
            }
        }
        for (Long i : receivers) {
            if (repoUser.findOne(i).isPresent()) {
                User user = repoUser.findOne(i).get();
                list_of_receivers.add(user);
            }
        }
        Message message1 = new Message(sender, list_of_receivers, message);
        checkId();
        message1.setId(FreeIdMessage);
        messageRepository.save(message1);

    }

    public void reply_message(Long id_message, String message) {
        Message message0 = messageRepository.findOne(id_message).get();
        User replier = loggedUser;
        if (message0.getFrom().getId() == null) {
            throw new ValidationException("You cannot reply to the admin!");
        }
        List<User> list_of_messages = new ArrayList<>();
        if (messageRepository.findOne(id_message).isPresent()) {
            User user = messageRepository.findOne(id_message).get().getFrom();
            list_of_messages.add(user);
        }
        Message replyMessage = new Message(replier, list_of_messages, message, message0);
        checkId();
        //message0.setReply(replyMessage);
        replyMessage.setId(FreeIdMessage);
        messageRepository.save(replyMessage);
    }


    public List<Message> cronological_message(Long id1) {
        Long id2 = loggedUser.getId();
        List<Message> mesaje = new ArrayList<>();
        messageRepository.findAll().forEach(x -> {
            if ((x.getFrom().getId() == id1 && x.getTo().contains(repoUser.findOne(id2).get())) || (x.getFrom().getId() == id2 && x.getTo().contains(repoUser.findOne(id1).get())))
                mesaje.add(x);
        });
        if (mesaje.size() == 0) {
            throw new ValidationException("There is no messages between you and that user");
        }
        return mesaje.stream().sorted((m1, m2) -> m1.getDate().compareTo(m2.getDate())).collect(Collectors.toList());
    }

    public List<User> other_users(Long id) {
        List<User> users = new ArrayList<>();
        repoUser.findAll().forEach(x -> {
            if (x.getId() != id)
                users.add(x);
        });
        return users;
    }

    public List<User> getFriendsOfLoggeduser() {
        Iterable<Friendship> all = repo.findAll();
        return StreamSupport.stream(all.spliterator(), false)
                .filter(friendship -> ((friendship.getId().getE2().equals(loggedUser) || friendship.getId().getE1().equals(loggedUser))))
                .map(friendship -> {
                    User user;
                    if (friendship.getId().getE1().equals(loggedUser))
                        user = friendship.getId().getE2();
                    else user = friendship.getId().getE1();
                    return user;
                }).collect(Collectors.toList());
    }

    public List<User> getSuggestionsForLoggeduser() {
        Iterable<User> all = repoUser.findAll();
        Iterable<FriendRequest> requests = repoRequests.findAll();
        ArrayList<Friendship> friendships = repo.findAll();
        List<User> suggestions = new ArrayList<>();
        all.forEach(u -> {

            if (!(u.equals(loggedUser)) && repo.findOne(new Tuple<>(loggedUser, u)).isEmpty()
                    && repo.findOne(new Tuple<>(u, loggedUser)).isEmpty()
                    && (repoRequests.findOne(new Tuple<>(loggedUser, u)).isEmpty())
                    && (repoRequests.findOne(new Tuple<>(u, loggedUser)).isEmpty())) {
                suggestions.add(u);
            }
        });
        return suggestions;
    }

    public void deleteRequest(Tuple<User, User> id) {
        repoRequests.delete(id);
    }

    private List<Observer<RequestsChangeEvent>> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer<RequestsChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<RequestsChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(RequestsChangeEvent t) {
        observers.stream().forEach(x -> x.update(t));
    }
}
