package com.TDD.service;

import com.TDD.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public interface ProductService {
    // get a product by its id
    Optional<Product> findbyId(Long id);

    // get all products from the database
    List<Product> findAll();

    //update the product
    boolean update(Product product);

    // save the product
    Product save(Product product);

    // delete the product
    boolean delete(Long id);
}
