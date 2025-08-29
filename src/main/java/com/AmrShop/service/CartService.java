package com.AmrShop.service;

import com.AmrShop.dto.AddCartItemDTO;
import com.AmrShop.dto.CartDTO;
import com.AmrShop.dto.CartItemDTO;
import com.AmrShop.exception.BadRequestException;
import com.AmrShop.exception.ResourceNotFoundException;
import com.AmrShop.model.Cart;
import com.AmrShop.model.CartItem;
import com.AmrShop.model.Product;
import com.AmrShop.repository.CartRepository;
import com.AmrShop.repository.CartItemRepository;
import com.AmrShop.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
            ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    private Cart findOrCreateCart(Long userId, String sessionId) {
        if (sessionId != null) {
            return cartRepository.findBySessionId(sessionId).orElseGet(() -> {
                Cart c = new Cart();
                c.setSessionId(sessionId);
                return cartRepository.save(c);
            });
        }
        throw new BadRequestException("Either userId or sessionId must be provided");
    }

    public CartDTO toDto(Cart c) {
        CartDTO dto = new CartDTO();
        dto.id = c.getId();
        dto.userId = c.getUserId();
        dto.sessionId = c.getSessionId();
        dto.items = c.getItems().stream().map(item -> {
            CartItemDTO it = new CartItemDTO();
            it.id = item.getId();
            it.productId = item.getProductId();
            it.productName = item.getProductNameSnapshot();
            it.unitPrice = item.getUnitPriceSnapshot();
            it.quantity = item.getQuantity();
            return it;
        }).collect(Collectors.toList());
        dto.total = c.getItems().stream()
                .map(i -> i.getUnitPriceSnapshot().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return dto;
    }

    @Transactional
    public CartDTO addItem(AddCartItemDTO dto) {
        if (dto.quantity == null || dto.quantity < 1)
            dto.quantity = 1;
        Cart cart = findOrCreateCart(dto.userId, dto.sessionId);

        Product p = productRepository.findById(dto.productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (!p.getActive())
            throw new BadRequestException("Product not available");
        if (p.getStock() < dto.quantity)
            throw new BadRequestException("Not enough stock");

        // find existing cart item
        Optional<CartItem> existing = cart.getItems().stream().filter(ci -> ci.getProductId().equals(p.getId()))
                .findFirst();
        if (existing.isPresent()) {
            CartItem ci = existing.get();
            ci.setQuantity(ci.getQuantity() + dto.quantity);
            cartItemRepository.save(ci);
        } else {
            CartItem ci = new CartItem();
            ci.setCart(cart);
            ci.setProductId(p.getId());
            ci.setProductNameSnapshot(p.getName());
            ci.setUnitPriceSnapshot(p.getPrice());
            ci.setQuantity(dto.quantity);
            cart.getItems().add(ci);
            cartRepository.save(cart);
        }

        return toDto(cart);
    }

    @Transactional
    public CartDTO updateItemQuantity(Long cartId, Long itemId, Integer quantity) {
        if (quantity == null || quantity < 1)
            throw new BadRequestException("Quantity must be >= 1");
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        CartItem item = cart.getItems().stream().filter(i -> i.getId().equals(itemId)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        // check stock
        Product p = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (p.getStock() < quantity)
            throw new BadRequestException("Not enough stock");

        item.setQuantity(quantity);
        cartItemRepository.save(item);
        return toDto(cart);
    }

    @Transactional
    public CartDTO removeItem(Long cartId, Long itemId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        CartItem item = cart.getItems().stream().filter(i -> i.getId().equals(itemId)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        return toDto(cart);
    }

    @Transactional
    public void clearCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public CartDTO getCart(Long userId, String sessionId) {
        Cart cart = null;
        if (userId != null)
            cart = cartRepository.findByUserId(userId).orElse(null);
        if (cart == null && sessionId != null)
            cart = cartRepository.findBySessionId(sessionId).orElse(null);
        if (cart == null)
            return null;
        return toDto(cart);
    }

    // Merge session cart into user cart after user logs in - to be called from auth success handler after login 
    @Transactional
    public CartDTO mergeSessionCartIntoUserCart(String sessionId, Long userId) {
        if (sessionId == null || userId == null)
            throw new BadRequestException("sessionId and userId required");
        Cart sessionCart = cartRepository.findBySessionId(sessionId).orElse(null);
        Cart userCart = cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart c = new Cart();
            c.setUserId(userId);
            return cartRepository.save(c);
        });

        if (sessionCart == null)
            return toDto(userCart);

        // merge items (accumulate quantities)
        for (CartItem sessionItem : sessionCart.getItems()) {
            Optional<CartItem> existing = userCart.getItems().stream()
                    .filter(ci -> ci.getProductId().equals(sessionItem.getProductId())).findFirst();
            if (existing.isPresent()) {
                CartItem ex = existing.get();
                ex.setQuantity(ex.getQuantity() + sessionItem.getQuantity());
                cartItemRepository.save(ex);
            } else {
                CartItem newItem = new CartItem();
                newItem.setCart(userCart);
                newItem.setProductId(sessionItem.getProductId());
                newItem.setProductNameSnapshot(sessionItem.getProductNameSnapshot());
                newItem.setUnitPriceSnapshot(sessionItem.getUnitPriceSnapshot());
                newItem.setQuantity(sessionItem.getQuantity());
                userCart.getItems().add(newItem);
            }
        }

        // delete session cart
        cartRepository.delete(sessionCart);
        cartRepository.save(userCart);
        return toDto(userCart);
    }

}
