package _42.spring.service.application;

import _42.spring.service.config.ApplicationConfig;
import _42.spring.service.repositories.UsersRepository;


import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String [] args) {

        try (AnnotationConfigApplicationContext context = 
            new AnnotationConfigApplicationContext(ApplicationConfig.class)
        ) {
            UsersRepository usersRepository = context.getBean("usersRepositoryJdbc", UsersRepository.class);
            System.out.println(usersRepository.findAll());
            usersRepository = context.getBean("usersRepositoryJdbcTemplate", UsersRepository.class);
            System.out.println(usersRepository.findAll());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }    
}
