// ╔══════════════════════════════════════════════════════════════════════════════╗
// ║                          ActivityLog.java                                  ║
// ║                                                                              ║
// ║  This is a JPA ENTITY class — it represents a single row in the            ║
// ║  "activity_log" database table.                                             ║
// ║                                                                              ║
// ║  PURPOSE:                                                                    ║
// ║  Every important action performed by a voter or admin is recorded here.     ║
// ║  This creates an AUDIT TRAIL — a history of who did what and when.          ║
// ║                                                                              ║
// ║  Examples of logged events:                                                 ║
// ║   → Admin logged in          → Voter registered                            ║
// ║   → Voter cast a vote        → Admin verified a voter                      ║
// ║   → Duplicate vote attempt   → Election status changed                     ║
// ║                                                                              ║
// ║  Each row in the table stores:                                              ║
// ║   logId     → unique auto-generated ID for this log entry                  ║
// ║   userType  → who performed the action: "ADMIN" or "VOTER"                 ║
// ║   username  → name/ID of the person who acted                              ║
// ║   action    → short label of the action (e.g. "Vote Cast")                 ║
// ║   details   → longer description with context                              ║
// ║   ipAddress → IP address of the request (for traceability)                 ║
// ║   loggedAt  → exact date and time the action occurred                      ║
// ╚══════════════════════════════════════════════════════════════════════════════╝

// ─── Package Declaration ────────────────────────────────────────────────────────
// This file belongs to the "model" package — the layer that defines
// data structures (entities) that map directly to database tables.
package com.example.votingsystem.model;

// ─── Imports ───────────────────────────────────────────────────────────────────

// jakarta.persistence.* imports all JPA (Java Persistence API) annotations.
// JPA is the standard Java specification for ORM (Object-Relational Mapping).
// ORM means: Java class ↔ Database table, Java field ↔ Database column.
import jakarta.persistence.*;
//   @Entity          → marks this class as a DB-mapped entity
//   @Table           → specifies the exact DB table name to map to
//   @Id              → marks a field as the primary key
//   @GeneratedValue  → tells JPA how to auto-generate primary key values
//   @Column          → maps a field to a specific DB column (with custom name)

// LocalDateTime represents a date + time without timezone.
// Example: 2024-05-10T14:32:45
// Used here to record the exact timestamp when the log entry was created.
import java.time.LocalDateTime;


// ─── @Entity ───────────────────────────────────────────────────────────────────
// Tells JPA/Hibernate: "This Java class is a database entity."
// JPA will manage it — reading from and writing to the database automatically.
// Without this annotation, Spring would not treat this class as a DB table.
@Entity

// ─── @Table ────────────────────────────────────────────────────────────────────
// Maps this class to the database table named "activity_log".
// Without @Table, JPA would default to using the class name "ActivityLog"
// (or "activity_log" depending on the naming strategy configured).
// Being explicit here avoids ambiguity.
@Table(name = "activity_log")

public class ActivityLog {

    // ═══════════════════════════════════════════════════════════════════════════
    //  PRIMARY KEY FIELD
    // ═══════════════════════════════════════════════════════════════════════════

    // @Id marks this field as the PRIMARY KEY of the "activity_log" table.
    // Every table must have a primary key — a unique identifier for each row.
    @Id

    // @GeneratedValue tells JPA how to auto-generate the primary key value.
    // GenerationType.IDENTITY means:
    //   → The database itself generates the ID using AUTO_INCREMENT (MySQL/MariaDB)
    //     or SERIAL (PostgreSQL).
    //   → We never manually set this value — the DB assigns it on INSERT.
    //   → After saving, JPA reads back the generated ID and sets it on the object.
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // The actual Java field that holds the primary key value.
    // Integer (wrapper type) is used instead of int (primitive)
    // so it can hold null before the entity is persisted (saved) for the first time.
    // Once saved, JPA sets this to the auto-generated DB value (e.g. 1, 2, 3...).
    private Integer logId;


    // ═══════════════════════════════════════════════════════════════════════════
    //  DATA FIELDS (Each maps to one column in the "activity_log" table)
    // ═══════════════════════════════════════════════════════════════════════════

    // @Column(name = "user_type") maps this Java field to the DB column "user_type".
    // Without @Column, JPA would map it to a column named "userType" (camelCase),
    // which may not match the actual snake_case column name in the database.
    @Column(name = "user_type")

    // Stores who performed the action — either "ADMIN" or "VOTER".
    // This helps filter logs: admin can search only voter actions or only admin actions.
    // Set by LogService when .log() is called.
    private String userType;  // "ADMIN" or "VOTER"


    // Maps to the "username" column in the activity_log table.
    @Column(name = "username")

    // Stores the display name or identifier of the person who performed the action.
    // For VOTER: stores the voter's full name (e.g. "Rahul Sharma")
    // For ADMIN: stores "Admin" or the admin's username
    private String username;


    // Maps to the "action" column in the activity_log table.
    @Column(name = "action")

    // A short, human-readable label describing WHAT happened.
    // Examples: "Vote Cast", "Admin Login", "Voter Approved", "Duplicate Vote Attempt"
    // This is used to categorize log entries and display them in the admin panel.
    private String action;


