package com.AmrShop.repository;

import com.AmrShop.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
Optional<Wishlist> findByUserId(Long userId);
}
