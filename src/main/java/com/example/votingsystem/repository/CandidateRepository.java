package com.example.votingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.votingsystem.model.Candidate;
import com.example.votingsystem.model.Election;
import java.util.List; // ✅ MUST ADD THIS IMPORT

public interface CandidateRepository extends JpaRepository<Candidate, Integer> {
    
    // ✅ This method is now defined correctly
    List<Candidate> findByElection(Election election);
}