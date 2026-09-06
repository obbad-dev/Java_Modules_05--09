package _42.spring.service.application;

import _42.spring.service.models.User;
import _42.spring.service.repositories.UsersRepository;
import _42.spring.service.repositories.UsersRepositoryJdbcImpl;
import _42.spring.service.repositories.UsersRepositoryJdbcTemplateImpl;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.util.List;
import java.util.Optional;

public class Main {
    static HikariConfig setupConfig() {
        HikariConfig hc = new HikariConfig();
        hc.setJdbcUrl("jdbc:postgresql://localhost:5432/testdb");
        hc.setUsername("postgres");
        hc.setPassword("postgres");
        hc.setDriverClassName("org.postgresql.Driver");
        return hc;
    }
    public static void main(String [] args) {
        try (HikariDataSource dataSource = new HikariDataSource(setupConfig())) {
            UsersRepository usersRepository;
            
            System.out.println("=================== JDBC Implementation ==================");
            
            usersRepository = new UsersRepositoryJdbcImpl(dataSource);
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

            System.out.println("=================== JDBC Template Implementation ==================");
            usersRepository = new UsersRepositoryJdbcTemplateImpl(dataSource);
            User user1 = new User(null, "JDBC@Template");
            usersRepository.save(user1);
            Optional<User> us1 = usersRepository.findByEmail("JDBC@Template");
            us1.ifPresentOrElse(
                    (user2 -> System.out.println("user added success: " + user2)),
                    () -> System.out.println("user not found")
            );
            user1.setEmail("JDBC@TemplateUpdated");
            usersRepository.update(user1);
            System.out.println("user updated succefully: " + usersRepository.findById(user1.getId()));
            usersRepository.delete(user1.getId());
            System.out.println("user deleted succefully: " + user1);
            System.out.println("ALL USERS");
            List<User> users1 = usersRepository.findAll();
            for (User user2 : users1)
            {
                System.out.println(user2);
            }

        }catch (Exception e)
        {
           e.printStackTrace();
        }
    }

    
}
