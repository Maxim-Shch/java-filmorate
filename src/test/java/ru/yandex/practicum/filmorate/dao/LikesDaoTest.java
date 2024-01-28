package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.dao.GenresDao;
import ru.yandex.practicum.filmorate.storage.film.dao.LikesDao;
import ru.yandex.practicum.filmorate.storage.film.daoImpl.GenresDaoImpl;
import ru.yandex.practicum.filmorate.storage.film.daoImpl.LikesDaoImpl;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)

public class LikesDaoTest {

    private final JdbcTemplate jdbcTemplate;
    LikesDao likesDao;

    @BeforeEach
    void init() {
        likesDao = new LikesDaoImpl(jdbcTemplate);
    }

    @Test
    public void testGetFilmLikes() {
        UserStorage userStorage = new UserDbStorage(jdbcTemplate);
        GenresDao genresDao = new GenresDaoImpl(jdbcTemplate);
        FilmStorage filmStorage = new FilmDbStorage(jdbcTemplate, likesDao, genresDao);

        User newUser = new User("user@email.ru", "vanya123", "Ivan Petrov",
                LocalDate.of(1990, 1, 1));
        userStorage.createUser(newUser);

        Film newFilm = new Film("filmOne", "новый единичный фильм",
                LocalDate.of(2000, 1, 1), 60,
                new Mpa(1, "G", "Нет возрастных ограничений"));
        filmStorage.addNewFilm(newFilm);

        likesDao.addLikeToFilm(newFilm.getId(), newUser.getId());
        Set<Integer> likes = likesDao.getFilmLikes(newFilm.getId());

        assertThat(likes)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(Set.of(newUser.getId()));
    }

    @Test
    public void testAddLikeToFilm() {
        UserStorage userStorage = new UserDbStorage(jdbcTemplate);
        GenresDao genresDao = new GenresDaoImpl(jdbcTemplate);
        FilmStorage filmStorage = new FilmDbStorage(jdbcTemplate, likesDao, genresDao);

        User newUser = new User("user@email.ru", "vanya123", "Ivan Petrov",
                LocalDate.of(1990, 1, 1));
        userStorage.createUser(newUser);

        Film newFilm = new Film("filmOne", "новый единичный фильм",
                LocalDate.of(2000, 1, 1), 60,
                new Mpa(1, "G", "Нет возрастных ограничений"));
        filmStorage.addNewFilm(newFilm);

        likesDao.addLikeToFilm(newFilm.getId(), newUser.getId());

        Assertions.assertFalse(likesDao.getFilmLikes(newFilm.getId()).isEmpty());
    }

    @Test
    public void testDeleteLikeFromFilm() {
        UserStorage userStorage = new UserDbStorage(jdbcTemplate);
        GenresDao genresDao = new GenresDaoImpl(jdbcTemplate);
        FilmStorage filmStorage = new FilmDbStorage(jdbcTemplate, likesDao, genresDao);

        User newUser = new User("user@email.ru", "vanya123", "Ivan Petrov",
                LocalDate.of(1990, 1, 1));
        userStorage.createUser(newUser);

        Film newFilm = new Film("filmOne", "новый единичный фильм",
                LocalDate.of(2000, 1, 1), 60,
                new Mpa(1, "G", "Нет возрастных ограничений"));
        filmStorage.addNewFilm(newFilm);

        likesDao.addLikeToFilm(newFilm.getId(),newUser.getId());

        likesDao.deleteLikeFromFilm(newFilm.getId(), newUser.getId());
        Set<Integer> likes = likesDao.getFilmLikes(newFilm.getId());

        Assertions.assertTrue(likes.isEmpty());
    }
}