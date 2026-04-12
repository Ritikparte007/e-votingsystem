package com.example.votingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.example.votingsystem.model.Vote;
import com.example.votingsystem.model.User;
import com.example.votingsystem.model.Election;
import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Integer> {

    boolean existsByUserAndElection(User user, Election election);

    @Query("SELECT c.fullName, COUNT(v) FROM Vote v JOIN v.candidate c GROUP BY c.fullName ORDER BY COUNT(v) DESC")
    List<Object[]> countVotesByCandidate();
}