package com.AmrShop.service;


import com.AmrShop.dto.AddCartItemDTO;
import com.AmrShop.dto.AddWishlistItemDTO;
import com.AmrShop.dto.CartItemDTO;
import com.AmrShop.dto.WishlistDTO;
import com.AmrShop.exception.ResourceNotFoundException;
import com.AmrShop.model.Product;
import com.AmrShop.model.Wishlist;
import com.AmrShop.model.WishlistItem;
import com.AmrShop.repository.ProductRepository;
import com.AmrShop.repository.WishlistRepository;

import java.util.Optional;
import java.util.Collections;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.stream.Collectors;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final CartService cartService; // used for moving items from wishlist -> cart

    public WishlistService(WishlistRepository wishlistRepository,
                           ProductRepository productRepository,
                           CartService cartService) {
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }

    /**
     * Add a product to the user's wishlist. If wishlist doesn't exist, create it.
     * Prevents duplicates.
     */
    @Transactional
    public WishlistDTO addItem(AddWishlistItemDTO dto) {
        Wishlist wishlist = wishlistRepository.findByUserId(dto.userId).orElseGet(() -> {
            Wishlist w = new Wishlist();
            w.setUserId(dto.userId);
            return wishlistRepository.save(w);
        });

        boolean exists = wishlist.getItems().stream().anyMatch(i -> i.getProductId().equals(dto.productId));
        if (exists) return toDto(wishlist);

        Product p = productRepository.findById(dto.productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        WishlistItem wi = new WishlistItem();
        wi.setWishlist(wishlist);
        wi.setProductId(p.getId());
        wi.setProductNameSnapshot(p.getName());
        wi.setUnitPriceSnapshot(p.getPrice());
        wishlist.getItems().add(wi);

        wishlistRepository.save(wishlist);
        return toDto(wishlist);
    }

    /**
     * Remove a wishlist item by productId for the given user.
     */
    @Transactional
    public WishlistDTO removeItem(Long userId, Long productId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("Wishlist not found"));
        boolean removed = wishlist.getItems().removeIf(i -> i.getProductId().equals(productId));
        if (removed) wishlistRepository.save(wishlist);
        return toDto(wishlist);
    }

    /**
     * Remove an item by the wishlist-item id.
     */
    @Transactional
    public WishlistDTO removeItemById(Long userId, Long wishlistItemId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("Wishlist not found"));
        boolean removed = wishlist.getItems().removeIf(i -> i.getId().equals(wishlistItemId));
        if (removed) wishlistRepository.save(wishlist);
        return toDto(wishlist);
    }

    /**
     * Clear all items from the user's wishlist.
     */
    @Transactional
    public WishlistDTO clearWishlist(Long userId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("Wishlist not found"));
        wishlist.getItems().clear();
        wishlistRepository.save(wishlist);
        return toDto(wishlist);
    }

    /**
     * Move a wishlist item (product) to the user's cart.
     * - Adds an item to the user's cart (via CartService) with quantity = 1 (can be adjusted)
     * - Removes the item from wishlist if move succeeds
     */
    @Transactional
    public WishlistDTO moveItemToCart(Long userId, Long productId, Integer quantity) {
        if (quantity == null || quantity < 1) quantity = 1;

        Wishlist wishlist = wishlistRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("Wishlist not found"));

        // ensure item exists in wishlist
        Optional<WishlistItem> maybeItem = wishlist.getItems().stream().filter(i -> i.getProductId().equals(productId)).findFirst();
        if (!maybeItem.isPresent()) throw new ResourceNotFoundException("Wishlist item not found");

        // validate product exists & is available
        Product p = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (!p.getActive()) throw new IllegalStateException("Product not available");
        if (p.getStock() < quantity) throw new IllegalStateException("Not enough stock to move to cart");

        // Add to cart (CartService expects userId or sessionId)
        AddCartItemDTO addToCart = new AddCartItemDTO();
        addToCart.productId = productId;
        addToCart.quantity = quantity;
        addToCart.userId = userId;
        cartService.addItem(addToCart);

        // Remove from wishlist
        wishlist.getItems().removeIf(i -> i.getProductId().equals(productId));
        wishlistRepository.save(wishlist);

        return toDto(wishlist);
    }

    /**
     * Fetch wishlist for a user as DTO
     */
    public WishlistDTO getByUserId(Long userId) {
        return wishlistRepository.findByUserId(userId).map(this::toDto).orElse(null);
    }

    private WishlistDTO toDto(Wishlist w) {
    WishlistDTO dto = new WishlistDTO();
    dto.id = w.getId();
    dto.userId = w.getUserId();

    // guard against null items (defensive) and map to CartItemDTO
    dto.items = Optional.ofNullable(w.getItems())
            .orElseGet(Collections::emptyList)
            .stream()
            .map(i -> {
                CartItemDTO it = new CartItemDTO();
                it.id = i.getId();
                it.productId = i.getProductId();
                it.productName = i.getProductNameSnapshot();
                it.unitPrice = i.getUnitPriceSnapshot();
                it.quantity = 1; // wishlist items don't have quantity; default to 1
                return it;
            })
            .collect(Collectors.toList());

    return dto;
}

}

