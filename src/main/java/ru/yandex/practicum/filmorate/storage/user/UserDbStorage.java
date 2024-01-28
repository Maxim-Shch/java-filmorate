package ru.yandex.practicum.filmorate.storage.user;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

@Component
@Primary
@AllArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<User> findAll() {
        return jdbcTemplate.query("select * from users",
                userRowMapper()
        );
    }

    @Override
    public User createUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(Objects.requireNonNull(jdbcTemplate.getDataSource()))
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        Map<String, String> params = Map.of("user_email", user.getEmail(),
                "user_login", user.getLogin(),
                "user_name", user.getName(),
                "user_birthday", user.getBirthday().toString());
        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        user.setId(id.intValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "update users set " +
                "user_email = ?, user_login = ?, user_name = ?, user_birthday = ? " +
                "where user_id = ?";
        int updatedUser = jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        if (updatedUser == 0) {
            throw new NotFoundException("Пользователь с идентификатором " + user.getId() + " не найден");
        }
        return user;
    }

    @Override
    public User findById(Integer id) {
        try {
            return jdbcTemplate.queryForObject(
                    "select * from users where user_id = ?",
                    userRowMapper(),
                    id
            );
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь с идентификатором " + id + " не найден");
        }
    }

    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> new User(rs.getInt("user_id"), rs.getString("user_email"),
                rs.getString("user_login"), rs.getString("user_name"),
                rs.getDate("user_birthday").toLocalDate());
    }
}