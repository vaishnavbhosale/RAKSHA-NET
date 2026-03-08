# 🛡️ RAKSHA-NET — Unified Emergency Communication & Alert System

> **PS8: Distress Signal Network** — BlueBit Hackathon 2026

RAKSHA-NET is a real-time emergency response platform that enables **citizens to report distress signals** and **authorities to broadcast multi-channel alerts** to affected populations. The system combines AI-powered incident validation, interactive geospatial mapping, and multi-channel broadcasting (SMS, Email, Push, Social Media) into a unified command and control interface.

---

## 🌐 Live Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      FRONTEND (Port 8002)                    │
│  Landing → Dashboard → Heatmap → SOS Terminal → Command Center│
└─────────────┬───────────────────────────┬───────────────────┘
              │                           │
              ▼                           ▼
┌─────────────────────┐     ┌─────────────────────────────────┐
│  AI SERVICE (8001)  │     │   SPRING BOOT BACKEND (8080)    │
│  FastAPI + Python   │     │  REST APIs + Twilio + SMTP      │
│  Incident Scoring   │     │  SOS CRUD + Dispatch + Alerts   │
│  Risk Analysis      │     │  Email/SMS Broadcasting         │
└─────────────────────┘     └─────────────────────────────────┘
```

---

## ✅ Problem Statement Coverage

| PS8 Requirement | Status | Implementation |
|---|---|---|
| **Emergency Types (≥2)** | ✅ 6 types | Fire, Flood, Medical, Earthquake, Cyclone, Security |
| **Alert Creation Dashboard** | ✅ | Command Center → CREATE_ALERT tab |
| **Message Templates** | ✅ | Auto-filled templates per emergency type |
| **Priority Levels** | ✅ | Critical / High / Medium / Low |
| **Multi-Channel Broadcasting (≥2)** | ✅ 4 channels | SMS (Twilio), Email (SMTP), Push Notifications, Social Media |
| **Location Targeting** | ✅ | GPS-based SOS + zone-targeted alerts on Leaflet maps |
| **Visualize Affected Zone** | ✅ | Interactive heatmap with risk zones |
| **Real-time Delivery Tracking (BONUS)** | ✅ | Live progress bars per channel |
| **Multi-language Alerts (BONUS)** | ✅ | English, Hindi (हिंदी), Marathi (मराठी) |
| **Two-way Feedback (BONUS)** | ✅ | Citizens report + audio recording; authorities respond via Command Center |

---

## 📁 Project Structure

```
RAKSHA-NET/
├── frontend/               # Client-side UI (HTML + Tailwind + JS)
│   ├── index.html          # Landing page with REQUEST_ACCESS
│   ├── dashboard.html      # Citizen dashboard with live map
│   ├── heatmap.html        # Risk heatmap with filters
│   ├── sos.html            # SOS distress terminal + audio recording
│   ├── command.html         # Authority command center + alert broadcasting
│   └── app.js              # Shared logic: AI scoring, broadcast sim, translations
│
├── ai-service/             # AI-powered incident analysis (Python)
│   ├── main.py             # FastAPI server (port 8001)
│   ├── scorer.py           # Gemini AI incident confidence scorer
│   ├── simulator.py        # Mock incident data generator
│   ├── seeder.py           # Database seeder
│   ├── requirements.txt    # Python dependencies
│   └── .env                # API keys (GEMINI_API_KEY)
│
├── NET/                    # Spring Boot backend (Java)
│   ├── pom.xml             # Maven dependencies (Twilio, Spring Mail)
│   └── src/main/
│       ├── java/           # Controllers, Services, Models
│       └── resources/      # application.properties
│
└── README.md               # This file
```

---

## 🚀 Quick Start

### Prerequisites

| Tool | Version | Purpose |
|---|---|---|
| Python | 3.9+ | AI service + frontend dev server |
| Java | 17+ | Spring Boot backend |
| Node.js | (optional) | Alternative frontend server |

### 1. Frontend

```bash
cd frontend
python -m http.server 8002
# Open http://localhost:8002
```

### 2. AI Service

```bash
cd ai-service
pip install -r requirements.txt

