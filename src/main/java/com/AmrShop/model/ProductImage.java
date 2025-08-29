package com.AmrShop.model;

import jakarta.persistence.*;


@Entity
@Table(name = "product_images")
public class ProductImage {


@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;


@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "product_id")
private Product product;


@Column(nullable = false)
private String url;


@Column
private String altText;


@Column
private Integer position = 0;


// Getters/Setters
public Long getId() { return id; }
public void setId(Long id) { this.id = id; }
public Product getProduct() { return product; }
public void setProduct(Product product) { this.product = product; }
public String getUrl() { return url; }
public void setUrl(String url) { this.url = url; }
public String getAltText() { return altText; }
public void setAltText(String altText) { this.altText = altText; }
public Integer getPosition() { return position; }
public void setPosition(Integer position) { this.position = position; }
}