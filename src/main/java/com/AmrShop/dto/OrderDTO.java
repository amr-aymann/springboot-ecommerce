package com.AmrShop.dto;

import com.AmrShop.model.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;


public class OrderDTO {
public Long id;
public Long customerId;
public OrderStatus status;
public BigDecimal totalAmount;
public String shippingAddressJson;
public List<OrderItemDTO> items;
public Instant createdAt;
}