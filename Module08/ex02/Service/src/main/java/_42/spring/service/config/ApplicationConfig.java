package _42.spring.service.config;



import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.zaxxer.hikari.HikariDataSource;

import _42.spring.service.repositories.UsersRepository;
import _42.spring.service.services.UsersService;


@Configuration
@PropertySource("classpath:db.properties") 
@ComponentScan (basePackageClasses = {
    UsersRepository.class,
    UsersService.class })
public class ApplicationConfig {

    @Value("${db.url}")
    private String jdbcUrl;

    @Value("${db.user}")
    private String userName;
    
    @Value("${db.password}")
    private String password;

    @Value("${db.driver.name}")
    private String driverName;

    @Bean
    public DataSource hikariDataSource()
    {
        HikariDataSource hs = new HikariDataSource();
        hs.setJdbcUrl(jdbcUrl);
        hs.setUsername(userName);
        hs.setPassword(password);
        hs.setDriverClassName(driverName);
        return hs;
    }

    @Bean
    public DataSource driverManagerDataSource(){
        DriverManagerDataSource dm = new DriverManagerDataSource();
        dm.setUrl(jdbcUrl);
        dm.setUsername(userName);
        dm.setPassword(password);
        dm.setDriverClassName(driverName);
        return dm;
    }
}
