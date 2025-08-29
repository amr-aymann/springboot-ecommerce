package com.AmrShop.repository;

import com.AmrShop.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderRepository extends JpaRepository<Order, Long> {
}