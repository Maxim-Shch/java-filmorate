package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
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
public class GenresDaoTest {

    private final JdbcTemplate jdbcTemplate;
    GenresDao genresDao;

    @BeforeEach
    void init() {
        genresDao = new GenresDaoImpl(jdbcTemplate);
    }

    @Test
    public void testGetAllGenres() {
        Collection<Genre> genres = genresDao.getAllGenres();

        assertThat(genres).isNotNull();
    }

    @Test
    public void testGetGenreById() {
        Genre savedGenre = genresDao.getGenreById(1);

        assertThat(savedGenre)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(new Genre(1, "Комедия"));
    }

    @Test
    public void testGetGenresOfFilm() {
        LikesDao likesDao = new LikesDaoImpl(jdbcTemplate);
        FilmStorage filmStorage = new FilmDbStorage(jdbcTemplate, likesDao, genresDao);
        Film newFilm = new Film("filmOne", "новый единичный фильм",
                LocalDate.of(2000, 1,1), 60,
                new Mpa(1, "G", "Нет возрастных ограничений"));
        newFilm.setGenres(List.of(new Genre(1, "Комедия")));
        filmStorage.addNewFilm(newFilm);

        Collection<Genre> filmGenres = genresDao.getGenresOfFilm(newFilm.getId());

        assertThat(filmGenres)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(new Genre(1, "Комедия")));
    }
}