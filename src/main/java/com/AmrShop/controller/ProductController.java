package com.AmrShop.controller;


import com.AmrShop.dto.CreateProductDTO;
import com.AmrShop.dto.ProductDTO;
import com.AmrShop.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/api/products")
public class ProductController {


private final ProductService productService;


public ProductController(ProductService productService) {
this.productService = productService;
}


@GetMapping
public ResponseEntity<List<ProductDTO>> list() {
return ResponseEntity.ok(productService.listAll());
}


@GetMapping("/{id}")
public ResponseEntity<ProductDTO> get(@PathVariable Long id) {
return ResponseEntity.ok(productService.getById(id));
}


// Admin endpoints (for simplicity not protected in skeleton)
@PostMapping
public ResponseEntity<ProductDTO> create(@RequestBody CreateProductDTO dto) {
ProductDTO created = productService.create(dto);
return ResponseEntity.status(201).body(created);
}


@PutMapping("/{id}")
public ResponseEntity<ProductDTO> update(@PathVariable Long id, @RequestBody CreateProductDTO dto) {
return ResponseEntity.ok(productService.update(id, dto));
}


@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id) {
productService.delete(id);
return ResponseEntity.noContent().build();
}
}