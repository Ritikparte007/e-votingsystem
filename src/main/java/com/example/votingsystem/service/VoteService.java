package com.example.votingsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.votingsystem.model.*;
import com.example.votingsystem.repository.*;

import java.time.LocalDateTime;

@Service
public class VoteService {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private VoteReceiptRepository voteReceiptRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private ElectionRepository electionRepository;

    // ─── Pehle se vote diya hai? ─────────────────────────────────────────────
    public boolean hasAlreadyVoted(User user, Election election) {
        return voteRepository.existsByUserAndElection(user, election);
    }

    // ─── Vote Save karo ──────────────────────────────────────────────────────
    public String castVote(User user, Election election, Candidate candidate) {
        // Vote save
        Vote vote = new Vote();
        vote.setUser(user);
        vote.setElection(election);
        vote.setCandidate(candidate);
        vote.setVotedAt(LocalDateTime.now());
        voteRepository.save(vote);

        // Receipt generate aur save
        String receiptNo = "VOTE-" + System.currentTimeMillis();
        VoteReceipt receipt = new VoteReceipt();
        receipt.setReceiptNo(receiptNo);
        receipt.setUser(user);
        receipt.setElection(election);
        receipt.setCandidate(candidate);
        receipt.setVotedAt(LocalDateTime.now());
        voteReceiptRepository.save(receipt);

        return receiptNo;
    }

    // ─── Receipt fetch karo ──────────────────────────────────────────────────
    public VoteReceipt findReceipt(String receiptNo) {
        return voteReceiptRepository.findByReceiptNo(receiptNo);
    }

    // ─── Candidate load ──────────────────────────────────────────────────────
    public Candidate findCandidate(Integer id) {
        return candidateRepository.findById(id).orElse(null);
    }

    // ─── Election load ────────────────────────────────────────────────────────
    public Election findElection(Integer id) {
        return electionRepository.findById(id).orElse(null);
    }

    // ─── Dashboard stats ─────────────────────────────────────────────────────
    public long countAll() {
        return voteRepository.count();
    }

    public java.util.List<?> countVotesByCandidate() {
        return voteRepository.countVotesByCandidate();
    }
}