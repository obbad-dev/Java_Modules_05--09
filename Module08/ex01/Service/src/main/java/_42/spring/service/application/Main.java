package _42.spring.service.application;

import _42.spring.service.models.User;
import _42.spring.service.repositories.UsersRepository;
import _42.spring.service.repositories.UsersRepositoryJdbcImpl;
import _42.spring.service.repositories.UsersRepositoryJdbcTemplateImpl;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

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

        try {

            User user = new User(null, "hassan@gmail.com");
            usersRepository.save(user);
            Optional<User> us = usersRepository.findByEmail("hassan@gmail.com");
            us.ifPresentOrElse(
                    (user1 -> System.out.println("user added success: " + user1)),
                    () -> System.out.println("user not found")
            );

            user.setEmail("hwisini@42.fr.com");
            usersRepository.update(user);
            System.out.println("user updated succefully: " + usersRepository.findById(user.getId()));
            usersRepository.delete(user.getId());
            System.out.println("user deleted succefully: " + user);
            System.out.println("ALL USERS");
            List<User> users = usersRepository.findAll();
            for (User user2 : users)
            {
                System.out.println(user2);
            }
        }catch (Exception e)
        {
           e.printStackTrace();
        }
    }
}
