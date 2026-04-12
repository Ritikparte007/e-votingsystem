package com.example.votingsystem.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.votingsystem.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    Optional<User> findByVoterId(String voterId);
}