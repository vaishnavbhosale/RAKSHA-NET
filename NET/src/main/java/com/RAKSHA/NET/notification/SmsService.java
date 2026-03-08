package com.RAKSHA.NET.notification;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Sends SMS alerts via the Twilio REST API.
 *
 * <p>If any of the required Twilio credentials (account-sid, auth-token,
 * from-number, to-number) are blank the service logs a warning and skips
 * sending rather than throwing an exception, so that incident creation is
 * never blocked by a missing SMS configuration.
 */
@Service
public class SmsService {

    private static final Logger log = LoggerFactory.getLogger(SmsService.class);

    private final TwilioProperties twilioProperties;

    public SmsService(TwilioProperties twilioProperties) {
        this.twilioProperties = twilioProperties;
    }

    /**
     * Sends an SMS to the configured recipient number. Failures are logged but
     * do NOT propagate so that the calling operation can still succeed.
     *
     * @param messageBody the text content of the SMS
     */
    public void sendSms(String messageBody) {
        if (!isConfigured()) {
            log.warn("Twilio credentials not configured – SMS alert skipped");
            return;
        }
        try {
            Twilio.init(twilioProperties.getAccountSid(), twilioProperties.getAuthToken());
            Message message = Message.creator(
                    new PhoneNumber(twilioProperties.getToNumber()),
                    new PhoneNumber(twilioProperties.getFromNumber()),
                    messageBody
            ).create();
            log.info("SMS alert sent, SID={}", message.getSid());
        } catch (ApiException ex) {
            log.error("Failed to send SMS alert: {}", ex.getMessage());
        } catch (Exception ex) {
            log.error("Unexpected error while sending SMS alert: {}", ex.getMessage());
        }
    }

    private boolean isConfigured() {
        return StringUtils.hasText(twilioProperties.getAccountSid())
                && StringUtils.hasText(twilioProperties.getAuthToken())
                && StringUtils.hasText(twilioProperties.getFromNumber())
                && StringUtils.hasText(twilioProperties.getToNumber());
    }
}
