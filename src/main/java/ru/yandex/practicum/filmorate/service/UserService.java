package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
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
        return userStorage.getFriendsOfUser(id).stream().map(userStorage::getUserById).collect(Collectors.toList());
    }

    public void addUserToFriends(Integer idOfUser, Integer idOfAddedUser) {
        userStorage.addUserToFriends(idOfUser, idOfAddedUser);
    }

    public void deleteUserFromFriends(Integer idOfUser, Integer idOfDeletedUser) {
        userStorage.deleteUserFromFriends(idOfUser,idOfDeletedUser);
    }

    public List<User> getListOfCommonFriends(Integer idOfUser, Integer idOfOtherUser) {
        List<Integer> friendsOfFirstUser = userStorage.getFriendsOfUser(idOfUser);
        List<Integer> friendsOfSecondUser = userStorage.getFriendsOfUser(idOfOtherUser);
        return friendsOfFirstUser.stream()
                .filter(friendsOfSecondUser::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }
}
