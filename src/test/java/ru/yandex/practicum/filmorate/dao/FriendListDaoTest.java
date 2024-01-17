package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.user.dao.FriendListDao;
import ru.yandex.practicum.filmorate.storage.user.dao.FriendListDaoImp;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FriendListDaoTest {

    private final JdbcTemplate jdbcTemplate;
    private FriendListDao friendListDao;
    private UserStorage userStorage;

    @BeforeEach
    void init() {
        friendListDao = new FriendListDaoImp(jdbcTemplate);
        userStorage = new UserDbStorage(jdbcTemplate);
    }

    @Test
    public void testAddFriend() {
        User newUser = new User("user@email.ru", "vanya123", "Ivan Petrov",
                LocalDate.of(1990, 1, 1));
        User newUser2 = new User("user@email.ru", "sasha123", "Sasha Petrov",
                LocalDate.of(1990, 1, 1));
        userStorage.createUser(newUser);
        userStorage.createUser(newUser2);

        friendListDao.addFriend(newUser.getId(), newUser2.getId());
    }

    @Test
    public void testDeleteFriend() {
        User newUser = new User("user@email.ru", "vanya123", "Ivan Petrov",
                LocalDate.of(1990, 1, 1));
        User newUser2 = new User("user@email.ru", "sasha123", "Sasha Petrov",
                LocalDate.of(1990, 1, 1));
        userStorage.createUser(newUser);
        userStorage.createUser(newUser2);

        friendListDao.addFriend(newUser.getId(), newUser2.getId());
        friendListDao.deleteFriend(newUser.getId(), newUser2.getId());
    }

    @Test
    public void testGetAllFriends() {
        User newUser = new User("user@email.ru", "vanya123", "Ivan Petrov",
                LocalDate.of(1990, 1, 1));
        User newUser2 = new User("user@email.ru", "sasha123", "Sasha Petrov",
                LocalDate.of(1990, 1, 1));
        userStorage.createUser(newUser);
        userStorage.createUser(newUser2);

        friendListDao.addFriend(newUser.getId(), newUser2.getId());

        Collection<User> friends = friendListDao.getAll(newUser.getId());

        assertThat(friends)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(newUser2));
    }

    @Test
    public void testGetCommonFriends() {
        User newUser = new User("user@email.ru", "vanya123", "Ivan Petrov",
                LocalDate.of(1990, 1, 1));
        User newUser2 = new User("user@email.ru", "sasha123", "Sasha Petrov",
                LocalDate.of(1990, 1, 1));
        User commonFriend = new User("common@email.ru", "common", "Vasya Petrov",
                LocalDate.of(1990, 1, 1));
        userStorage.createUser(newUser);
        userStorage.createUser(newUser2);
        userStorage.createUser(commonFriend);

        friendListDao.addFriend(newUser.getId(), commonFriend.getId());
        friendListDao.addFriend(newUser2.getId(), commonFriend.getId());

        Collection<User> commonFriends = friendListDao.getCommonFriends(newUser.getId(), newUser2.getId());

        assertThat(commonFriends)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(commonFriend));
    }
}