package com.AmrShop.controller;
import com.AmrShop.config.JwtUtil;
import com.AmrShop.dto.JwtResponse;
import com.AmrShop.dto.LoginRequest;
import com.AmrShop.dto.SignupRequest;
import com.AmrShop.model.Role;
import com.AmrShop.model.User;
import com.AmrShop.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(AuthenticationManager authManager,
                          UserRepository userRepo,
                          JwtUtil jwtUtil) {
        this.authManager = authManager;
        this.userRepo    = userRepo;
        this.jwtUtil     = jwtUtil;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest req) {
        if (userRepo.existsByUsername(req.username())) {
            return ResponseEntity.badRequest().body("Error: Username is already taken");
        }
        User user = new User();
        user.setUsername(req.username());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setRoles(Set.of(Role.USER));
        userRepo.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        Authentication auth = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );
        var principal = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        Set<String> roles = principal.getAuthorities().stream()
            .map(a -> a.getAuthority())
            .collect(Collectors.toSet());
        String token = jwtUtil.generateToken(principal.getUsername(), roles);
        return ResponseEntity.ok(new JwtResponse(token));
    }
}