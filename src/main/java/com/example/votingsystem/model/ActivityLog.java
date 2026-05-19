
package com.example.votingsystem.model;

// ─── Imports ───────────────────────────────────────────────────────────────────
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_log")

public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer logId;
    @Column(name = "user_type")
    private String userType; 
    @Column(name = "username")
    private String username;


    // Maps to the "action" column in the activity_log table.
    @Column(name = "action")
    private String action;


    // Maps to the "details" column in the activity_log table.
    @Column(name = "details")
    private String details;


    // Maps to the "ip_address" column in the activity_log table.
    @Column(name = "ip_address")
    private String ipAddress;


    // Maps to the "logged_at" column in the activity_log table.
    @Column(name = "logged_at")
    private LocalDateTime loggedAt = LocalDateTime.now();

    public Integer getLogId() { return logId; }
    public String getUserType() { return userType; }

    public String getUsername() { return username; }
    public String getAction() { return action; }

    public String getDetails() { return details; }

    public String getIpAddress() { return ipAddress; }
    public LocalDateTime getLoggedAt() { return loggedAt; }
    
    public void setUserType(String userType) { this.userType = userType; }

    public void setUsername(String username) { this.username = username; }

    public void setAction(String action) { this.action = action; }

    public void setDetails(String details) { this.details = details; }

    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public void setLoggedAt(LocalDateTime loggedAt) { this.loggedAt = loggedAt; }

} // ── End of ActivityLog ────────────────────────────────────────────────────────