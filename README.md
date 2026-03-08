# RAKSHA-NET Backend

Spring Boot 4 backend for the RAKSHA-NET emergency-response platform.  
Provides REST APIs for SOS incident management, responder dispatch, citizen-status tracking, and — as of this update — **real-time email and SMS emergency alerts**.

---

## Quick Start

### Prerequisites
| Tool | Version |
|------|---------|
| Java | 17+ |
| Maven wrapper (`./mvnw`) | bundled |

### 1. Clone & configure

```bash
# Copy the example config and fill in your credentials
cp src/main/resources/application.properties.example \
   src/main/resources/application-local.properties
```

Edit `application-local.properties` (or set the environment variables below).  
**Do not commit `application-local.properties` or any file containing real credentials.**

### 2. Run

```bash
cd NET
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

The server starts on **http://localhost:8080**.

---

## Email Alerts (SMTP / Gmail)

| Property | Environment Variable | Default |
|----------|---------------------|---------|
| `spring.mail.host` | `MAIL_HOST` | `smtp.gmail.com` |
| `spring.mail.port` | `MAIL_PORT` | `587` |
| `spring.mail.username` | `MAIL_USERNAME` | _(empty)_ |
| `spring.mail.password` | `MAIL_PASSWORD` | _(empty)_ |
| `alert.recipient-email` | `ALERT_RECIPIENT_EMAIL` | `admin@example.com` |

### Gmail App Password Setup
1. Enable 2-Factor Authentication on your Google account.
2. Go to **Google Account → Security → App Passwords**.
3. Generate a new app password for "Mail".
4. Use that 16-character password as `MAIL_PASSWORD`.

### Test email delivery
```
GET http://localhost:8080/api/alerts/test/email?to=you@example.com
```

---

## SMS Alerts (Twilio)

| Property | Environment Variable | Description |
|----------|---------------------|-------------|
| `twilio.account-sid` | `TWILIO_ACCOUNT_SID` | Account SID from Twilio Console |
| `twilio.auth-token` | `TWILIO_AUTH_TOKEN` | Auth Token from Twilio Console |
| `twilio.from-number` | `TWILIO_FROM_NUMBER` | Your Twilio phone number, e.g. `+14155552671` |
| `twilio.to-number` | `TWILIO_TO_NUMBER` | Recipient phone number, e.g. `+919876543210` |

### Twilio Setup
1. Sign up at [twilio.com/try-twilio](https://www.twilio.com/try-twilio).
2. Get a free phone number in the Twilio Console.
3. Find **Account SID** and **Auth Token** on the dashboard.
4. Set the four environment variables above.

> If any Twilio credential is blank the SMS is silently skipped (incident creation still succeeds).

### Test SMS delivery
```
GET http://localhost:8080/api/alerts/test/sms
```

---

## SOS Incident Creation (triggers alerts)

```
POST /api/incidents/sos
Content-Type: application/json

{
  "latitude": 19.0760,
  "longitude": 72.8777,
  "incidentType": "FIRE",
  "severity": "HIGH",
  "description": "Fire reported near station road"
}
```

On success (HTTP 200) an email **and** SMS alert are dispatched to the configured recipients.  
Notification failures are logged but do **not** cause the API to return an error.

---

## Environment Variable Reference

```bash
# SMTP
export MAIL_HOST=smtp.gmail.com
export MAIL_PORT=587
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password

# Alert recipient
export ALERT_RECIPIENT_EMAIL=admin@your-org.com

# Twilio
export TWILIO_ACCOUNT_SID=ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
export TWILIO_AUTH_TOKEN=your_auth_token
export TWILIO_FROM_NUMBER=+14155552671
export TWILIO_TO_NUMBER=+919876543210
```

---

## Key Endpoints

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/incidents/sos` | Create SOS incident (triggers email + SMS) |
| `GET` | `/api/incidents` | List active incidents |
| `GET` | `/api/incidents/{id}` | Get incident by ID |
| `PATCH` | `/api/incidents/{id}/status` | Update incident status |
| `GET` | `/api/incidents/heatmap` | Heatmap data |
| `GET` | `/api/incidents/nearby` | Nearby active incidents |
| `POST` | `/api/dispatch` | Dispatch nearest responder |
| `GET` | `/api/alerts/test/email` | Test email alert (dev only) |
| `GET` | `/api/alerts/test/sms` | Test SMS alert (dev only) |
| `GET` | `/alerts` | List stored alerts |
| `POST` | `/alerts` | Create stored alert |

---

## Security

- Credentials are **never** committed to source control.
- Use environment variables or `application-local.properties` (git-ignored).
- See `.gitignore` for the full list of excluded secret-file patterns.
