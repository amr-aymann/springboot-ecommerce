package com.AmrShop.controller;


import com.AmrShop.dto.CreateOrderDTO;
import com.AmrShop.dto.OrderDTO;
import com.AmrShop.model.OrderStatus;
import com.AmrShop.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/orders")
public class OrderController {


private final OrderService orderService;


public OrderController(OrderService orderService) {
this.orderService = orderService;
}


@PostMapping
public ResponseEntity<OrderDTO> create(@RequestBody CreateOrderDTO dto) {
OrderDTO created = orderService.create(dto);
return ResponseEntity.status(201).body(created);
}


@GetMapping("/{id}")
public ResponseEntity<OrderDTO> get(@PathVariable Long id) {
return ResponseEntity.ok(orderService.getById(id));
}


// Admin: update status
@PutMapping("/{id}/status")
public ResponseEntity<OrderDTO> updateStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
OrderDTO updated = orderService.updateStatus(id, status);
return ResponseEntity.ok(updated);
}
}