"""
RAKSHA-NET — BLE Mesh / Live SOS Simulator
Continuously sends random SOS incidents to the Spring Boot backend,
clustered around 3 Mumbai hotspot zones.
"""

import uuid
import random
import time
import signal
import sys
import requests
from datetime import datetime
from dotenv import load_dotenv
import os

load_dotenv()

# ── Configuration ──────────────────────────────────────────────────────────
BACKEND_URL = os.getenv("BACKEND_URL", "http://localhost:8080/api/sos")
MIN_INTERVAL = 8   # seconds
MAX_INTERVAL = 15  # seconds
JITTER = 0.005     # ± coordinate jitter

# ── Hotspot zones ──────────────────────────────────────────────────────────
HOTSPOTS = {
    "Dharavi":  {"lat": 19.0411, "lng": 72.8545},
    "Kurla":    {"lat": 19.0728, "lng": 72.8826},
    "Andheri":  {"lat": 19.1136, "lng": 72.8697},
}

# ── Incident rotation ─────────────────────────────────────────────────────
TYPES = ["FIRE", "FLOOD", "MEDICAL", "VIOLENCE", "OTHER"]
TYPE_WEIGHTS = [15, 10, 45, 20, 10]

SOURCES = ["BLE", "IOT", "APP", "SOCIAL"]
SOURCE_WEIGHTS = [40, 25, 25, 10]

DESCRIPTIONS = {
    "FIRE": [
        "BLE node detected smoke and heat spike",
        "Fire alarm triggered via mesh sensor",
        "Thermal anomaly reported by IoT node",
    ],
    "FLOOD": [
        "Water level sensor breach detected",
        "BLE mesh flood warning — water rising fast",
        "Low-area flooding detected by sensor grid",
    ],
    "MEDICAL": [
        "Emergency medical assist requested via BLE beacon",
        "Fall detection triggered on wearable device",
        "SOS pulse detected — possible cardiac event",
        "Mesh node relayed ambulance request",
    ],
    "VIOLENCE": [
        "Panic button pressed on BLE device",
        "Sound-level spike detected — possible altercation",
        "Multiple SOS beacons activated in area",
    ],
    "OTHER": [
        "Generic SOS beacon activated",
        "Unclassified distress signal received",
        "BLE mesh relay — unknown emergency type",
    ],
}

# ── Graceful shutdown ──────────────────────────────────────────────────────
running = True
sent_count = 0


def signal_handler(sig, frame):
    global running
    print(f"\n\n  🛑 Stopping simulator... ({sent_count} incidents sent)")
    running = False


signal.signal(signal.SIGINT, signal_handler)


def generate_live_incident() -> dict:
    """Generate a single live SOS incident clustered around a random hotspot."""
    zone_name = random.choice(list(HOTSPOTS.keys()))
    zone = HOTSPOTS[zone_name]

    lat = zone["lat"] + random.uniform(-JITTER, JITTER)
    lng = zone["lng"] + random.uniform(-JITTER, JITTER)

    incident_type = random.choices(TYPES, weights=TYPE_WEIGHTS, k=1)[0]
    source = random.choices(SOURCES, weights=SOURCE_WEIGHTS, k=1)[0]

    return {
        "id": str(uuid.uuid4()),
        "lat": round(lat, 6),
        "lng": round(lng, 6),
        "type": incident_type,
        "reporterCount": random.randint(1, 6),
        "source": source,
        "description": random.choice(DESCRIPTIONS[incident_type]),
        "timestamp": datetime.utcnow().isoformat() + "Z",
    }, zone_name


def run_simulator():
    """Main simulation loop — runs until Ctrl+C."""
    global sent_count

    print("=" * 65)
    print("  RAKSHA-NET BLE Mesh Simulator")
    print(f"  Target: {BACKEND_URL}")
    print(f"  Interval: {MIN_INTERVAL}-{MAX_INTERVAL}s")
    print(f"  Hotspots: {', '.join(HOTSPOTS.keys())}")
    print("  Press Ctrl+C to stop")
    print("=" * 65)
    print()

    while running:
        incident, zone = generate_live_incident()
        now = datetime.now().strftime("%H:%M:%S")

        try:
            response = requests.post(BACKEND_URL, json=incident, timeout=5)
            sent_count += 1

            if response.status_code in (200, 201):
                icon = "📡"
            else:
                icon = "⚠️"

            print(
                f"  {icon} [{now}] #{sent_count:>4} | "
                f"{zone:<8} | {incident['type']:<10} | {incident['source']:<6} | "
                f"reporters: {incident['reporterCount']} | "
                f"({incident['lat']:.4f}, {incident['lng']:.4f})"
            )

        except requests.exceptions.ConnectionError:
            sent_count += 1
            print(
                f"  ❌ [{now}] #{sent_count:>4} | "
                f"Connection refused — is backend running at {BACKEND_URL}?"
            )
        except requests.exceptions.Timeout:
            sent_count += 1
            print(f"  ⏱️ [{now}] #{sent_count:>4} | Request timed out")

        # Random delay between 8-15 seconds
        delay = random.uniform(MIN_INTERVAL, MAX_INTERVAL)
        # Check `running` every 0.5s so Ctrl+C is responsive
        elapsed = 0
        while running and elapsed < delay:
            time.sleep(0.5)
            elapsed += 0.5

    print()
    print("=" * 65)
    print(f"  Simulator stopped. Total incidents sent: {sent_count}")
    print("=" * 65)


if __name__ == "__main__":
    run_simulator()
