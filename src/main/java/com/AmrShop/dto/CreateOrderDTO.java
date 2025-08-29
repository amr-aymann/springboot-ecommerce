package com.AmrShop.dto;

import java.util.List;


public class CreateOrderDTO {
public Long customerId;
public String shippingAddressJson; // lightweight for skeleton
public List<CreateOrderItemDTO> items;
}