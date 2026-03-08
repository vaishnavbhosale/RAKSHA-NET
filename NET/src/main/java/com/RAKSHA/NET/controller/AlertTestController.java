package com.RAKSHA.NET.controller;

import com.RAKSHA.NET.notification.EmailService;
import com.RAKSHA.NET.notification.NotificationProperties;
import com.RAKSHA.NET.notification.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Manual test endpoints for verifying email and SMS alert delivery without
 * needing to create an incident.
 *
 * <p>Usage:
 * <pre>
 *   GET /api/alerts/test/email?to=someone@example.com
 *   GET /api/alerts/test/sms
 * </pre>
 */
@RestController
@RequestMapping("/api/alerts/test")
@RequiredArgsConstructor
public class AlertTestController {

    private final EmailService emailService;
    private final SmsService smsService;
    private final NotificationProperties notificationProperties;

    /**
     * Sends a test email alert to the given address (defaults to the configured
     * recipient if no {@code to} parameter is supplied).
     */
    @GetMapping("/email")
    public ResponseEntity<Map<String, String>> testEmail(
            @RequestParam(required = false) String to) {
        String recipient = (to != null && !to.isBlank())
                ? to
                : notificationProperties.getRecipientEmail();
        emailService.sendEmail(
                recipient,
                "RAKSHA-NET Test Alert",
                "This is a test email from RAKSHA-NET. If you received this, email alerts are working correctly."
        );
        return ResponseEntity.ok(Map.of(
                "status", "Email alert dispatched",
                "recipient", recipient
        ));
    }

    /**
     * Sends a test SMS to the configured Twilio recipient number.
     */
    @GetMapping("/sms")
    public ResponseEntity<Map<String, String>> testSms() {
        smsService.sendSms(
                "RAKSHA-NET Test Alert: This is a test SMS. If you received this, SMS alerts are working correctly."
        );
        return ResponseEntity.ok(Map.of("status", "SMS alert dispatched"));
    }
}
