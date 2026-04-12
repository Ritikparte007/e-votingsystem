package com.example.votingsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.votingsystem.model.Admin;
import com.example.votingsystem.model.User;
import com.example.votingsystem.service.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;

@Controller
public class HomeController {

    @Autowired private UserService userService;
    @Autowired private VoteService voteService;
    @Autowired private ElectionService electionService;
    @Autowired private CandidateService candidateService;
    @Autowired private LogService logService;
    @Autowired private OtpService otpService;

    // ─── Home Page ────────────────────────────────────────────────────────────
    @GetMapping({"/", "/index"})
    public String home(Model model) {
        model.addAttribute("userCount",          userService.countAll());
        model.addAttribute("voteCount",          voteService.countAll());
        model.addAttribute("candidateCount",     candidateService.countAll());
        model.addAttribute("activeElectionCount",electionService.countActive());
        model.addAttribute("voteResults",        voteService.countVotesByCandidate());
        model.addAttribute("candidates",         candidateService.findAll());
        return "index";
    }

    // ─── Login ────────────────────────────────────────────────────────────────
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(
            @RequestParam String loginId,
            @RequestParam String password,
            @RequestParam String role,
            HttpServletRequest request,
            HttpSession session,
            Model model) {

        if ("admin".equals(role)) {
            Admin admin = userService.findAdmin(loginId);
            if (admin != null && admin.getPassword().equals(password)) {
                session.setAttribute("adminMode", true);
                logService.log("ADMIN", loginId, "Admin Login", "Admin logged in", request);
                return "redirect:/admin";
            }
            model.addAttribute("error", "Invalid Admin Credentials");
            return "login";
        }

        User user = userService.findVoterByIdOrEmail(loginId);
        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("userId", user.getUserId());
            logService.log("VOTER", user.getFullName(), "Voter Login",
                    "Voter " + user.getVoterId() + " logged in", request);
            return "redirect:/voter";
        }

        model.addAttribute("error", "Invalid Voter Credentials");
        return "login";
    }

    // ─── Logout ───────────────────────────────────────────────────────────────
    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletRequest request) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            User user = userService.findById(userId);
            if (user != null)
                logService.log("VOTER", user.getFullName(), "Logout", "Voter logged out", request);
        }
        if (session.getAttribute("adminMode") != null)
            logService.log("ADMIN", "Admin", "Logout", "Admin logged out", request);

        session.invalidate();
        return "redirect:/login";
    }

    // ─── Register ─────────────────────────────────────────────────────────────
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String saveUser(
            @RequestParam String fullName, @RequestParam String email,
            @RequestParam String mobileNo, @RequestParam String voterId,
            @RequestParam String aadhaarNo, @RequestParam String address,
            @RequestParam String password,  @RequestParam String confirmPassword,
            @RequestParam String state,     @RequestParam String dateOfBirth,
            HttpSession session, Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match!");
            return "register";
        }

        LocalDate dob = LocalDate.parse(dateOfBirth);
        if (LocalDate.now().getYear() - dob.getYear() < 18) {
            model.addAttribute("error", "You must be 18 or older!");
            return "register";
        }

        if (userService.isVoterIdTaken(voterId)) {
            model.addAttribute("error", "Voter ID already registered!");
            return "register";
        }

        if (userService.isEmailTaken(email)) {
            model.addAttribute("error", "Email already registered!");
            return "register";
        }

        String otp = otpService.generateOtp();
        session.setAttribute("reg_fullName",    fullName);
        session.setAttribute("reg_email",       email);
        session.setAttribute("reg_mobileNo",    mobileNo);
        session.setAttribute("reg_voterId",     voterId);
        session.setAttribute("reg_aadhaarNo",   aadhaarNo);
        session.setAttribute("reg_address",     address);
        session.setAttribute("reg_password",    password);
        session.setAttribute("reg_state",       state);
        session.setAttribute("reg_dateOfBirth", dateOfBirth);
        session.setAttribute("reg_otp",         otp);
        session.setAttribute("reg_otpTime",     System.currentTimeMillis());

        try {
            otpService.sendOtpSms(mobileNo, otp);
        } catch (Exception e) {
            model.addAttribute("error", "SMS send nahi hua: " + e.getMessage());
            return "register";
        }

        return "redirect:/verify-otp";
    }

    // ─── OTP Verify ───────────────────────────────────────────────────────────
    @GetMapping("/verify-otp")
    public String showOtpPage(HttpSession session, Model model) {
        if (session.getAttribute("reg_otp") == null)
            return "redirect:/register";
        model.addAttribute("maskedMobile", maskMobile((String) session.getAttribute("reg_mobileNo")));
        return "verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String otp, HttpSession session, Model model) {
        String savedOtp = (String) session.getAttribute("reg_otp");
        Long   otpTime  = (Long)   session.getAttribute("reg_otpTime");
        String mobile   = (String) session.getAttribute("reg_mobileNo");

        if (savedOtp == null) return "redirect:/register";

        if (System.currentTimeMillis() - otpTime > 5 * 60 * 1000L) {
            clearRegSession(session);
            model.addAttribute("error", "OTP expire ho gaya! Dobara register karo.");
            return "redirect:/register";
        }

        if (!savedOtp.equals(otp.trim())) {
            model.addAttribute("error", "Galat OTP!");
            model.addAttribute("maskedMobile", maskMobile(mobile));
            return "verify-otp";
        }

        User user = new User();
        user.setFullName((String)  session.getAttribute("reg_fullName"));
        user.setEmail((String)     session.getAttribute("reg_email"));
        user.setMobileNo((String)  session.getAttribute("reg_mobileNo"));
        user.setVoterId((String)   session.getAttribute("reg_voterId"));
        user.setAadhaarNo((String) session.getAttribute("reg_aadhaarNo"));
        user.setAddress((String)   session.getAttribute("reg_address"));
        user.setState((String)     session.getAttribute("reg_state"));
        user.setPassword((String)  session.getAttribute("reg_password"));
        user.setDateOfBirth(LocalDate.parse((String) session.getAttribute("reg_dateOfBirth")));
        user.setVerified(false);

        userService.saveUser(user);
        clearRegSession(session);
        return "redirect:/login?registered=true";
    }

    @GetMapping("/resend-otp")
    public String resendOtp(HttpSession session, Model model) {
        String mobile = (String) session.getAttribute("reg_mobileNo");
        if (mobile == null) return "redirect:/register";

        String newOtp = otpService.generateOtp();
        session.setAttribute("reg_otp",     newOtp);
        session.setAttribute("reg_otpTime", System.currentTimeMillis());

        try {
            otpService.sendOtpSms(mobile, newOtp);
            model.addAttribute("success", "Naya OTP bheja gaya!");
        } catch (Exception e) {
            model.addAttribute("error", "OTP bhejne mein problem.");
        }

        model.addAttribute("maskedMobile", maskMobile(mobile));
        return "verify-otp";
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private String maskMobile(String mobile) {
        if (mobile == null || mobile.length() < 4) return "**********";
        return mobile.substring(0, 2) + "*****" + mobile.substring(mobile.length() - 3);
    }

    private void clearRegSession(HttpSession session) {
        String[] keys = {"reg_fullName","reg_email","reg_mobileNo","reg_voterId",
                         "reg_aadhaarNo","reg_address","reg_password","reg_state",
                         "reg_dateOfBirth","reg_otp","reg_otpTime"};
        for (String key : keys) session.removeAttribute(key);
    }
}