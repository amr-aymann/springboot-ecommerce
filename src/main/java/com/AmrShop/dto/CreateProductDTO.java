package com.AmrShop.dto;

import java.math.BigDecimal;
import java.util.List;


public class CreateProductDTO {
public String name;
public String slug;
public String description;
public BigDecimal price;
public Integer stock;
public String sku;
public Boolean active;
public List<String> images; // urls
}
