package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.user.dao.FriendListDao;
import ru.yandex.practicum.filmorate.storage.user.dao.FriendListDaoImp;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    UserController userController;
    UserStorage userStorage;
    UserService userService;
    FriendListDao friendListDao;

    @BeforeEach
    void init() {
        friendListDao = new FriendListDaoImp(new JdbcTemplate());
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage, friendListDao);
        userController = new UserController(userService);
    }

    @Test
    void shouldCreateUser() { //Корректные данные
        User user = new User("ivanov@yandex.ru", "ivanov1234", "ivan",
                LocalDate.of(1996, 06, 20));
        userController.createUser(user);
        Collection<User> users = userController.findAll();
        assertNotNull(users, "Список пользователей пуст.");
        assertEquals(1, users.size());
    }

    @Test
    void shouldNotCreateUserWithEmptyEmail() { //Проверка на пустой email
        User user = new User(null, "ivanov1234", "ivan",
                LocalDate.of(1996, 06, 20));
        Collection<User> users = userController.findAll();
        assertThrows(ValidationException.class, () -> userController.createUser(user));
        assertEquals(0, users.size());
    }

    @Test
    void shouldNotCreateUserWithBadEmail() { //Проверка на неверный email
        User user = new User("ivanov-yandex.ru", "ivanov1234", "ivan",
                LocalDate.of(1996, 06, 20));
        Collection<User> users = userController.findAll();
        assertThrows(ValidationException.class, () -> userController.createUser(user));
        assertEquals(0, users.size());
    }

    @Test
    void shouldNotCreateUserWithEmptyLogin() { //Проверка на пустой login
        User user = new User("ivanov@yandex.ru", null, "ivan",
                LocalDate.of(1996, 06, 20));
        Collection<User> users = userController.findAll();
        assertThrows(ValidationException.class, () -> userController.createUser(user));
        assertEquals(0, users.size());
    }

    @Test
    void shouldNotCreateUserWithBadLogin() { //Проверка на неверный login
        User user = new User("ivanov-yandex.ru", "ivanov 1 2 3 4", "ivan",
                LocalDate.of(1996, 06, 20));
        Collection<User> users = userController.findAll();
        assertThrows(ValidationException.class, () -> userController.createUser(user));
        assertEquals(0, users.size());
    }

    @Test
    void shouldCreateUserWithEmptyName() { //Проверка на пустое имя
        User user = new User("ivanov-yandex.ru", "ivanov1234", null,
                LocalDate.of(1996, 06, 20));
        Collection<User> users = userController.findAll();
        assertThrows(ValidationException.class, () -> userController.createUser(user));
        assertEquals(0, users.size());
    }

    @Test
    void shouldNotCreateUserWithBadBirthday() { //Проверка на неверное дата рождение(дата которая еще не наступила)
        User user = new User("ivanov-yandex.ru", "ivanov1234", "ivan",
                LocalDate.of(2024, 06, 20));
        Collection<User> users = userController.findAll();
        assertThrows(ValidationException.class, () -> userController.createUser(user));
        assertEquals(0, users.size());
    }

    @Test
    void shouldUpdateUser() { //Обновляем пользователя
        User user = new User("ivanov@yandex.ru", "ivanov1234", "ivan",
                LocalDate.of(1996, 06, 20));
        userController.createUser(user);
        user.setEmail("ivanov@gmail.com");
        userController.updateUser(user);
        Collection<User> users = userController.findAll();
        assertEquals(1, users.size());
    }

    @Test
    void shouldUpdateUnknown() { //проверка на несуществующего пользователя
        User user = new User("ivanov@gmail.com", "ivanov1234", "ivan",
                LocalDate.of(1996,06,20));
        user.setId(9999);
        assertThrows(NotFoundException.class, () -> userController.updateUser(user));
    }

    @Test
    void shouldUpdateUserGetAll() { //Получаем всех пользователей
        User user = new User("ivanov@yandex.ru", "ivanov1234", "ivan",
                LocalDate.of(1996, 06, 20));
        userController.createUser(user);
        User user2 = new User("petrov@yandex.ru", "petrov1234", "petr",
                LocalDate.of(1998, 05, 21));
        userController.createUser(user2);
        Collection<User> users = userController.findAll();
        assertEquals(2, users.size());
    }
}
