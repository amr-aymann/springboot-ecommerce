package com.AmrShop.dto;
// LoginRequest.java

/** Carries login credentials from client → server */
public record LoginRequest(String username, String password) {}

