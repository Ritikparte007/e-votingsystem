package com.example.votingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.votingsystem.model.ActivityLog;
import java.util.List;

/**
 * ActivityLogRepository — ActivityLog table ke liye database operations.
 *
 * JpaRepository<ActivityLog, Integer> extend karne se ye saari
 * methods automatically milti hain — koi SQL likhne ki zaroorat nahi:
 *
 * ┌──────────────────────────────────────────────────────────────────────────┐
 * │  METHOD                        │  SQL QUERY                             │
 * ├──────────────────────────────────────────────────────────────────────────┤
 * │  save(log)                     │  INSERT INTO activity_log              │
 * │  ← naya object                 │  (user_type, username, action,         │
 * │                                │   details, ip_address, logged_at)      │
 * │                                │  VALUES (?, ?, ?, ?, ?, ?)             │
 * ├──────────────────────────────────────────────────────────────────────────┤
 * │  save(log)                     │  UPDATE activity_log                   │
 * │  ← existing object (has ID)    │  SET user_type=?, username=?, ...      │
 * │                                │  WHERE log_id = ?                      │
 * ├──────────────────────────────────────────────────────────────────────────┤
 * │  findById(1)                   │  SELECT * FROM activity_log            │
 * │                                │  WHERE log_id = 1                      │
 * ├──────────────────────────────────────────────────────────────────────────┤
 * │  findAll()                     │  SELECT * FROM activity_log            │
 * ├──────────────────────────────────────────────────────────────────────────┤
 * │  deleteById(1)                 │  DELETE FROM activity_log              │
 * │                                │  WHERE log_id = 1                      │
 * ├──────────────────────────────────────────────────────────────────────────┤
 * │  count()                       │  SELECT COUNT(*)                       │
 * │                                │  FROM activity_log                     │
 * ├──────────────────────────────────────────────────────────────────────────┤
 * │  existsById(1)                 │  SELECT COUNT(*) FROM activity_log     │
 * │                                │  WHERE log_id = 1                      │
 * ├──────────────────────────────────────────────────────────────────────────┤
 * │  deleteAll()                   │  DELETE FROM activity_log              │
 * └──────────────────────────────────────────────────────────────────────────┘
 */
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Integer> {
    //                                                        ^           ^
    //                                                    Entity Type   Primary Key Type (logId)

    /**
     * Latest 50 log entries fetch karta hai, sabse naya pehle.
     *
     * Method naam se Spring khud SQL bana leta hai:
     *   findTop50       → LIMIT 50
     *   OrderByLoggedAt → ORDER BY logged_at
     *   Desc            → DESC (newest first)
     *
     * SQL:
     *   SELECT * FROM activity_log
     *   ORDER BY logged_at DESC
     *   LIMIT 50
     *
     * Use: HomeController → admin dashboard pe last 50 actions dikhane ke liye
     *   model.addAttribute("logs", activityLogRepository.findTop50ByOrderByLoggedAtDesc());
     */
    List<ActivityLog> findTop50ByOrderByLoggedAtDesc();
}