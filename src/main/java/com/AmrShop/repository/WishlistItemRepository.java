package com.AmrShop.repository;


import com.AmrShop.model.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;


public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
}
