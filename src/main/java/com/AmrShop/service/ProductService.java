package com.AmrShop.service;
import com.AmrShop.dto.CreateProductDTO;
import com.AmrShop.dto.ProductDTO;
import com.AmrShop.exception.ResourceNotFoundException;
import com.AmrShop.model.Product;
import com.AmrShop.model.ProductImage;
import com.AmrShop.repository.ProductImageRepository;
import com.AmrShop.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
private final ProductRepository productRepository;
private final ProductImageRepository imageRepository;

public ProductService(ProductRepository productRepository, ProductImageRepository imageRepository) {
this.productRepository = productRepository;
this.imageRepository = imageRepository;
}


public ProductDTO toDto(Product p) {
ProductDTO dto = new ProductDTO();
dto.id = p.getId();
dto.name = p.getName();
dto.slug = p.getSlug();
dto.description = p.getDescription();
dto.price = p.getPrice();
dto.stock = p.getStock();
dto.sku = p.getSku();
dto.active = p.getActive();
dto.images = p.getImages().stream().map(ProductImage::getUrl).collect(Collectors.toList());
return dto;
}


public ProductDTO getById(Long id) {
Product p = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
return toDto(p);
}


public List<ProductDTO> listAll() {
return productRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
}


@Transactional
public ProductDTO create(CreateProductDTO dto) {
Product p = new Product();
p.setName(dto.name);
p.setSlug(dto.slug);
p.setDescription(dto.description);
p.setPrice(dto.price);
p.setStock(dto.stock != null ? dto.stock : 0);
p.setSku(dto.sku);
p.setActive(dto.active != null ? dto.active : true);
if (dto.images != null) {
dto.images.forEach(url -> {
ProductImage img = new ProductImage();
img.setUrl(url);
img.setProduct(p);
p.getImages().add(img);
});
}
Product saved = productRepository.save(p);
return toDto(saved);
}


@Transactional
public ProductDTO update(Long id, CreateProductDTO dto) {
Product p = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
if (dto.name != null) p.setName(dto.name);
if (dto.slug != null) p.setSlug(dto.slug);
if (dto.description != null) p.setDescription(dto.description);
if (dto.price != null) p.setPrice(dto.price);
if (dto.stock != null) p.setStock(dto.stock);
if (dto.sku != null) p.setSku(dto.sku);
if (dto.active != null) p.setActive(dto.active);
if (dto.images != null) {
p.getImages().clear();
dto.images.forEach(url -> {
ProductImage img = new ProductImage();
img.setUrl(url);
img.setProduct(p);
p.getImages().add(img);
});
}
Product saved = productRepository.save(p);
return toDto(saved);
}


@Transactional
public void delete(Long id) {
Product p = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
productRepository.delete(p);
}
}