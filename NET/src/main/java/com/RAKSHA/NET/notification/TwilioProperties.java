package com.RAKSHA.NET.notification;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Twilio SMS configuration properties.
 *
 * <p>Set these via environment variables or application.properties:
 * <ul>
 *   <li>TWILIO_ACCOUNT_SID  → twilio.account-sid</li>
 *   <li>TWILIO_AUTH_TOKEN   → twilio.auth-token</li>
 *   <li>TWILIO_FROM_NUMBER  → twilio.from-number</li>
 *   <li>TWILIO_TO_NUMBER    → twilio.to-number</li>
 * </ul>
 */
@Component
@ConfigurationProperties(prefix = "twilio")
public class TwilioProperties {

    private String accountSid = "";
    private String authToken = "";
    private String fromNumber = "";
    private String toNumber = "";

    public String getAccountSid() {
        return accountSid;
    }

    public void setAccountSid(String accountSid) {
        this.accountSid = accountSid;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getFromNumber() {
        return fromNumber;
    }

    public void setFromNumber(String fromNumber) {
        this.fromNumber = fromNumber;
    }

    public String getToNumber() {
        return toNumber;
    }

    public void setToNumber(String toNumber) {
        this.toNumber = toNumber;
    }
}
