package ru.yandex.practicum.filmorate.storage.film.daoImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.storage.film.dao.LikesDao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class LikesDaoImpl implements LikesDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Set<Integer> getFilmLikes(Integer filmId) {
        String sqlQuery = "SELECT user_id FROM film_likes WHERE film_id = ?";
        List<Integer> filmLikes = jdbcTemplate.queryForList(sqlQuery, Integer.class, filmId);
        return new HashSet<>(filmLikes);
    }

    @Override
    public void addLikeToFilm(Integer filmId, Integer userId) {
        String sqlQuery = "INSERT INTO film_likes(film_id, user_id)" +
                "VALUES(?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public void deleteLikeFromFilm(Integer filmId, Integer userId) {
        if (checkFilmLikes(filmId) == 0) {
            throw new NotFoundException(String.format("У фильма с id = %d нет лайков", filmId));
        }
        String sqlQuery = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    private int checkFilmLikes(int id) {
        String sql = "select count(*) from FILM_LIKES where FILM_ID = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, id);
    }
}
