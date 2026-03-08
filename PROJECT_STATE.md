# RAKSHA-NET — Member 3 AI/Integration State
Last Updated: 2026-03-08T11:40:00+05:30
Member: Dhanesh / Agent: Antigravity

## COMPLETION STATUS
- [x] requirements.txt created
- [x] scorer.py — rule-based AI logic ✅ tested
- [x] main.py — FastAPI microservice running on 8001 ✅ tested
- [x] seeder.py — 50 fake Mumbai incidents (ready, needs backend)
- [x] simulator.py — live BLE simulator (ready, needs backend)
- [x] .env configured
- [x] tested POST /validate endpoint ✅
- [ ] tested seeder against backend (needs Spring Boot running)
- [ ] simulator running and confirmed (needs Spring Boot running)

## LAST COMPLETED TASK
Built and verified all 4 core files. Migrated into team GitHub repo.

## NEXT TASK
Test seeder.py and simulator.py once Spring Boot backend is running on port 8080.

## KNOWN ISSUES / BLOCKERS
- Seeder and Simulator require Spring Boot backend at http://localhost:8080/api/sos to be running
- Both scripts will gracefully handle connection errors if backend is down

## HOW TO RUN
```bash
cd ai-service
pip install -r requirements.txt

# Start AI scoring service
python -m uvicorn main:app --port 8001

# Seed 50 fake incidents (needs Spring Boot backend)
python seeder.py

# Start live BLE simulation (needs Spring Boot backend)
python simulator.py
```

## BACKEND URL
http://localhost:8080/api/sos

## TEST RESULTS
### Scorer Self-Test
- 5 reporters + FIRE + IOT → confidence: 99, CRITICAL, validated ✅
- 3 reporters + MEDICAL + APP → confidence: 85, HIGH, validated ✅
- 2 reporters + FLOOD + BLE → confidence: 80, HIGH, validated ✅
- 1 reporter + VIOLENCE + SOCIAL → confidence: 30, LOW, not validated ✅
- 1 reporter + OTHER + APP → confidence: 40, LOW, not validated ✅

### FastAPI Endpoints
- GET /health → {"status": "ok", "service": "raksha-ai"} ✅
- POST /validate → Returns correct scoring response ✅
