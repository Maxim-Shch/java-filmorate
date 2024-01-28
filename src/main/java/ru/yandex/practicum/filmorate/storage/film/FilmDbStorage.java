package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.dao.GenresDao;
import ru.yandex.practicum.filmorate.storage.film.dao.LikesDao;

import java.util.*;


@Component
@Primary
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final LikesDao likesDao;
    private final GenresDao genresDao;

    @Override
    public Film addNewFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(Objects.requireNonNull(jdbcTemplate.getDataSource()))
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        Number id = simpleJdbcInsert.executeAndReturnKey(Map.of(
                "name", film.getName(),
                "description", film.getDescription(),
                "release_date", film.getReleaseDate(),
                "duration", film.getDuration(),
                "mpa_id", film.getMpa().getId()
                ));
        film.setId(id.intValue());

        if (film.getGenres() != null) {
            addGenresToFilm(film);
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "update films set " +
                "name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "where film_id = ?";
        int updatedFilm = jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        if (updatedFilm == 0) {
            throw new NotFoundException("Фильм с идентификатором " + film.getId() + " не найден");
        }

        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            deleteGenresFromFilm(film);
        } else {
            film.setGenres(new LinkedHashSet<>(film.getGenres()));
            updateFilmGenres(film);
        }
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        String sqlQuery = "SELECT FILM_ID , NAME , f.DESCRIPTION , RELEASE_DATE , DURATION , mr.RATING_ID , mr.RATING , " +
                "mr.RATING_DESCRIPTION \n" +
                "FROM FILMS f\n" +
                "JOIN MPA_RATING mr ON f.MPA_ID = MR.RATING_ID;";
        return jdbcTemplate.query(sqlQuery, filmRowMapper());
    }

    @Override
    public Film findById(Integer id) {
        String sqlQuery = "SELECT FILM_ID , NAME , f.DESCRIPTION , RELEASE_DATE , DURATION , mr.RATING_ID , " +
                "mr.RATING , mr.RATING_DESCRIPTION\n" +
                "FROM FILMS f\n" +
                "JOIN MPA_RATING mr ON f.MPA_ID = MR.RATING_ID\n" +
                "WHERE film_id = ?";
        try {
            return jdbcTemplate.queryForObject(
                    sqlQuery,
                    filmRowMapper(),
                    id
            );
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Фильм с id = %d не найден", id));
        }
    }

    @Override
    public Collection<Film> findTheMostPopulars(Integer count) {
        String sqlQuery = "SELECT f.*, MR.RATING_ID, mr.RATING, MR.RATING_DESCRIPTION \n" +
                "FROM FILMS f \n" +
                "JOIN MPA_RATING mr ON f.MPA_ID = MR.RATING_ID \n" +
                "LEFT JOIN FILM_LIKES fl ON fl.FILM_ID = f.FILM_ID\n" +
                "GROUP BY f.FILM_ID \n" +
                "ORDER BY COUNT(fl.USER_ID) DESC \n" +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, filmRowMapper(), count);
    }

    private RowMapper<Film> filmRowMapper() {
        return (rs, rowNum) -> new Film(rs.getInt("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                new HashSet<>(likesDao.getFilmLikes(rs.getInt("film_id"))),
                new ArrayList<>(genresDao.getGenresOfFilm(rs.getInt("film_id"))),
                new Mpa(rs.getInt("rating_id"), rs.getString("rating"),
                rs.getString("rating_description")));
    }

    private void addGenresToFilm(Film film) {
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)",
                    film.getId(), genre.getId());
        }
    }

    private void updateFilmGenres(Film film) {
        deleteGenresFromFilm(film);
        addGenresToFilm(film);
    }

    private void deleteGenresFromFilm(Film film) {
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
    }
}
