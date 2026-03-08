package com.RAKSHA.NET.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends an e-mail alert. Failures are logged but do NOT propagate so that
     * the calling operation (e.g. incident creation) can still succeed.
     *
     * @param to      recipient address
     * @param subject e-mail subject line
     * @param body    e-mail body text
     */
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(body);
            mailSender.send(mail);
            log.info("Email alert sent to {}", to);
        } catch (MailException ex) {
            log.error("Failed to send email alert to {}: {}", to, ex.getMessage());
        }
    }

    /**
     * Convenience overload that uses a default subject of "Emergency Alert".
     *
     * @param to      recipient address
     * @param message e-mail body text
     */
    public void sendEmail(String to, String message) {
        sendEmail(to, "Emergency Alert", message);
    }
}

