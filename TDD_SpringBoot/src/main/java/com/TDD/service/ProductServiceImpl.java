package com.TDD.service;

import com.TDD.model.Product;
import com.TDD.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class  ProductServiceImpl implements ProductService {
    @Autowired
  private ProductRepository productRepository;

    // if the product does not exist, it will return null;
    @Override
    public Optional<Product> findbyId(Long id) {
       return productRepository.findById(id);
    }
    // get all the products from the database
    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }


    @Override
    public boolean update(Product product) {
        return productRepository.update(product);
    }


    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Override
    public boolean delete(Long id) {
        return productRepository.delete(id);
    }
}
