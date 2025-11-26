package com.digiscrib.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
public class DatabaseTestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/db-test")
    public String testDatabase() {
        try {
            // Simple query to test connection
            List<Map<String, Object>> result = jdbcTemplate.queryForList("SELECT 1 as test");
            return "Database connected! ✅ MySQL is working.";
        } catch (Exception e) {
            return "Database connection failed! ❌ Error: " + e.getMessage();
        }
    }
}