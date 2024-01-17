package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.user.dao.FriendListDao;

import java.time.LocalDate;
import java.util.Collection;

@Service
public class UserService {

    private final UserStorage userStorage;
    private final FriendListDao friendListDao;

    @Autowired
    public UserService(UserStorage userStorage, FriendListDao friendListDao) {
        this.userStorage = userStorage;
        this.friendListDao = friendListDao;
    }

    public void addNewFriend(Integer userId, Integer friendId) {
        friendListDao.addFriend(userId, friendId);

    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User createUser(User user) {
        validateUser(user);
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        validateUser(user);
        return userStorage.updateUser(user);
    }

    public User findById(Integer id) {
        return userStorage.findById(id);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        friendListDao.deleteFriend(userId, friendId);
    }

    public Collection<User> getUsersFriends(Integer id) {
        return friendListDao.getAll(id);
    }

    public Collection<User> findCommonFriends(Integer userId, Integer otherUserId) {
        return friendListDao.getCommonFriends(userId, otherUserId);

    }

    private void validateUser(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("email не должен быть пустым и должен содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().contains(" ")) {
            throw new ValidationException("логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }
}
