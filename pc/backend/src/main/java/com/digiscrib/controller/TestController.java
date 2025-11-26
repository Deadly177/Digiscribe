package com.digiscrib.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    
    @GetMapping("/")
    public String home() {
        return "DigiScrib Server is running! ✅";
    }
    
    @GetMapping("/api/public/health")
    public String health() {
        return "Server is healthy!";
    }
    
    @GetMapping("/api/public/test")
    public String test() {
        return "Public test endpoint works!";
    }
}