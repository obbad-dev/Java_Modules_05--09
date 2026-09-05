package _42.spring.service.repositories;

import _42.spring.service.models.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Optional;
import _42.spring.service.models.*;

public class UsersRepositoryJdbcTemplateImpl implements UsersRepository{

    private final JdbcTemplate jdbcTemplate;

//    private RowMapper rowMapper = (rs, numRows) -> (
//            new User(rs.getLong("id"), rs.getString("email"))
//    );

    private RowMapper rowMapper = (rs, rowNum) -> (
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setEmail(rs.getString("email"));
            );
    public UsersRepositoryJdbcTemplateImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<User> findByEmail(String email) {

        return jdbcTemplate.;
    }

    @Override
    public User findById(Long id) {
        return null;
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public void save(User entity) {

    }

    @Override
    public void update(User entity) {

    }

    @Override
    public void delete(Long id) {

    }
}
