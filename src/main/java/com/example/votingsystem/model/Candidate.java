package com.example.votingsystem.model;

import jakarta.persistence.*;

@Entity
@Table(name = "candidate")
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer candidateId;

    private String fullName;
    private String partyName;
    private String symbol;

    @Column(columnDefinition = "TEXT")
    private String description;  // ✅ NEW

    @ManyToOne
    @JoinColumn(name = "election_id")
    private Election election;

    // ===== GETTERS =====
    public Integer getCandidateId() { return candidateId; }
    public String getFullName() { return fullName; }
    public String getPartyName() { return partyName; }
    public String getSymbol() { return symbol; }
    public String getDescription() { return description; }  // ✅ NEW
    public Election getElection() { return election; }

    // ===== SETTERS =====
    public void setCandidateId(Integer id) { this.candidateId = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPartyName(String partyName) { this.partyName = partyName; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public void setDescription(String description) { this.description = description; }  // ✅ NEW
    public void setElection(Election election) { this.election = election; }
}