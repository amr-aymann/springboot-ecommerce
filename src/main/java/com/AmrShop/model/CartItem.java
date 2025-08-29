package com.AmrShop.model;
import jakarta.persistence.*;
import java.math.BigDecimal;


@Entity
@Table(name = "cart_items")
public class CartItem {


@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;


@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "cart_id")
private Cart cart;


@Column(nullable = false)
private Long productId;


@Column(nullable = false)
private String productNameSnapshot;


@Column(nullable = false)
private BigDecimal unitPriceSnapshot;


@Column(nullable = false)
private Integer quantity;


// Getters/Setters
public Long getId() { return id; }
public void setId(Long id) { this.id = id; }
public Cart getCart() { return cart; }
public void setCart(Cart cart) { this.cart = cart; }
public Long getProductId() { return productId; }
public void setProductId(Long productId) { this.productId = productId; }
public String getProductNameSnapshot() { return productNameSnapshot; }
public void setProductNameSnapshot(String productNameSnapshot) { this.productNameSnapshot = productNameSnapshot; }
public BigDecimal getUnitPriceSnapshot() { return unitPriceSnapshot; }
public void setUnitPriceSnapshot(BigDecimal unitPriceSnapshot) { this.unitPriceSnapshot = unitPriceSnapshot; }
public Integer getQuantity() { return quantity; }
public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
