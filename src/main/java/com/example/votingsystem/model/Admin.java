package com.example.votingsystem.model;

import jakarta.persistence.*;

@Entity
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer adminId;

    private String username;
    private String password;

    // ===== GETTERS =====
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // ===== SETTERS =====
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}