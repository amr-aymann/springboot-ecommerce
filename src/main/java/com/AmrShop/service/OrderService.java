package com.AmrShop.service;

import java.math.BigDecimal;
import java.util.stream.Collectors;

import com.AmrShop.dto.CreateOrderDTO;
import com.AmrShop.dto.CreateOrderItemDTO;
import com.AmrShop.dto.OrderDTO;
import com.AmrShop.dto.OrderItemDTO;
import com.AmrShop.exception.BadRequestException;
import com.AmrShop.exception.ResourceNotFoundException;
import com.AmrShop.model.OrderItem;
import com.AmrShop.model.OrderStatus;
import com.AmrShop.model.Product;
import com.AmrShop.repository.OrderRepository;
import com.AmrShop.repository.ProductRepository;

import jakarta.transaction.Transactional;

import com.AmrShop.model.Order;

public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public OrderDTO toDto(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.id = order.getId();
        dto.customerId = order.getCustomerId();
        dto.status = order.getStatus();
        dto.totalAmount = order.getTotalAmount();
        dto.shippingAddressJson = order.getShippingAddressJson();
        dto.createdAt = order.getCreatedAt();
        dto.items = order.getItems().stream().map(item -> {
            OrderItemDTO it = new OrderItemDTO();
            it.productId = item.getProductId();
            it.productNameSnapshot = item.getProductNameSnapshot();
            it.unitPriceSnapshot = item.getUnitPriceSnapshot();
            it.quantity = item.getQuantity();
            return it;
        }).collect(Collectors.toList());
        return dto;
    }

    @Transactional
    public OrderDTO create(CreateOrderDTO dto) {
        if (dto.items == null || dto.items.isEmpty())
            throw new BadRequestException("Order must have at least one item");

        Order order = new Order();
        order.setCustomerId(dto.customerId != null ? dto.customerId : 0L);
        order.setShippingAddressJson(dto.shippingAddressJson);

        BigDecimal total = BigDecimal.ZERO;

        for (CreateOrderItemDTO it : dto.items) {
            Product p = productRepository.findById(it.productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + it.productId));
            if (p.getStock() < it.quantity)
                throw new BadRequestException("Not enough stock for product: " + p.getId());

            // decrement stock
            p.setStock(p.getStock() - it.quantity);
            productRepository.save(p);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(p.getId());
            orderItem.setProductNameSnapshot(p.getName());
            orderItem.setUnitPriceSnapshot(p.getPrice());
            orderItem.setQuantity(it.quantity);
            order.getItems().add(orderItem);

            total = total.add(p.getPrice().multiply(BigDecimal.valueOf(it.quantity.longValue())));
        }

        order.setTotalAmount(total);
        order.setStatus(OrderStatus.PENDING_PAYMENT);

        Order saved = orderRepository.save(order);
        return toDto(saved);
    }

    public OrderDTO getById(Long id) {
        return orderRepository.findById(id).map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    @Transactional
    public OrderDTO updateStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(status);
        Order saved = orderRepository.save(order);
        return toDto(saved);
    }

}
