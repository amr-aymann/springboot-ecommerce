package com.AmrShop.dto;

import java.math.BigDecimal;
import java.util.List;


public class CartDTO {
public Long id;
public Long userId;
public String sessionId;
public List<CartItemDTO> items;
public BigDecimal total;
}
