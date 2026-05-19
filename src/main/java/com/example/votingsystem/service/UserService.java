package com.example.votingsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.votingsystem.model.Admin;
import com.example.votingsystem.model.User;
import com.example.votingsystem.repository.AdminRepository;
import com.example.votingsystem.repository.UserRepository;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private AdminRepository adminRepository;
    @Autowired private BCryptPasswordEncoder passwordEncoder;

    // ─── Admin Login Check ────────────────────────────────────────────────────
    public Admin findAdmin(String username) {
        return adminRepository.findByUsername(username).orElse(null);
    }

    // ─── Voter Login — VoterId ya Email dono se login ho sake ────────────────
    public User findVoterByIdOrEmail(String loginId) {
        return userRepository.findByVoterId(loginId)
                .orElseGet(() -> userRepository.findByEmail(loginId).orElse(null));
    }

    // ─── Registration Validations ─────────────────────────────────────────────
    public boolean isVoterIdTaken(String voterId) {
        return userRepository.findByVoterId(voterId).isPresent();
    }

    public boolean isEmailTaken(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    // ─── Aadhaar Image — Secure Save ─────────────────────────────────────────
    public String saveAadhaarImage(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Aadhaar image is required!");
        }

       
        String contentType = file.getContentType();
        if (contentType == null ||
            (!contentType.equals("image/jpeg") &&
             !contentType.equals("image/png")  &&
             !contentType.equals("image/jpg"))) {
            throw new IllegalArgumentException("Only JPG / PNG images are allowed!");
        }

        if (file.getSize() > 5L * 1024 * 1024) {
            throw new IllegalArgumentException("File size must be under 5 MB!");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.contains(".")) {
            throw new IllegalArgumentException("Invalid file name!");
        }
        String ext = originalName.substring(originalName.lastIndexOf('.')).toLowerCase();

 
        // Example output: aadhaar_f47ac10b-58cc-4372-a567-0e02b2c3d479.jpg
        String fileName = "aadhaar_" + UUID.randomUUID() + ext;

 
        // Folder: project-root/uploads/aadhaar/
        Path uploadDir = Paths.get("uploads/aadhaar");
        Files.createDirectories(uploadDir);  


        Files.copy(
            file.getInputStream(),
            uploadDir.resolve(fileName),
            StandardCopyOption.REPLACE_EXISTING
        );

        return fileName;
    }

    // ─── User Save — BCrypt encode  ───────────────────────────
    public void saveUser(User user) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        userRepository.save(user);
    }

    // ─── Password Check — Login ke liye ──────────────────────────────────────
    public boolean checkPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }

    // ─── Voter Verify (Admin approval) ───────────────────────────────────────
    public User findById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    public void verifyVoter(Integer id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setVerified(true);
            userRepository.save(user);
        }
    }

    // ─── Admin Dashboard  ─────────────────────────────────────────────
    public long countAll() {
        return userRepository.count();
    }

    public java.util.List<User> findAll() {
        return userRepository.findAll();
    }
}