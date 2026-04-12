package com.example.votingsystem.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;          // ✅ Integer not int (needed for session)

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email")
    private String email;

    @Column(name = "mobile_no")
    private String mobileNo;

    @Column(name = "voter_id")
    private String voterId;

    @Column(name = "aadhaar_no")
    private String aadhaarNo;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "state")
    private String state;

    @Column(name = "address")
    private String address;

    @Column(name = "password")
    private String password;

    @Column(name = "is_verified")
    private boolean isVerified;      // ✅ maps to is_verified in DB

    // ===== GETTERS =====
    public Integer getUserId() { return userId; }        // ✅ Integer return type
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getMobileNo() { return mobileNo; }
    public String getVoterId() { return voterId; }
    public String getAadhaarNo() { return aadhaarNo; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getState() { return state; }
    public String getAddress() { return address; }
    public String getPassword() { return password; }
    public boolean isVerified() { return isVerified; }

    // ===== SETTERS =====
    public void setUserId(Integer userId) { this.userId = userId; }  // ✅ Integer
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setMobileNo(String mobileNo) { this.mobileNo = mobileNo; }
    public void setVoterId(String voterId) { this.voterId = voterId; }
    public void setAadhaarNo(String aadhaarNo) { this.aadhaarNo = aadhaarNo; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public void setState(String state) { this.state = state; }
    public void setAddress(String address) { this.address = address; }
    public void setPassword(String password) { this.password = password; }
    public void setVerified(boolean verified) { this.isVerified = verified; }
}