    // Maps to the "details" column in the activity_log table.
    @Column(name = "details")

    // A longer description providing CONTEXT about the action.
    // Examples:
    //   "Voted in: General Election 2024 | Receipt: VOTE-1704892345678"
    //   "Approved voter: Rahul Sharma (ABC123)"
    //   "Admin logged in successfully"
    // This gives enough information to reconstruct exactly what happened.
    private String details;


    // Maps to the "ip_address" column in the activity_log table.
    @Column(name = "ip_address")

    // Stores the IP address of the HTTP request that triggered this action.
    // Captured from HttpServletRequest.getRemoteAddr() inside LogService.
    // Useful for detecting suspicious activity (e.g. multiple votes from same IP).
    // Example value: "192.168.1.105" or "::1" (localhost IPv6)
    private String ipAddress;


    // Maps to the "logged_at" column in the activity_log table.
    @Column(name = "logged_at")

    // Stores the exact date and time when this log entry was created.
    // LocalDateTime.now() is called as the DEFAULT VALUE right here at field declaration.
    // This means if setLoggedAt() is never called, the object is still initialized
    // with the current timestamp at the moment the ActivityLog object is created in Java.
    // Example value: 2024-05-10T14:32:45.123
    private LocalDateTime loggedAt = LocalDateTime.now();


    // ═══════════════════════════════════════════════════════════════════════════
    //  GETTERS
    // ═══════════════════════════════════════════════════════════════════════════
    //
    // Getters are PUBLIC methods that allow OTHER classes to READ the private fields.
    // In Java, fields are private (encapsulation) — direct access is not allowed.
    // Thymeleaf templates, LogService, repositories, and controllers all use
    // these getters to access the field values.
    //
    // Naming convention: get + FieldName (with capital first letter)
    //   logId → getLogId()
    //   userType → getUserType()
    // ═══════════════════════════════════════════════════════════════════════════

    // Returns the auto-generated primary key ID of this log entry.
    // Used internally by JPA; rarely needed in application code directly.
    public Integer getLogId() { return logId; }

    // Returns "ADMIN" or "VOTER" — who performed the logged action.
    // Used in admin.html to display the role badge next to each log entry.
    public String getUserType() { return userType; }

    // Returns the name/identifier of the person who performed the action.
    // Displayed in the "Actor" column of the activity log table in admin.html.
    public String getUsername() { return username; }

    // Returns the short action label (e.g. "Vote Cast", "Admin Login").
    // Displayed in the "Action" column of the activity log table.
    public String getAction() { return action; }

    // Returns the detailed description of what happened.
    // Displayed in the "Details" column of the activity log table.
    public String getDetails() { return details; }

    // Returns the IP address from which the action was performed.
    // Displayed in the "IP" column of the activity log table.
    public String getIpAddress() { return ipAddress; }

    // Returns the LocalDateTime timestamp when this log entry was created.
    // Displayed as the timestamp in the activity log table.
    // Thymeleaf formats it using: ${#temporals.format(log.loggedAt, 'dd-MM-yyyy HH:mm:ss')}
    public LocalDateTime getLoggedAt() { return loggedAt; }


    // ═══════════════════════════════════════════════════════════════════════════
    //  SETTERS
    // ═══════════════════════════════════════════════════════════════════════════
    //
    // Setters are PUBLIC methods that allow OTHER classes to WRITE to private fields.
    // LogService calls these setters to populate an ActivityLog object before saving it.
    //
    // Notice: There is NO setLogId() setter.
    // → logId is auto-generated by the database. We must NEVER manually set it.
    //   If we did, JPA might try to INSERT with a hardcoded ID, causing conflicts.
    //
    // Naming convention: set + FieldName (with capital first letter)
    //   userType → setUserType(String userType)
    // ═══════════════════════════════════════════════════════════════════════════

    // Sets the user type for this log entry.
    // Called by LogService: activityLog.setUserType("VOTER") or setUserType("ADMIN")
    // The parameter name "userType" shadows the field name — "this.userType" refers
    // to the class field, while "userType" alone refers to the method parameter.
    public void setUserType(String userType) { this.userType = userType; }

    // Sets the username/display name for this log entry.
    // Called by LogService with the voter's full name or admin's username.
    public void setUsername(String username) { this.username = username; }

    // Sets the action label for this log entry.
    // Called by LogService with values like "Vote Cast", "Admin Login", etc.
    public void setAction(String action) { this.action = action; }

    // Sets the detailed description of what happened for this log entry.
    // Called by LogService with context like receipt numbers, election names, etc.
    public void setDetails(String details) { this.details = details; }

    // Sets the IP address of the HTTP request that triggered this log entry.
    // Called by LogService after extracting IP from HttpServletRequest.getRemoteAddr().
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    // Sets the timestamp for this log entry.
    // Although loggedAt is already initialized to LocalDateTime.now() at field declaration,
    // this setter allows overriding it if needed (e.g. for testing with a fixed timestamp).
    public void setLoggedAt(LocalDateTime loggedAt) { this.loggedAt = loggedAt; }

} // ── End of ActivityLog ────────────────────────────────────────────────────────