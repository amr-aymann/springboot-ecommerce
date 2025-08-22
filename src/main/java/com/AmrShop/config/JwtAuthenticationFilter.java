package com.AmrShop.config;

import com.AmrShop.service.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Still under development 
        String token = header.substring(7);
        // try {
        //     Jws<Claims> claimsJws = jwtUtil.validateToken(token);
        //     Claims claims = claimsJws.getBody();
        //     String username = claims.getSubject();
        //     List<String> roles = claims.get("roles", List.class);

        //     if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        //         UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        //         if (jwtUtil.isToken Valid(token, userDetails)) {
        //             UsernamePasswordAuthenticationToken authToken =
        //                 new UsernamePasswordAuthenticationToken(
        //                     userDetails, null, userDetails.getAuthorities());
        //             authToken.setDetails(
        //                 new WebAuthenticationDetailsSource().buildDetails(request)
        //             );
        //             SecurityContextHolder.getContext().setAuthentication(authToken);
        //         }
        //     }
        // } catch (Exception e) {
        //     logger.warn("JWT authentication failed: {}", e.getMessage());
        // }

        filterChain.doFilter(request, response);
    }
}
