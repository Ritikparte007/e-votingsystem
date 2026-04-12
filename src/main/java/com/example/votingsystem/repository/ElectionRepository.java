package com.example.votingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.votingsystem.model.Election;
import java.util.List; 

public interface ElectionRepository extends JpaRepository<Election, Integer> {

     List<Election> findByStatus(String status);
}