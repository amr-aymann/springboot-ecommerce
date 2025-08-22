// SignupRequest.java
package com.AmrShop.dto;

/** Carries signup data from client → server */
public record SignupRequest(
    String username,
    String password
) {}

