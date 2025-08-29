package com.AmrShop.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "wishlists")
public class Wishlist {


@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;


// wishlist always belongs to a user (you can extend to anonymous in future)
@Column(nullable = false)
private Long userId;


@OneToMany(mappedBy = "wishlist", cascade = CascadeType.ALL, orphanRemoval = true)
private List<WishlistItem> items = new ArrayList<>();


@Column(nullable = false, updatable = false)
private Instant createdAt = Instant.now();


// Getters/Setters
public Long getId() { return id; }
public void setId(Long id) { this.id = id; }
public Long getUserId() { return userId; }
public void setUserId(Long userId) { this.userId = userId; }
public List<WishlistItem> getItems() { return items; }
public void setItems(List<WishlistItem> items) { this.items = items; }
public Instant getCreatedAt() { return createdAt; }
}
