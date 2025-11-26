package com.digiscrib.controller;

import com.digiscrib.dto.*;
import com.digiscrib.entity.User;
import com.digiscrib.service.UserService;
import com.digiscrib.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    // ===== ADDED GET ENDPOINTS FOR TESTING =====
    @GetMapping("/test")
    public String test() {
        return "Auth controller is working! ✅ - Server is running";
    }
    
    @GetMapping("/public/health")
    public String health() {
        return "Server health: OK - DigiScrib API is active";
    }
    
    @GetMapping("/status")
    public String status() {
        return "Authentication service is active and ready for requests";
    }
    
    @GetMapping("/info")
    public String info() {
        return "DigiScrib Authentication API - Use /login and /register endpoints";
    }
    // ===== END OF ADDED GET ENDPOINTS =====

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            
            User user = (User) authentication.getPrincipal();
            String token = jwtUtil.generateToken(user.getUsername());
            
            userService.updateLastLogin(user.getUsername());
            
            return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), user.getEmail(), user.getRole(), "Login successful"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse(null, null, null, null, "Invalid credentials"));
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            User user = userService.createUser(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                registerRequest.getPassword(),
                "USER"
            );
            
            String token = jwtUtil.generateToken(user.getUsername());
            
            return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), user.getEmail(), user.getRole(), "Registration successful"));
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new AuthResponse(null, null, null, null, e.getMessage()));
    }
}
}
