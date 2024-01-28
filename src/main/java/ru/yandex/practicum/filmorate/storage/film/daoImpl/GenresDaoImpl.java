package ru.yandex.practicum.filmorate.storage.film.daoImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.dao.GenresDao;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class GenresDaoImpl implements GenresDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Genre> getAllGenres() {
        return jdbcTemplate.query("SELECT * FROM GENRES", genreRowMapper());
    }

    @Override
    public Genre getGenreById(Integer genreId) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM GENRES \n" +
                    "WHERE GENRE_ID = ?", genreRowMapper(), genreId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Жанр с id = %d не найден", genreId));
        }
    }

    @Override
    public Collection<Genre> getGenresOfFilm(Integer filmId) {
        String sqlQuery = "SELECT * FROM GENRES g\n" +
                "JOIN FILM_GENRES fg ON g.GENRE_ID = fg.GENRE_ID \n" +
                "WHERE FILM_ID = ?";
        return jdbcTemplate.query(sqlQuery, genreRowMapper(), filmId);
    }

    private RowMapper<Genre> genreRowMapper() {
        return ((rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("genre_name")));
    }
}
