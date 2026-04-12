package com.example.votingsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.votingsystem.model.Election;
import com.example.votingsystem.repository.ElectionRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class ElectionService {

    @Autowired
    private ElectionRepository electionRepository;

    // ─── Active election dhundo ───────────────────────────────────────────────
    public Election findActive() {
        return electionRepository.findAll().stream()
                .filter(e -> "ACTIVE".equals(e.getStatus()))
                .findFirst()
                .orElse(null);
    }

    // ─── Saari elections ─────────────────────────────────────────────────────
    public List<Election> findAll() {
        return electionRepository.findAll();
    }

    // ─── Election by ID ───────────────────────────────────────────────────────
    public Election findById(Integer id) {
        return electionRepository.findById(id).orElse(null);
    }

    // ─── Nai election add karo ────────────────────────────────────────────────
    public void addElection(String name, String startDate, String startTime,
                            String endDate, String endTime) {
        Election election = new Election();
        election.setElectionName(name);
        election.setStartDate(LocalDate.parse(startDate));
        election.setEndDate(LocalDate.parse(endDate));
        election.setStartTime(LocalTime.parse(startTime));
        election.setEndTime(LocalTime.parse(endTime));
        election.setStatus("ACTIVE");
        electionRepository.save(election);
    }

    // ─── ACTIVE ↔ STOPPED toggle ─────────────────────────────────────────────
    public String toggleStatus(Integer id) {
        Election election = electionRepository.findById(id).orElse(null);
        if (election != null) {
            String newStatus = "ACTIVE".equals(election.getStatus()) ? "STOPPED" : "ACTIVE";
            election.setStatus(newStatus);
            electionRepository.save(election);
            return election.getElectionName() + " → " + newStatus;
        }
        return "";
    }

    // ─── Election delete ──────────────────────────────────────────────────────
    public void delete(Integer id) {
        electionRepository.deleteById(id);
    }

    // ─── Active count (dashboard ke liye) ────────────────────────────────────
    public long countActive() {
        return electionRepository.findAll().stream()
                .filter(e -> "ACTIVE".equals(e.getStatus())).count();
    }
}