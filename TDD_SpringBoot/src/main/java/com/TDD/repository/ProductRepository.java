package com.TDD.repository;

import com.TDD.model.Product;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ProductRepository {

    public Optional<Product> findById(long id);
    public List<Product> findAll();
    public boolean update(Product product);
    public Product save(Product product);
    public boolean delete(Long id);
}
