package com.example.votingsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.votingsystem.repository.ActivityLogRepository;
import com.example.votingsystem.service.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class AdminController {

    @Autowired private UserService userService;
    @Autowired private VoteService voteService;
    @Autowired private ElectionService electionService;
    @Autowired private CandidateService candidateService;
    @Autowired private LogService logService;
    @Autowired private ActivityLogRepository activityLogRepository;

    // ─── Admin Dashboard ─────────────────────────────────────────────────────
    @GetMapping("/admin")
    public String admin(HttpSession session, Model model) {
        if (session.getAttribute("adminMode") == null) return "redirect:/login";

        var allUsers    = userService.findAll();
        long totalVotes = voteService.countAll();
        long totalVoters= userService.countAll();

        model.addAttribute("users",           allUsers);
        model.addAttribute("verifiedCount",   allUsers.stream().filter(u -> u.isVerified()).count());
        model.addAttribute("pendingCount",    allUsers.stream().filter(u -> !u.isVerified()).count());
        model.addAttribute("totalVoters",     totalVoters);
        model.addAttribute("totalCandidates", candidateService.countAll());
        model.addAttribute("totalVotes",      totalVotes);
        model.addAttribute("activeElections", electionService.countActive());
        model.addAttribute("candidates",      candidateService.findAll());
        model.addAttribute("elections",       electionService.findAll());
        model.addAttribute("voteResults",     voteService.countVotesByCandidate());
        model.addAttribute("turnoutPercent",
                String.format("%.1f", totalVoters > 0 ? (totalVotes * 100.0 / totalVoters) : 0));
        model.addAttribute("logs", activityLogRepository.findTop50ByOrderByLoggedAtDesc());

        return "admin";
    }

    // ─── Voter Verify ────────────────────────────────────────────────────────
    @GetMapping("/verify-voter/{id}")
    public String verifyVoter(@PathVariable Integer id, HttpServletRequest request) {
        userService.verifyVoter(id);
        var user = userService.findById(id);
        if (user != null)
            logService.log("ADMIN", "Admin", "Voter Approved",
                    "Approved: " + user.getFullName(), request);
        return "redirect:/admin";
    }

    // ─── Election Management ─────────────────────────────────────────────────
    @PostMapping("/add-election")
    public String addElection(
            @RequestParam String electionName,
            @RequestParam String startDate, @RequestParam String startTime,
            @RequestParam String endDate,   @RequestParam String endTime,
            HttpServletRequest request) {

        electionService.addElection(electionName, startDate, startTime, endDate, endTime);
        logService.log("ADMIN", "Admin", "Election Created", "New: " + electionName, request);
        return "redirect:/admin";
    }

    @GetMapping("/toggle-election/{id}")
    public String toggleElection(@PathVariable Integer id, HttpServletRequest request) {
        String result = electionService.toggleStatus(id);
        logService.log("ADMIN", "Admin", "Election Toggled", result, request);
        return "redirect:/admin";
    }

    @GetMapping("/delete-election/{id}")
    public String deleteElection(@PathVariable Integer id) {
        electionService.delete(id);
        return "redirect:/admin";
    }

    // ─── Aadhaar Image Serve ─────────────────────────────────────────────────
    @GetMapping("/admin/aadhaar-image/{filename}")
    public ResponseEntity<Resource> serveAadhaarImage(
            @PathVariable String filename,
            HttpSession session) throws Exception {

        if (session.getAttribute("adminMode") == null)
            return ResponseEntity.status(403).build();

        if (filename.contains("..") || filename.contains("/") || filename.contains("\\"))
            return ResponseEntity.badRequest().build();

        Path filePath = Paths.get("uploads/aadhaar").resolve(filename).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable())
            return ResponseEntity.notFound().build();

        String contentType = filename.toLowerCase().endsWith(".png") ? "image/png" : "image/jpeg";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    // ─── Candidate Image Serve ───────────────────────────────────────────────  ✅ NEW
    @GetMapping("/admin/candidate-image/{filename}")
    public ResponseEntity<Resource> serveCandidateImage(
            @PathVariable String filename,
            HttpSession session) throws Exception {

        if (session.getAttribute("adminMode") == null)
            return ResponseEntity.status(403).build();

        if (filename.contains("..") || filename.contains("/") || filename.contains("\\"))
            return ResponseEntity.badRequest().build();

        Path filePath = Paths.get("uploads/candidates").resolve(filename).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable())
            return ResponseEntity.notFound().build();

        String contentType = filename.toLowerCase().endsWith(".png") ? "image/png" : "image/jpeg";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    // ─── Candidate Management ────────────────────────────────────────────────
    @PostMapping("/add-candidate")
    public String addCandidate(
            @RequestParam String fullName,
            @RequestParam String partyName,
            @RequestParam String symbol,
            @RequestParam String description,
            @RequestParam Integer electionId,
            @RequestParam Integer age,                                     // ✅ NEW
            @RequestParam("candidateImage") MultipartFile candidateImage,  // ✅ NEW
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        // ✅ Image save karo
        String savedImage = null;
        try {
            savedImage = candidateService.saveCandidateImage(candidateImage);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                "Image upload failed: " + e.getMessage());
            return "redirect:/admin";
        }

        candidateService.addCandidate(
            fullName, partyName, symbol, description,
            electionId, age, savedImage               // ✅ pass karo
        );

        logService.log("ADMIN", "Admin", "Candidate Added",
            fullName + " (" + partyName + ")", request);
        return "redirect:/admin";
    }

    @PostMapping("/edit-candidate/{id}")
    public String editCandidate(
            @PathVariable Integer id,
            @RequestParam String fullName,  @RequestParam String partyName,
            @RequestParam String symbol,    @RequestParam String description,
            @RequestParam Integer electionId) {

        candidateService.editCandidate(id, fullName, partyName, symbol, description, electionId);
        return "redirect:/admin";
    }

    @GetMapping("/delete-candidate/{id}")
    public String deleteCandidate(@PathVariable Integer id) {
        candidateService.delete(id);
        return "redirect:/admin";
    }

    @GetMapping("/edit-candidate/{id}")
    public String editCandidatePage(@PathVariable Integer id, Model model, HttpSession session) {
        if (session.getAttribute("adminMode") == null) return "redirect:/login";

        model.addAttribute("elections",       electionService.findAll());
        model.addAttribute("candidates",      candidateService.findAll());
        model.addAttribute("totalVoters",     userService.countAll());
        model.addAttribute("totalCandidates", candidateService.countAll());
        model.addAttribute("totalVotes",      voteService.countAll());
        model.addAttribute("activeElections", electionService.countActive());
        model.addAttribute("voteResults",     voteService.countVotesByCandidate());
        model.addAttribute("users",           userService.findAll());
        return "admin";
    }
}