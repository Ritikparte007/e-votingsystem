package com.example.votingsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.votingsystem.model.*;
import com.example.votingsystem.service.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@Controller
public class VoterController {

    @Autowired private UserService userService;
    @Autowired private VoteService voteService;
    @Autowired private ElectionService electionService;
    @Autowired private CandidateService candidateService;
    @Autowired private LogService logService;

    // ─── Voter Dashboard ─────────────────────────────────────────────────────
    @GetMapping("/voter")
    public String voter(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        User user = userService.findById(userId);
        model.addAttribute("user", user);

        Election activeElection = electionService.findActive();
        model.addAttribute("election", activeElection);

        if (activeElection != null) {
            model.addAttribute("candidates", candidateService.findByElection(activeElection));
            model.addAttribute("alreadyVoted", voteService.hasAlreadyVoted(user, activeElection));
        } else {
            model.addAttribute("candidates",   List.of());
            model.addAttribute("alreadyVoted", false);
        }

        return "voter";
    }

    // ─── Vote Cast ────────────────────────────────────────────────────────────
    @PostMapping("/cast-vote")
    public String castVote(
            @RequestParam Integer candidateId,
            @RequestParam Integer electionId,
            HttpSession session,
            HttpServletRequest request) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        User      user      = userService.findById(userId);
        Election  election  = voteService.findElection(electionId);
        Candidate candidate = voteService.findCandidate(candidateId);

        if (user == null || election == null || candidate == null)
            return "redirect:/voter";

        if (!user.isVerified())
            return "redirect:/voter";

        if (voteService.hasAlreadyVoted(user, election)) {
            logService.log("VOTER", user.getFullName(), "Duplicate Vote",
                    "Already voted in: " + election.getElectionName(), request);
            return "redirect:/voter?error=alreadyVoted";
        }

        String receiptNo = voteService.castVote(user, election, candidate);

        logService.log("VOTER", user.getFullName(), "Vote Cast",
                "Voted in: " + election.getElectionName() + " | Receipt: " + receiptNo, request);

        session.setAttribute("lastReceiptNo", receiptNo);
        return "redirect:/receipt";
    }

    // ─── Receipt ─────────────────────────────────────────────────────────────
    @GetMapping("/receipt")
    public String receiptPage(HttpSession session, Model model) {
        Integer userId   = (Integer) session.getAttribute("userId");
        String receiptNo = (String)  session.getAttribute("lastReceiptNo");

        if (userId == null || receiptNo == null) return "redirect:/voter";

        VoteReceipt receipt = voteService.findReceipt(receiptNo);
        if (receipt == null) return "redirect:/voter";

        model.addAttribute("receipt", receipt);
        return "receipt";
    }
}