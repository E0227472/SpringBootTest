package com.TDD.repository;

import com.TDD.model.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
@Component
public class ProductRepositoryImpl implements ProductRepository {
    @Override
    public Optional<Product> findById(long id) {
        return Optional.empty();
    }

    @Override
    public List<Product> findAll() {
        return null;
    }

    @Override
    public boolean update(Product product) {
        return false;
    }

    @Override
    public Product save(Product product) {
        return null;
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }
}
