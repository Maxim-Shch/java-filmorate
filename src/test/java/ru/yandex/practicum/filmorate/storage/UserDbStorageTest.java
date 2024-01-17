package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private UserDbStorage userStorage;

    @BeforeEach
    void init() {
        userStorage = new UserDbStorage(jdbcTemplate);
    }

    @Test
    public void testFindUserById() {
        // Подготавливаем данные для теста
        User newUser = new User("user@email.ru", "vanya123", "Ivan Petrov",
                LocalDate.of(1990, 1, 1));
        userStorage.createUser(newUser);

        // вызываем тестируемый метод
        User savedUser = userStorage.findById(newUser.getId());

        // проверяем утверждения
        assertThat(savedUser)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(newUser);        // и сохраненного пользователя - совпадают
    }

    @Test
    public void testCreateUser() {
        User newUser = new User(4, "user@email.ru", "vanya123", "Ivan Petrov",
                LocalDate.of(1990, 1, 1));
        User savedUser = userStorage.createUser(newUser);

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser);
    }

    @Test
    public void testUpdateUser() {
        User newUser = new User("user@email.ru", "vanya123", "Ivan Petrov",
                LocalDate.of(1990, 1, 1));
        userStorage.createUser(newUser);
        //изменили данные
        newUser = new User(newUser.getId(), "user@email.ru", "vanya123", "Masya Petrov",
                LocalDate.of(1996, 5, 21));
        User updatedUser = userStorage.updateUser(newUser);//вызвали метод обновления из хранилища

        // проверяем утверждения
        assertThat(updatedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser);
    }

    @Test
    public void testFindAllUsers() {
        User newUser = new User(1, "user@email.ru", "vanya123", "Ivan Petrov",
                LocalDate.of(1990, 1, 1));
        User newUser2 = new User(2, "user2@email.ru", "kate", "Kate Shchigoleva",
                LocalDate.of(1998, 5, 31));
        userStorage.createUser(newUser);
        userStorage.createUser(newUser2);//сохранили данные

        // вызываем тестируемый метод
        Collection<User> users = userStorage.findAll();

        // проверяем утверждения
        assertThat(users)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(List.of(newUser, newUser2));        // и сохраненного пользователя - совпадают
    }
}