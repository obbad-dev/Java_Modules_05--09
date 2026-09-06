package _42.spring.service.application;

import _42.spring.service.models.User;
import _42.spring.service.repositories.UsersRepository;
import _42.spring.service.repositories.UsersRepositoryJdbcImpl;
import _42.spring.service.repositories.UsersRepositoryJdbcTemplateImpl;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static HikariConfig setupConfig() {
        HikariConfig hc = new HikariConfig();
        hc.setJdbcUrl("jdbc:postgresql://localhost:5432/testdb");
        hc.setUsername("postgres");
        hc.setPassword("postgres");
        hc.setDriverClassName("org.postgresql.Driver");
        return hc;
    }
    static void main() {
        HikariDataSource dataSource = new HikariDataSource(setupConfig());

        UsersRepository usersRepository = new UsersRepositoryJdbcImpl(dataSource);

        UsersRepository usersRepository1 = new UsersRepositoryJdbcTemplateImpl(new JdbcTemplate(dataSource));

        // plain jdbc
//        Optional<User> user = usersRepository.findByEmail("oualidobbad@gmail.com");
//        user.ifPresentOrElse(
//            u -> System.out.println("Found user: " + u),
//            () -> System.out.println("User not found")
//        );

        // jdbcTemplate
        Optional<User> user1 = usersRepository1.findByEmail("oualidobbad@gmail.com");
        user1.ifPresentOrElse(
            u -> System.out.println("Found user: " + u),
            () -> System.out.println("User not found")
        );
    }
}
