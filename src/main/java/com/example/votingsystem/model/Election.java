package com.example.votingsystem.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "election")
public class Election {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer electionId;

    private String electionName;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;    // ✅ field tha but getter/setter missing tha
    private String status = "ACTIVE";

    // ===== GETTERS =====
    public Integer getElectionId()      { return electionId; }
    public String getElectionName()     { return electionName; }
    public LocalDate getStartDate()     { return startDate; }
    public LocalDate getEndDate()       { return endDate; }
    public LocalTime getStartTime()     { return startTime; }
    public LocalTime getEndTime()       { return endTime; }   // ✅ ADD KIYA
    public String getStatus()           { return status; }

    // ===== SETTERS =====
    public void setElectionId(Integer electionId)       { this.electionId = electionId; }
    public void setElectionName(String electionName)    { this.electionName = electionName; }
    public void setStartDate(LocalDate startDate)       { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate)           { this.endDate = endDate; }
    public void setStartTime(LocalTime startTime)       { this.startTime = startTime; }
    public void setEndTime(LocalTime endTime)           { this.endTime = endTime; }   // ✅ ADD KIYA
    public void setStatus(String status)                { this.status = status; }
}