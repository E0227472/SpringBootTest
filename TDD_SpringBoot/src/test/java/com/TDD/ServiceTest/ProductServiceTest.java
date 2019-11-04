package com.TDD.ServiceTest;

import com.TDD.model.Product;
import com.TDD.repository.ProductRepository;
import com.TDD.service.ProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ProductServiceTest {

    @Autowired
    ProductService productService;

    @MockBean
    ProductRepository productRepository;

    @Test
    @DisplayName("Test findbyId Success")
    void testFindByIdSuccess() {
        // actual product
        Product mockProduct = new Product(1, "Product Name", 10, 1);

        // Expected product when the respository / service  layer is called
        doReturn(Optional.of(mockProduct)).when(productRepository).findById(1);
        Optional<Product> returnedProduct = productService.findbyId((long) 1);

        // Assert the response
        // In assertTrue, a true result should be returned
        Assertions.assertTrue(returnedProduct.isPresent(), "Product was not found");
        // in assertSame, the expected and actual product should match
        Assertions.assertSame(returnedProduct.get(), mockProduct, "Products should be the same");
    } // end of find id success method

    @Test
    @DisplayName("Test findbyId - not found")
    void testFindByIdNotFound() {

        // Expected product when the respository / service  layer is called
        doReturn(Optional.empty()).when(productRepository).findById(1);
        Optional<Product> returnedProduct = productService.findbyId((long) 1);

        // Assert the response
        // In assertFalse, a false result is expected
        Assertions.assertFalse(returnedProduct.isPresent(), "Product was found, when it should'nt be");

    } // end of find id success method

    @Test
    @DisplayName("Test findAll - Success")
    void testFindAllSuccess() {
        // create products
        Product mockProduct1 = new Product(1, "Product Name", 10, 1);
        Product mockProduct2 = new Product(2, "Product name 1", 10, 2);
        Product mockProduct3 = new Product(3, "Product name 2", 10, 3);

        // add products to a list
        List<Product> productList = new LinkedList<>();
        productList.add(mockProduct1);
        productList.add(mockProduct2);
        productList.add(mockProduct3);

        // When product respository called, return the product list with the products
        doReturn(productList).when(productRepository).findAll();

        List<Product> prodtList = productService.findAll();

        // Assert if the response is the same
        Assertions.assertSame(prodtList, productList, "Successful find");
    } // end of find all success test

    @Test
    @DisplayName("Test save - successful")
    void testSaveSuccessful() {
        // mock product
        Product mockProduct = new Product(1, "Product Name", 10, 1);
        // product repository returns mock product when called upon
        doReturn(mockProduct).when(productRepository).save(any());

        Product returnedProduct = productService.save(mockProduct);
        // for objs, compare the properties to see if they are equal
        Assertions.assertSame(returnedProduct.getVersion(), mockProduct.getVersion(), "Products should be the same");
    }




}
