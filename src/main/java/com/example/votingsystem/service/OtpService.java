package com.example.votingsystem.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class OtpService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String fromNumber;

    // App start hote hi Twilio initialize hoga
    @PostConstruct
    public void initTwilio() {
        Twilio.init(accountSid, authToken);
    }

    // 6 digit OTP generate karo
    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    // SMS bhejo via Twilio
    public void sendOtpSms(String toMobileNo, String otp) {

        String formattedNumber = toMobileNo.startsWith("+")
                ? toMobileNo
                : "+91" + toMobileNo;

        // ── Console mein print karo (testing ke liye) ──────────────────────
        System.out.println("=============================");
        System.out.println("OTP: " + otp + " -> " + toMobileNo);
        System.out.println("=============================");

        // ── Twilio se SMS bhejo ────────────────────────────────────────────
        Message.creator(
                new PhoneNumber(formattedNumber),
                new PhoneNumber(fromNumber),
                "Your OTP for Online Voting System is: " + otp +
                "\nValid for 5 minutes. Do NOT share with anyone."
        ).create();

    } 

} // ✅ Class ka closing brace