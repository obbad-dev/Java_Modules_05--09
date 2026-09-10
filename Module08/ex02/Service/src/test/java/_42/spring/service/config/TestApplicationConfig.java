package _42.spring.service.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import _42.spring.service.repositories.UsersRepositoryJdbcTemplateImpl;
import _42.spring.service.services.UsersServiceImpl;

@Configuration 
// @ComponentScan (baseClasse= {
//     UsersRepository.class,
//     UsersService.class 
//     })
@Import({UsersRepositoryJdbcTemplateImpl.class, UsersServiceImpl.class})
public class TestApplicationConfig {

    @Bean 
    public DataSource hikariDataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setName("testdb;MODE=PostgreSQL")
                .addScript("schema.sql")
                .build();
    }

}
