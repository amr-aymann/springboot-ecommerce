package com.AmrShop.controller;

import com.AmrShop.dto.*;
import com.AmrShop.model.User; // your entity
import com.AmrShop.repository.UserRepository;
import com.AmrShop.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwt;
    private final PasswordEncoder encoder;
    private final UserRepository users;
    private final UserDetailsService userDetailsService; // DB one (@Primary)

    public AuthController(AuthenticationManager authManager,
                          JwtService jwt,
                          PasswordEncoder encoder,
                          UserRepository users,
                          UserDetailsService userDetailsService) {
        this.authManager = authManager;
        this.jwt = jwt;
        this.encoder = encoder;
        this.users = users;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest req) {
        if (users.findByUsername(req.username()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        var u = new User();
        u.setUsername(req.username());
        u.setEmail(req.email());
        u.setPassword(encoder.encode(req.password()));
        // set default role USER if needed
        u.addRoleIfMissing("USER");
        users.save(u);

        var token = jwt.generateAccessToken(u.getUsername(), Map.of("roles", u.rolesAsStrings()));
        var refresh = jwt.generateRefreshToken(u.getUsername());
        return new AuthResponse(token, refresh);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );
        var ud = userDetailsService.loadUserByUsername(auth.getName());

        var roles = ud.getAuthorities().stream().map(a -> a.getAuthority()).toList();
        var token = jwt.generateAccessToken(ud.getUsername(), Map.of("roles", roles));
        var refresh = jwt.generateRefreshToken(ud.getUsername());
        return new AuthResponse(token, refresh);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken == null || !jwt.isValid(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        String username = jwt.extractUsername(refreshToken);
        var ud = userDetailsService.loadUserByUsername(username);
        var roles = ud.getAuthorities().stream().map(a -> a.getAuthority()).toList();
        var access = jwt.generateAccessToken(username, Map.of("roles", roles));
        return new AuthResponse(access, refreshToken);
    }
}
