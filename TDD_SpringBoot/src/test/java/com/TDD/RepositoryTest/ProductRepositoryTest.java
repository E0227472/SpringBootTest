package com.TDD.RepositoryTest;

import com.TDD.model.Product;
import com.TDD.repository.ProductRepository;
import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.DBUnitExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({DBUnitExtension.class, SpringExtension.class})
@ActiveProfiles("test")
@SpringBootTest
public class ProductRepositoryTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ProductRepository productRepository;

    public ConnectionHolder connectionHolder = () -> dataSource.getConnection();


    @Test
    @DataSet("products.yml")
    void testFindAll() {
        // when the product repo called, sql statements executed.
        // the return is from the test database
        assertThat(productRepository).isNotNull();
        List<Product> products = productRepository.findAll();
        Assertions.assertEquals(2, products.size(), "2 products in database ");
    }

    @Test
    @DataSet("products.yml")
    void testFindByIdSuccess() {

        //find product with id 2
        Optional<Product> product = productRepository.findById(2);

        //validate that we found it
        Assertions.assertTrue(product.isPresent(), "Product with id 2 is found");

        // validate the product values
        Product p = product.get();
        Assertions.assertEquals(2, p.getId(), "id should be 2");
        Assertions.assertEquals("Product 2", p.getName(), "Product name should be product 2");
        Assertions.assertEquals(3, p.getQuantity(), "product quantity is 3");
        Assertions.assertEquals(2, p.getVersion(), "product version is 2");
    }

    @Test
    @DataSet("products.yml")
    void testFindByIdNotFound() {

        // find an id that does not exist, e.g 3
        Optional<Product> product = productRepository.findById(3);

        // validate that we found it
        Assertions.assertFalse(product.isPresent(), "Product is not not found");
    }

    @Test
    @DataSet("products.yml")
    void testSave() {
        // create new product and save it to the database
        Product product = new Product("Product 5", 5);
        product.setVersion(1);
        Product savedProduct = productRepository.save(product);

        //validate the saved product
        Assertions.assertEquals("Product 5", savedProduct.getName());
        Assertions.assertEquals(5, savedProduct.getQuantity());

        //Validate that we get it out of the database - once the product has been saved
        Optional<Product> loadedProduct = productRepository.findById(savedProduct.getId());
        Product _loadedProduct = loadedProduct.get();
        Assertions.assertTrue(loadedProduct.isPresent(), "Could not load product from database");
        Assertions.assertEquals("Product 5", _loadedProduct.getName(), "Load the product name");
        Assertions.assertEquals(5, _loadedProduct.getQuantity(), "Load the quantity");
        Assertions.assertEquals(1, _loadedProduct.getVersion(), "Load the version");
    }

    @Test
    @DataSet("products.yml")
    void testUpdateSuccess() {
        // update product 1's name, quantity and version
        Product product = new Product(1, "This is product 1", 100, 5);
        boolean result = productRepository.update(product);

        //validate product is returned by update
        Assertions.assertTrue(result, "product should be updated");

        //retrieve product 1 and validate if its updated with new data
        Optional<Product> loadedProduct = productRepository.findById(1);
        Product _loadedProduct = loadedProduct.get();
        Assertions.assertTrue(loadedProduct.isPresent(), "product is in database");
        Assertions.assertEquals("This is product 1", _loadedProduct.getName(), "Product name should exist");
        Assertions.assertEquals(100, _loadedProduct.getQuantity(), "Product quantity should match");
        Assertions.assertEquals(5, _loadedProduct.getVersion(), "Product version should match");
    }

    @Test
    @DataSet("products.yml")
    void testUpdateFailure() {
        // update product 1's name, quantity and version
        Product product = new Product(3, "This is product 3", 100, 5);
        boolean result = productRepository.update(product);

        //validate product is returned by update
        Assertions.assertFalse(result, "No such product exists in database");
    }

    @Test
    @DataSet("products.yml")
    void testDeleteSuccess() {
        boolean result = productRepository.delete((long) 1);
        Assertions.assertTrue(result, "Product is successfully deleted");

        //Validate that the product has been deleted
        Optional<Product> product = productRepository.findById(1);
        Assertions.assertFalse(product.isPresent(), "No such product exists");

    }

    @Test
    @DataSet("products.yml")
    void testDeleteFailure() {
        boolean result = productRepository.delete((long) 3);
        Assertions.assertFalse(result, "No such Product exists");

    }

}
