package com.example.votingsystem.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vote_receipt")
public class VoteReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer receiptId;

    @Column(name = "receipt_no", unique = true)
    private String receiptNo;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    @ManyToOne
    @JoinColumn(name = "election_id")
    private Election election;

    @Column(name = "voted_at")
    private LocalDateTime votedAt = LocalDateTime.now();

    // ===== GETTERS =====
    public Integer getReceiptId() { return receiptId; }
    public String getReceiptNo() { return receiptNo; }
    public User getUser() { return user; }
    public Candidate getCandidate() { return candidate; }
    public Election getElection() { return election; }
    public LocalDateTime getVotedAt() { return votedAt; }

    // ===== SETTERS =====
    public void setReceiptNo(String receiptNo) { this.receiptNo = receiptNo; }
    public void setUser(User user) { this.user = user; }
    public void setCandidate(Candidate candidate) { this.candidate = candidate; }
    public void setElection(Election election) { this.election = election; }
    public void setVotedAt(LocalDateTime votedAt) { this.votedAt = votedAt; }
}