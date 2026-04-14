package com.example.votingsystem.model;

import jakarta.persistence.*;
import java.time.LocalDate;

import com.example.votingsystem.config.AesEncryptor;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email")
    private String email;

    @Column(name = "mobile_no")
    private String mobileNo;

    @Convert(converter = AesEncryptor.class)
    @Column(name = "voter_id")
    private String voterId;

    @Convert(converter = AesEncryptor.class)
    @Column(name = "aadhaar_no")
    private String aadhaarNo;

    @Column(name = "aadhaar_image")        
    private String aadhaarImage;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "state")
    private String state;

    @Column(name = "address")
    private String address;

    @Column(name = "password")
    private String password;

    @Column(name = "is_verified")
    private boolean isVerified;

    // ===== GETTERS =====
    public Integer getUserId()           { return userId; }
    public String getFullName()          { return fullName; }
    public String getEmail()             { return email; }
    public String getMobileNo()          { return mobileNo; }
    public String getVoterId()           { return voterId; }
    public String getAadhaarNo()         { return aadhaarNo; }
    public String getAadhaarImage()      { return aadhaarImage; }   // ✅ NEW
    public LocalDate getDateOfBirth()    { return dateOfBirth; }
    public String getState()             { return state; }
    public String getAddress()           { return address; }
    public String getPassword()          { return password; }
    public boolean isVerified()          { return isVerified; }

    // ===== SETTERS =====
    public void setUserId(Integer userId)              { this.userId = userId; }
    public void setFullName(String fullName)           { this.fullName = fullName; }
    public void setEmail(String email)                 { this.email = email; }
    public void setMobileNo(String mobileNo)           { this.mobileNo = mobileNo; }
    public void setVoterId(String voterId)             { this.voterId = voterId; }
    public void setAadhaarNo(String aadhaarNo)         { this.aadhaarNo = aadhaarNo; }
    public void setAadhaarImage(String aadhaarImage)   { this.aadhaarImage = aadhaarImage; } // ✅ NEW
    public void setDateOfBirth(LocalDate dateOfBirth)  { this.dateOfBirth = dateOfBirth; }
    public void setState(String state)                 { this.state = state; }
    public void setAddress(String address)             { this.address = address; }
    public void setPassword(String password)           { this.password = password; }
    public void setVerified(boolean verified)          { this.isVerified = verified; }
}