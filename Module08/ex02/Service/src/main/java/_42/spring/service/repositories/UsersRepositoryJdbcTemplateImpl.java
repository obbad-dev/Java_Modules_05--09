package _42.spring.service.repositories;

import _42.spring.service.models.User;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

public class UsersRepositoryJdbcTemplateImpl implements UsersRepository
{
    private final NamedParameterJdbcTemplate jdbcTemplate;

    private RowMapper<User> rowMapper = (rs, numRows) -> (
            new User(rs.getLong("id"), rs.getString("email"))
    );

    public UsersRepositoryJdbcTemplateImpl(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null)
            throw new RuntimeException("Email must not be null");
        String sql = "SELECT * FROM users WHERE email = :email";
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("email", email);
        return jdbcTemplate.query(sql, param, rowMapper)
                .stream()
                .findFirst();
    }

    @Override
    public User findById(Long id) {
        if (id == null)
            throw new RuntimeException("Id must not be null");
        String QUERY = "SELECT id, email FROM users WHERE id = :id";
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("id", id);
        return jdbcTemplate.query(QUERY, param, rowMapper).stream().findFirst().orElse(null);
    }

    @Override
    public List<User> findAll() {
        String QUERY = "SELECT * FROM users";
        return jdbcTemplate.query(QUERY, rowMapper);
    }

    @Override
    public void save(User entity) {
        if (entity == null || entity.getEmail() == null)
            throw new RuntimeException("User object and email must not be null");
        String QUERY = "INSERT INTO users (email) VALUES (:email)";
        GeneratedKeyHolder holder = new GeneratedKeyHolder();
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("email", entity.getEmail());
        jdbcTemplate.update(QUERY, param, holder, new String[] {"id"});

        if (holder.getKey() != null)
            entity.setId(holder.getKey().longValue());

    }

    @Override
    public void update(User entity) {
        if (entity == null || entity.getId() == null || entity.getEmail() == null)
            throw new RuntimeException("User object, id and email must not be null");
        String QUERY = "UPDATE users SET email = :email WHERE id = :id";
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("email", entity.getEmail());
        param.addValue("id", entity.getId());
        jdbcTemplate.update(QUERY, param);

    }

    @Override
    public void delete(Long id) {
        if (id == null)
            throw new RuntimeException("Id must not be null");
        String QUERY = "DELETE FROM users WHERE id = :id";
        jdbcTemplate.update(QUERY, Map.of("id", id));
    }
}
