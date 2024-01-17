package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.dao.GenresDao;
import ru.yandex.practicum.filmorate.storage.film.dao.LikesDao;
import ru.yandex.practicum.filmorate.storage.film.daoImpl.GenresDaoImpl;
import ru.yandex.practicum.filmorate.storage.film.daoImpl.LikesDaoImpl;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private FilmStorage filmStorage;

    @BeforeEach
    void init() {
        LikesDao likesDao = new LikesDaoImpl(jdbcTemplate);
        GenresDao genresDao = new GenresDaoImpl(jdbcTemplate);
        filmStorage = new FilmDbStorage(jdbcTemplate, likesDao, genresDao);
    }

    @Test
    public void testAddNewFilm() {
        // Подготавливаем данные для теста
        Film newFilm = new Film(1, "filmOne", "новый единичный фильм",
                LocalDate.of(2000, 1,1), 60, new Mpa(1, "R", "R desc"));

        // вызываем тестируемый метод
        Film savedFilm = filmStorage.addNewFilm(newFilm);

        // проверяем утверждения
        assertThat(savedFilm)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(newFilm);        // и сохраненного пользователя - совпадают
    }

    @Test
    public void testUpdateNewFilm() {
        Film newFilm = new Film("filmOne", "новый единичный фильм",
                LocalDate.of(2000, 1,1), 60, new Mpa(1, "R", "R desc"));
        filmStorage.addNewFilm(newFilm);
        newFilm = new Film(newFilm.getId(),"filmTwo", "новый вторичный фильм",
                LocalDate.of(2001, 1,1), 60, new Mpa(1, "R", "R desc"));

        Film updatedFilm = filmStorage.updateFilm(newFilm);

        assertThat(updatedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newFilm);
    }

    @Test
    public void testFindAllFilms() {
        Film newFilm = new Film(1, "filmOne", "новый единичный фильм",
                LocalDate.of(2000, 1,1), 60,
                new Mpa(1, "G", "Нет возрастных ограничений"));
        Film newFilm2 = new Film(2, "filmOne", "новый единичный фильм",
                LocalDate.of(2000, 1,1), 60,
                new Mpa(1, "G", "Нет возрастных ограничений"));

        filmStorage.addNewFilm(newFilm);
        filmStorage.addNewFilm(newFilm2);

        Collection<Film> films = filmStorage.findAll();

        assertThat(films)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(newFilm, newFilm2));
    }

    @Test
    public void testFindByIdFilm() {
        Film newFilm = new Film("filmOne", "новый единичный фильм",
                LocalDate.of(2000, 1,1), 60,
                new Mpa(1, "G", "Нет возрастных ограничений"));
        filmStorage.addNewFilm(newFilm);

        Film savedFilm = filmStorage.findById(newFilm.getId());

        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newFilm);
    }

    @Test
    public void testFindTheMostPopulars() {
        Film newFilm = new Film(1, "filmOne", "новый единичный фильм",
                LocalDate.of(2000, 1,1), 60,
                new Mpa(1, "G", "Нет возрастных ограничений"));
        Film newFilm2 = new Film(2, "filmOne", "новый единичный фильм",
                LocalDate.of(2000, 1,1), 60,
                new Mpa(1, "G", "Нет возрастных ограничений"));

        filmStorage.addNewFilm(newFilm);
        filmStorage.addNewFilm(newFilm2);

        Collection<Film> films = filmStorage.findTheMostPopulars(10);

        assertThat(films)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(newFilm, newFilm2));
    }
}