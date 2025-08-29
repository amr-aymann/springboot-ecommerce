package com.AmrShop.dto;

public class AddCartItemDTO {
public Long productId;
public Integer quantity;
public Long userId; // optional, if logged in
public String sessionId; // optional, for anonymous
}