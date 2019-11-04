package com.TDD.repository;

import com.TDD.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Component
public class ProductRepositoryImpl implements ProductRepository {
    // JdbcTemplate is used for writing jpql queries
    @Autowired
    private JdbcTemplate jdbcTemplate;
    // for writing insert statements into the database
    private final SimpleJdbcInsert simpleJdbcInsert;

    // constructor
    public ProductRepositoryImpl(DataSource dataSource) {
        // enter the data-source into the constructor
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("Product")
                .usingGeneratedKeyColumns("id");
    }


    @Override
    public Optional<Product> findById(long id) {
        try{
            Product product = jdbcTemplate.queryForObject("SELECT * FROM Product WHERE id = ?",
                    new Object[] {id}, // id is passed from method
                    (rs, rowNum) -> {
                      Product p = new Product();
                      p.setId(rs.getLong("id"));
                      p.setName(rs.getString("name"));
                      p.setQuantity(rs.getInt("quantity"));
                      p.setVersion(rs.getInt("version"));
                      return p; // returning the product based on information from the database
                    });
            return Optional.of(product);
        } catch(EmptyResultDataAccessException e) { // if no such product id exists
            return Optional.empty();
        }
    } // end of find by id method

    @Override
    public List<Product> findAll() {
        return jdbcTemplate.query("SELECT * FROM Product",
                (rs, rowNumber) -> {
                   Product product = new Product();
                   product.setId(rs.getLong("id"));
                   product.setName(rs.getString("name"));
                   product.setQuantity(rs.getInt("quantity"));
                   product.setVersion(rs.getInt("version"));
                   return product;
                });
    } // end of findAll method

    @Override
    public boolean update(Product product) {
        // if the number of updated rows == 1, return true
        return jdbcTemplate.update("UPDATE Product SET name = ?, quantity = ?, version = ? WHERE id = ?",
                product.getName(),
                product.getQuantity(),
                product.getVersion(),
                product.getId()) == 1;
    }

    @Override
    public Product save(Product product) {
        // simpleJdbc insert saves the object as a map interface
        Map<String, Object> parameters = new HashMap<>();

        parameters.put("name", product.getName());
        parameters.put("quantity", product.getQuantity());
        parameters.put("version", product.getVersion());

        // Execute the query and get the generated Key - product id
        Number newId = simpleJdbcInsert.executeAndReturnKey(parameters);

        product.setId((Long) newId);
        return product;
    }

    @Override
    public boolean delete(Long id) {
        // if deletion of one row successful, return 1 which is truthsy statement.
        return jdbcTemplate.update("DELETE FROM Product WHERE id = ?",id) == 1;
    }
}
