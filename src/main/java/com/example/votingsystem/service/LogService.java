package com.example.votingsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.votingsystem.model.ActivityLog;
import com.example.votingsystem.repository.ActivityLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Service
public class LogService {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    public void log(String userType, String username, String action,
                    String details, HttpServletRequest request) {

        ActivityLog log = new ActivityLog();
        log.setUserType(userType);
        log.setUsername(username);
        log.setAction(action);
        log.setDetails(details);
        log.setIpAddress(request.getRemoteAddr());
        log.setLoggedAt(LocalDateTime.now());

        activityLogRepository.save(log);
    }
}