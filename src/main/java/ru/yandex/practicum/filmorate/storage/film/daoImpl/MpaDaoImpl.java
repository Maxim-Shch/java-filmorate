package ru.yandex.practicum.filmorate.storage.film.daoImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.dao.MpaDao;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class MpaDaoImpl implements MpaDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Mpa> getAllMpa() {
        String sqlQuery = "SELECT * FROM MPA_RATING mr";
        return jdbcTemplate.query(sqlQuery, mpaRowMapper());
    }

    @Override
    public Mpa getMpaById(Integer mpaId) {
        String sqlQuery = "SELECT * FROM MPA_RATING mr\n" +
                "WHERE mr.RATING_ID = ?";
        try {
            return jdbcTemplate.queryForObject(
                    sqlQuery,
                    mpaRowMapper(),
                    mpaId
            );
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("MPA-рейтинг с id = %d не найден", mpaId));
        }
    }

    private RowMapper<Mpa> mpaRowMapper() {
        return ((rs, rowNum) -> new Mpa(rs.getInt("rating_id"), rs.getString("rating"),
                rs.getString("rating_description")));
    }
}
