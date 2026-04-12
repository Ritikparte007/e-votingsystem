package com.example.votingsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.votingsystem.model.Admin;
import com.example.votingsystem.model.User;
import com.example.votingsystem.repository.AdminRepository;
import com.example.votingsystem.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

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

    // ─── User Save (OTP verify hone ke baad call karna) ──────────────────────
    public void saveUser(User user) {
        userRepository.save(user);
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

    // ─── Admin Dashboard ke liye ──────────────────────────────────────────────
    public long countAll() {
        return userRepository.count();
    }

    public java.util.List<User> findAll() {
        return userRepository.findAll();
    }
}