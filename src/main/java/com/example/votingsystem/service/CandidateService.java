package com.example.votingsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.votingsystem.model.Candidate;
import com.example.votingsystem.model.Election;
import com.example.votingsystem.repository.CandidateRepository;
import com.example.votingsystem.repository.ElectionRepository;

import java.util.List;

@Service
public class CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private ElectionRepository electionRepository;

    // ─── Saare candidates ────────────────────────────────────────────────────
    public List<Candidate> findAll() {
        return candidateRepository.findAll();
    }

    // ─── Election ke candidates ───────────────────────────────────────────────
    public List<Candidate> findByElection(Election election) {
        return candidateRepository.findByElection(election);
    }

    // ─── Candidate by ID ──────────────────────────────────────────────────────
    public Candidate findById(Integer id) {
        return candidateRepository.findById(id).orElse(null);
    }

    // ─── Naya candidate add karo ─────────────────────────────────────────────
    public void addCandidate(String fullName, String partyName, String symbol,
                             String description, Integer electionId) {
        Candidate c = new Candidate();
        c.setFullName(fullName);
        c.setPartyName(partyName);
        c.setSymbol(symbol);
        c.setDescription(description);
        c.setElection(electionRepository.findById(electionId).orElse(null));
        candidateRepository.save(c);
    }

    // ─── Candidate edit karo ─────────────────────────────────────────────────
    public void editCandidate(Integer id, String fullName, String partyName,
                              String symbol, String description, Integer electionId) {
        Candidate c = candidateRepository.findById(id).orElse(null);
        if (c != null) {
            c.setFullName(fullName);
            c.setPartyName(partyName);
            c.setSymbol(symbol);
            c.setDescription(description);
            c.setElection(electionRepository.findById(electionId).orElse(null));
            candidateRepository.save(c);
        }
    }

    // ─── Candidate delete ────────────────────────────────────────────────────
    public void delete(Integer id) {
        candidateRepository.deleteById(id);
    }

    // ─── Count (dashboard) ───────────────────────────────────────────────────
    public long countAll() {
        return candidateRepository.count();
    }
}