package com.TDD;

import com.TDD.model.Product;
import com.TDD.repository.ProductRepository;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.DBUnitExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@ExtendWith({DBUnitExtension.class, SpringExtension.class})
@SpringBootTest
@ActiveProfiles("test")
public class ProductRepositoryTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ProductRepository productRepository;

    public ConnectionHolder getConnectionHolder() throws SQLException {
        // return a function that retrieves a connection from our data source
       Connection con = dataSource.getConnection();

       return (ConnectionHolder) con;
    }

    @Test
    @DataSet("products.yml")
    void testFindAll() {
        // when the product repo called, sql statements executed.
        // the return is from the test database
        List<Product> products = productRepository.findAll();
        Assertions.assertEquals(2, products.size(), "2 products in database ");
    }
}
