package com.AmrShop.repository;


import com.AmrShop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface ProductRepository extends JpaRepository<Product, Long> {
Optional<Product> findBySlug(String slug);
}
