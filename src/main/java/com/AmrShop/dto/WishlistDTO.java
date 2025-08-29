package com.AmrShop.dto;

import java.math.BigDecimal;
import java.util.List;


public class WishlistDTO {
public Long id;
public Long userId;
public List<CartItemDTO> items; // reuse CartItemDTO shape for simplicity
}
