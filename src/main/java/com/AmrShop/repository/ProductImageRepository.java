package com.AmrShop.repository;

import com.AmrShop.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}