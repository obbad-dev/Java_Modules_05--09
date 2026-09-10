package _42.spring.service.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import _42.spring.service.repositories.UsersRepository;
import _42.spring.service.services.*;

@Configuration 
@ComponentScan (basePackageClasses = {
    UsersRepository.class,
    UsersService.class 
    })

public class TestApplicationConfig {

    @Bean 
    public DataSource hikariDataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("schema.sql")
                .build();
    }
}
