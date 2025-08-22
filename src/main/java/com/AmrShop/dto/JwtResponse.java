package com.AmrShop.dto;
// JwtResponse.java

/** Wraps the JWT we return after successful login */
public record JwtResponse(
    String token
) {}

