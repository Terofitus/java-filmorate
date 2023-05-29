package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public User addUserToStorage(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.addUserToStorage(user);
    }

    @Override
    public User updateUserInStorage(User user) {
        return userStorage.updateUserInStorage(user);
    }

    @Override
    public void deleteUserFromStorageById(Integer id) {
        userStorage.deleteUserFromStorageById(id);
    }

    @Override
    public void deleteAllUsers() {
        userStorage.deleteAllUsers();
    }

    @Override
    public User getUserById(Integer id) {
        return userStorage.getUserById(id);
    }

    @Override
    public List<User> getListOfFriends(Integer id) {
        return userStorage.getFriendsOfUser(id).stream().map(userStorage::getUserById).collect(Collectors.toList());
    }

    @Override
    public void addUserToFriends(Integer idOfUser, Integer idOfAddedUser) {
        userStorage.addUserToFriends(idOfUser, idOfAddedUser);
    }

    @Override
    public void deleteUserFromFriends(Integer idOfUser, Integer idOfDeletedUser) {
        userStorage.deleteUserFromFriends(idOfUser, idOfDeletedUser);
    }

    @Override
    public List<User> getListOfCommonFriends(Integer idOfUser, Integer idOfOtherUser) {
        List<Integer> friendsOfFirstUser = userStorage.getFriendsOfUser(idOfUser);
        List<Integer> friendsOfSecondUser = userStorage.getFriendsOfUser(idOfOtherUser);
        return friendsOfFirstUser.stream()
                .filter(friendsOfSecondUser::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }
}