python main.py
# Runs on http://localhost:8001
```

### 3. Spring Boot Backend

```bash
cd NET

# Configure credentials (see Environment Variables below)
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
# Runs on http://localhost:8080
```

---

## 🔑 Environment Variables

### Spring Boot Backend
```bash
# SMTP (Gmail)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

# Alert Recipient
ALERT_RECIPIENT_EMAIL=admin@your-org.com

# Twilio (SMS)
TWILIO_ACCOUNT_SID=ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
TWILIO_AUTH_TOKEN=your_auth_token
TWILIO_FROM_NUMBER=+14155552671
TWILIO_TO_NUMBER=+919876543210
```

---

## 📡 API Endpoints

### Spring Boot Backend (`:8080`)

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/incidents/sos` | Create SOS incident (triggers email + SMS) |
| `GET` | `/api/incidents` | List active incidents |
| `GET` | `/api/incidents/{id}` | Get incident by ID |
| `PATCH` | `/api/incidents/{id}/status` | Update incident status |
| `GET` | `/api/incidents/heatmap` | Heatmap data |
| `GET` | `/api/incidents/nearby` | Nearby active incidents |
| `POST` | `/api/dispatch` | Dispatch nearest responder |
| `GET` | `/api/alerts/test/email` | Test email alert |
| `GET` | `/api/alerts/test/sms` | Test SMS alert |

### AI Service (`:8001`)

| Method | Path | Description |
|---|---|---|
| `POST` | `/validate` | AI incident validation + confidence scoring |
| `POST` | `/score` | Risk severity analysis |

---

## 🎨 Frontend Pages

| Page | URL | Features |
|---|---|---|
| **Landing** | `/index.html` | Request Access, system overview |
| **Dashboard** | `/dashboard.html` | Live map, incident feed, location search |
| **Heatmap** | `/heatmap.html` | Risk zone visualization, time/type/severity filters |
| **SOS Terminal** | `/sos.html` | Emergency type selection, GPS, audio recording, multi-language |
| **Command Center** | `/command.html` | Heatmap grid, incident queue, unit tracker, alert broadcasting |

---

## 🛠️ Tech Stack

| Layer | Technologies |
|---|---|
| **Frontend** | HTML5, Tailwind CSS, Vanilla JavaScript, Leaflet.js |
| **AI Service** | Python, FastAPI |
| **Backend** | Java 17, Spring Boot 4, Maven |
| **SMS** | Twilio API |
| **Email** | Spring Mail (SMTP / Gmail) |
| **Maps** | Leaflet.js + OpenStreetMap |
| **Database** | H2 (dev) / PostgreSQL (prod) |

---

## � Future Scope: AI Model Integration

Currently, the `ai-service` uses a **Python Rule-Based Engine** (`scorer.py`) to calculate incident confidence and severity. 

Because the AI logic is completely isolated as a **FastAPI microservice**, the architecture is explicitly designed to be future-proof. Without changing a single line of code in the Spring Boot backend or the Frontend, the rule-based engine can be seamlessly swapped with a **trained Machine Learning model** (e.g., Random Forest or a custom neural network) to analyze historical incident data and predict severity based on real-world patterns.

---

## �👥 Team

| Member | Role |
|---|---|
| **Member 1** | Frontend Development (UI/UX + JavaScript) |
| **Member 2** | AI Service (Python + Scoring Logic) |
| **Member 3** | Backend (Spring Boot + SMS/Email) |

---

## 🔒 Security

- Credentials are **never** committed to source control
- AES-256 encryption indicators in citizen profiles
- CORS configured for cross-origin frontend-backend communication
- `.env` and `application-local.properties` are git-ignored

---

## 📝 License

This project was built for the **BlueBit Hackathon 2026**.
