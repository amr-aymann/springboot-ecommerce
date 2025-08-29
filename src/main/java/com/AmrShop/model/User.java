package com.AmrShop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password; // will be BCrypt’d

    private String email;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    // ✅ Helper method to safely add a role
    public void addRoleIfMissing(Role role) {
        if (!roles.contains(role)) {
            roles.add(role);
        }
    }

    public void addRoleIfMissing(String roleStr) {
        try {
            Role role = Role.valueOf(roleStr);
            addRoleIfMissing(role);
        } catch (IllegalArgumentException ex) {
            // Invalid role string, ignore or log
        }
    }

    // inside User.java

    public Set<String> rolesAsStrings() {
        return roles.stream()
                .map(Enum::name) // convert each Role enum to its String name
                .collect(java.util.stream.Collectors.toSet());
    }

}
