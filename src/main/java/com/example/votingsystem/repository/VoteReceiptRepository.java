package com.example.votingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.votingsystem.model.VoteReceipt;
import com.example.votingsystem.model.User;
import com.example.votingsystem.model.Election;

public interface VoteReceiptRepository extends JpaRepository<VoteReceipt, Integer> {
    VoteReceipt findByReceiptNo(String receiptNo);           // ✅ ADD THIS
    VoteReceipt findByUserAndElection(User user, Election election);
}