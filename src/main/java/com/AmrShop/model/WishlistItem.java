package com.AmrShop.model;

import jakarta.persistence.*;
import java.math.BigDecimal;


@Entity
@Table(name = "wishlist_items")
public class WishlistItem {


@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;


@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "wishlist_id")
private Wishlist wishlist;


@Column(nullable = false)
private Long productId;


@Column(nullable = false)
private String productNameSnapshot;


@Column(nullable = false)
private BigDecimal unitPriceSnapshot;


// Getters/Setters
public Long getId() { return id; }
public void setId(Long id) { this.id = id; }
public Wishlist getWishlist() { return wishlist; }
public void setWishlist(Wishlist wishlist) { this.wishlist = wishlist; }
public Long getProductId() { return productId; }
public void setProductId(Long productId) { this.productId = productId; }
public String getProductNameSnapshot() { return productNameSnapshot; }
public void setProductNameSnapshot(String productNameSnapshot) { this.productNameSnapshot = productNameSnapshot; }
public BigDecimal getUnitPriceSnapshot() { return unitPriceSnapshot; }
public void setUnitPriceSnapshot(BigDecimal unitPriceSnapshot) { this.unitPriceSnapshot = unitPriceSnapshot; }
}
