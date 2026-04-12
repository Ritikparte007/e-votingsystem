package com.example.votingsystem.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "votes")
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer voteId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    @ManyToOne
    @JoinColumn(name = "election_id")
    private Election election;

    private LocalDateTime votedAt = LocalDateTime.now();

    // ===== GETTERS =====
    public Integer getVoteId() { return voteId; }
    public User getUser() { return user; }
    public Candidate getCandidate() { return candidate; }
    public Election getElection() { return election; }
    public LocalDateTime getVotedAt() { return votedAt; }

    // ===== SETTERS =====
    public void setUser(User user) { this.user = user; }
    public void setCandidate(Candidate candidate) { this.candidate = candidate; }
    public void setElection(Election election) { this.election = election; }
    public void setVotedAt(LocalDateTime votedAt) { this.votedAt = votedAt; }
}