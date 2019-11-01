package com.TDD;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.DriverManager;

@Configuration
@Profile("test") // set the bean scope to work in test envt
public class ProductRepositoryTestConfiguration {

    @Primary
    @Bean
    public DataSource dataSource() {
        // setup of data source for the tests - setting up mock database for testing
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY =-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("");

        return dataSource;
    }
}
