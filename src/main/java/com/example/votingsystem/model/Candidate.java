package com.example.votingsystem.model;

import jakarta.persistence.*;

@Entity
@Table(name = "candidate")
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "candidate_id")
    private Integer candidateId;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "party_name")
    private String partyName;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "candidate_image")       // ✅ NEW — stores image filename
    private String candidateImage;

    @Column(name = "age")                   // ✅ NEW — candidate age
    private Integer age;

    @ManyToOne
    @JoinColumn(name = "election_id")
    private Election election;

    // ===== GETTERS =====
    public Integer getCandidateId()     { return candidateId; }
    public String getFullName()         { return fullName; }
    public String getPartyName()        { return partyName; }
    public String getSymbol()           { return symbol; }
    public String getDescription()      { return description; }
    public String getCandidateImage()   { return candidateImage; }  // ✅ NEW
    public Integer getAge()             { return age; }             // ✅ NEW
    public Election getElection()       { return election; }

    // ===== SETTERS =====
    public void setCandidateId(Integer id)          { this.candidateId = id; }
    public void setFullName(String fullName)        { this.fullName = fullName; }
    public void setPartyName(String partyName)      { this.partyName = partyName; }
    public void setSymbol(String symbol)            { this.symbol = symbol; }
    public void setDescription(String description)  { this.description = description; }
    public void setCandidateImage(String img)       { this.candidateImage = img; }  // ✅ NEW
    public void setAge(Integer age)                 { this.age = age; }             // ✅ NEW
    public void setElection(Election election)      { this.election = election; }
}