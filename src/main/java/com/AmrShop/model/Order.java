package com.AmrShop.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "orders")
public class Order {


@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;


@Column(nullable = false)
private Long customerId; // lightweight reference; adjust to relation if you implement User entity fully


@Enumerated(EnumType.STRING)
private OrderStatus status = OrderStatus.CREATED;


@Column(nullable = false)
private BigDecimal totalAmount = BigDecimal.ZERO;


@Column(columnDefinition = "TEXT")
private String shippingAddressJson;


@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
private List<OrderItem> items = new ArrayList<>();


@Column(nullable = false, updatable = false)
private Instant createdAt = Instant.now();


// Getters/Setters
public Long getId() { return id; }
public void setId(Long id) { this.id = id; }
public Long getCustomerId() { return customerId; }
public void setCustomerId(Long customerId) { this.customerId = customerId; }
public OrderStatus getStatus() { return status; }
public void setStatus(OrderStatus status) { this.status = status; }
public BigDecimal getTotalAmount() { return totalAmount; }
public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
public String getShippingAddressJson() { return shippingAddressJson; }
public void setShippingAddressJson(String shippingAddressJson) { this.shippingAddressJson = shippingAddressJson; }
public List<OrderItem> getItems() { return items; }
public void setItems(List<OrderItem> items) { this.items = items; }
public Instant getCreatedAt() { return createdAt; }
}
