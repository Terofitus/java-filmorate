package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addUserToStorage(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.addUserToStorage(user);
    }

    public User updateUserInStorage(User user) {
        return userStorage.updateUserInStorage(user);
    }

    public void deleteUserFromStorageById(Integer id) {
        userStorage.deleteUserFromStorageById(id);
    }

    public void deleteAllUsers() {
        userStorage.deleteAllUsers();
    }

    public User getUserById(Integer id) {
        return userStorage.getUserById(id);
    }

    public List<User> getListOfFriends(Integer id) {
        return userStorage.getUserById(id).getFriends()
                .stream().map(userStorage::getUserById).collect(Collectors.toList());
    }

    public void addUserToFriends(Integer idOfUser, Integer idOfAddedUser) {
        User user1 = userStorage.getUserById(idOfUser);
        User user2 = userStorage.getUserById(idOfAddedUser);
        user1.getFriends().add(idOfAddedUser);
        user2.getFriends().add(idOfUser);
    }

    public void deleteUserFromFriends(Integer idOfUser, Integer idOfDeletedUser) {
        User user1 = userStorage.getUserById(idOfUser);
        User user2 = userStorage.getUserById(idOfDeletedUser);
        user1.getFriends().remove(idOfDeletedUser);
        user2.getFriends().remove(idOfUser);
    }

    public List<User> getListOfCommonFriends(Integer idOfUser, Integer idOfOtherUser) {
        User user1 = userStorage.getUserById(idOfUser);
        User user2 = userStorage.getUserById(idOfOtherUser);
        Set<Integer> friendsOfFirstUser = user1.getFriends();
        Set<Integer> friendsOfSecondUser = user2.getFriends();
        return friendsOfFirstUser.stream()
                .filter(friendsOfSecondUser::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }
}
