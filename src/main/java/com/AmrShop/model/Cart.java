package com.AmrShop.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "carts")
public class Cart {


@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;


// Either userId (for logged-in users) OR sessionId (for anonymous sessions)
private Long userId;


@Column(unique = true)
private String sessionId;


@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
private List<CartItem> items = new ArrayList<>();


@Column(nullable = false, updatable = false)
private Instant createdAt = Instant.now();


// Getters / Setters
public Long getId() { return id; }
public void setId(Long id) { this.id = id; }
public Long getUserId() { return userId; }
public void setUserId(Long userId) { this.userId = userId; }
public String getSessionId() { return sessionId; }
public void setSessionId(String sessionId) { this.sessionId = sessionId; }
public List<CartItem> getItems() { return items; }
public void setItems(List<CartItem> items) { this.items = items; }
public Instant getCreatedAt() { return createdAt; }
}
