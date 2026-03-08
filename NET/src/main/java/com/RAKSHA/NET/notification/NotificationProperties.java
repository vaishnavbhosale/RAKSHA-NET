package com.RAKSHA.NET.notification;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Notification-related configuration properties.
 *
 * <p>Set these via environment variables or application.properties:
 * <ul>
 *   <li>ALERT_RECIPIENT_EMAIL  → alert.recipient-email</li>
 * </ul>
 */
@Component
@ConfigurationProperties(prefix = "alert")
public class NotificationProperties {

    /** Recipient e-mail address for emergency alert notifications. */
    private String recipientEmail = "admin@example.com";

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }
}
