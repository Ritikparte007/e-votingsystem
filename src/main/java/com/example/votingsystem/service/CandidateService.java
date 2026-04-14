package com.example.votingsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.votingsystem.model.Candidate;
import com.example.votingsystem.model.Election;
import com.example.votingsystem.repository.CandidateRepository;
import com.example.votingsystem.repository.ElectionRepository;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class CandidateService {

    @Autowired private CandidateRepository candidateRepository;
    @Autowired private ElectionRepository electionRepository;

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

    // ─── Candidate Image Save ────────────────────────────────────────────────  ✅ NEW
    public String saveCandidateImage(MultipartFile file) throws IOException {

        // Image optional hai — agar upload nahi kiya to null return karo
        if (file == null || file.isEmpty()) {
            return null;
        }

        // ✅ Content-type validate karo
        String contentType = file.getContentType();
        if (contentType == null ||
            (!contentType.equals("image/jpeg") &&
             !contentType.equals("image/png")  &&
             !contentType.equals("image/jpg"))) {
            throw new IllegalArgumentException("Only JPG / PNG images allowed!");
        }

        // ✅ Size check — max 10MB
        if (file.getSize() > 10L * 1024 * 1024) {
            throw new IllegalArgumentException("File size must be under 10MB!");
        }

        // ✅ Safe extension nikalo
        String originalName = file.getOriginalFilename();
        String ext = (originalName != null && originalName.contains("."))
                ? originalName.substring(originalName.lastIndexOf('.')).toLowerCase()
                : ".jpg";

        // ✅ UUID se random filename banao
        String fileName = "candidate_" + UUID.randomUUID() + ext;

        // ✅ uploads/candidates/ folder mein save karo — /static/ ke bahar
        Path uploadDir = Paths.get("uploads/candidates");
        Files.createDirectories(uploadDir);
        Files.copy(
            file.getInputStream(),
            uploadDir.resolve(fileName),
            StandardCopyOption.REPLACE_EXISTING
        );

        return fileName; // ✅ Sirf filename DB mein store hoga
    }

    // ─── Naya candidate add karo ─────────────────────────────────────────────  ✅ UPDATED
    public void addCandidate(String fullName, String partyName, String symbol,
                             String description, Integer electionId,
                             Integer age, String candidateImage) {
        Candidate c = new Candidate();
        c.setFullName(fullName);
        c.setPartyName(partyName);
        c.setSymbol(symbol);
        c.setDescription(description);
        c.setAge(age);                      // ✅ NEW
        c.setCandidateImage(candidateImage); // ✅ NEW
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

    // ─── Save (direct object) ────────────────────────────────────────────────
    public void save(Candidate candidate) {
        candidateRepository.save(candidate);
    }
}