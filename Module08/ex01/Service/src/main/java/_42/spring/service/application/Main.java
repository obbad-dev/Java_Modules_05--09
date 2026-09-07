package _42.spring.service.application;

import _42.spring.service.models.User;
import _42.spring.service.repositories.UsersRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String [] args) {

        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("context.xml")) {

            UsersRepository usersRepository;            
            System.out.println("=================== JDBC Implementation ==================");
            usersRepository = context.getBean("userRepositoryJdbc", UsersRepository.class);
            User user = context.getBean("userTest", User.class);

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
            usersRepository = context.getBean("userRepositoryTemplateJdbc", UsersRepository.class);
            user = context.getBean("userTest", User.class);
            System.out.println(user);
            usersRepository.save(user);
            Optional<User> us1 = usersRepository.findByEmail("JDBC@Template");
            us1.ifPresentOrElse(
                    (user2 -> System.out.println("user added success: " + user2)),
                    () -> System.out.println("user not found")
            );
            user.setEmail("JDBC@TemplateUpdated");
            usersRepository.update(user);
            System.out.println("user updated succefully: " + usersRepository.findById(user.getId()));
            usersRepository.delete(user.getId());
            System.out.println("user deleted succefully: " + user);
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
