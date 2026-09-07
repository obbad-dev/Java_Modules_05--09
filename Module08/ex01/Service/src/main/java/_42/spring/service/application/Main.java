package _42.spring.service.application;

import _42.spring.service.models.User;
import _42.spring.service.repositories.UsersRepository;

import java.util.List;
import java.util.Optional;

// import org.springframework.context.ApplicationContext; // not auto-closeable
import org.springframework.context.support.ClassPathXmlApplicationContext;

// ApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
// UsersRepository usersRepository = context.getBean("usersRepositoryJdbc", UsersRepository.class);
// System.out.println(usersRepository.findAll());
// usersRepository = context.getBean("usersRepositoryJdbcTemplate", UsersRepository.class);
// System.out.println(usersRepository.findAll());
public class Main {
    public static void main(String [] args) {


        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("context.xml")) {

            UsersRepository usersRepository;            
            System.out.println("=================== JDBC Implementation ==================");
            usersRepository = context.getBean("usersRepositoryJdbc", UsersRepository.class);
            User user = new User(null, "JDBC@Template");

            usersRepository.save(user);
            Optional<User> us = usersRepository.findByEmail("JDBC@Template");
            us.ifPresentOrElse(
                    (user1 -> System.out.println("user added success: " + user1)),
                    () -> System.out.println("user not found")
            );

            user.setEmail("JDBC@TemplateUpdated");
            usersRepository.update(user);
            System.out.println("user updated succefully: " + usersRepository.findById(user.getId()));
            usersRepository.delete(user.getId());
            System.out.println("user deleted succefully: " + user);
            System.out.println("ALL USERS");
            List<User> users = usersRepository.findAll();
            for (User user1 : users)
            {
                System.out.println(user1);
            }

            System.out.println("=================== JDBC Template Implementation ==================");
            usersRepository = context.getBean("usersRepositoryJdbcTemplate", UsersRepository.class);
            user = new User(null, "JDBC@Template");
            usersRepository.save(user );
            Optional<User> us1 = usersRepository.findByEmail("JDBC@Template");
            us1.ifPresentOrElse(
                    (u -> System.out.println("user added success: " + u)),
                    () -> System.out.println("user not found")
            );
            user.setEmail("JDBC@Templateupdated");
            usersRepository.update(user);
            System.out.println("user updated succefully: " + usersRepository.findById(user.getId()));
            usersRepository.delete(user.getId());
            System.out.println("user deleted succefully: " + user);
            System.out.println("ALL USERS");
            users = usersRepository.findAll();
            for (User usr : users)
            {
                System.out.println(usr);
            }

        }catch (Exception e)
        {
           e.printStackTrace();
        }
    }    
}
