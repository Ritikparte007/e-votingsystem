package com.example.votingsystem.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.votingsystem.model.Admin;

public interface AdminRepository extends JpaRepository<Admin, Integer> {

    Optional<Admin> findByUsername(String username);
}