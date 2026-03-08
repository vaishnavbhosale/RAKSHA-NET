"""
RAKSHA-NET — Incident Data Seeder
Generates and POSTs 50 fake Mumbai incidents to the Spring Boot backend.
"""

import uuid
import random
import time
import requests
from datetime import datetime, timedelta
from dotenv import load_dotenv
import os

load_dotenv()

# ── Configuration ──────────────────────────────────────────────────────────
BACKEND_URL = os.getenv("BACKEND_URL", "http://localhost:8080/api/sos")
TOTAL_INCIDENTS = 50
DELAY_BETWEEN_POSTS = 0.3  # seconds

# ── Mumbai coordinate bounds ──────────────────────────────────────────────
LAT_MIN, LAT_MAX = 18.89, 19.27
LNG_MIN, LNG_MAX = 72.77, 73.00

# ── Weighted random distributions ─────────────────────────────────────────
TYPES = ["FIRE", "FLOOD", "MEDICAL", "VIOLENCE", "OTHER"]
TYPE_WEIGHTS = [20, 15, 40, 15, 10]

SOURCES = ["APP", "IOT", "SOCIAL", "BLE"]
SOURCE_WEIGHTS = [50, 20, 20, 10]

# ── Realistic descriptions per type ───────────────────────────────────────
DESCRIPTIONS = {
    "FIRE": [
        "Building fire reported in residential area",
        "Smoke seen from factory, fire spreading",
        "Kitchen fire in apartment complex",
        "Electrical fire in commercial building",
        "Fire in slum area, multiple huts affected",
    ],
    "FLOOD": [
        "Heavy waterlogging on main road",
        "Flooding in low-lying residential area",
        "Nallah overflow causing street flooding",
        "Rainwater flooding ground floor homes",
        "Underpass fully submerged, vehicles stuck",
    ],
    "MEDICAL": [
        "Person collapsed on street, needs ambulance",
        "Heart attack reported, elderly patient",
        "Road accident, multiple injuries",
        "Pregnant woman needs emergency transport",
        "Child having severe allergic reaction",
        "Snake bite victim needs anti-venom",
        "Heat stroke case at construction site",
    ],
    "VIOLENCE": [
        "Street fight with weapons reported",
        "Domestic violence, screaming heard",
        "Robbery in progress at shop",
        "Group clash near railway station",
        "Assault reported near bus stop",
    ],
    "OTHER": [
        "Suspicious package found near station",
        "Gas leak detected in building",
        "Building structure appears unsafe, tilting",
        "Stray animal attack reported",
        "Tree fell on road blocking traffic",
    ],
}


def generate_incident(index: int) -> dict:
    """Generate a single realistic fake Mumbai incident."""
    incident_type = random.choices(TYPES, weights=TYPE_WEIGHTS, k=1)[0]
    source = random.choices(SOURCES, weights=SOURCE_WEIGHTS, k=1)[0]

    # Timestamp spread across last 2 hours
    offset_seconds = random.randint(0, 7200)
    ts = datetime.utcnow() - timedelta(seconds=offset_seconds)

    return {
        "id": str(uuid.uuid4()),
        "lat": round(random.uniform(LAT_MIN, LAT_MAX), 6),
        "lng": round(random.uniform(LNG_MIN, LNG_MAX), 6),
        "type": incident_type,
        "reporterCount": random.randint(1, 8),
        "source": source,
        "description": random.choice(DESCRIPTIONS[incident_type]),
        "timestamp": ts.isoformat() + "Z",
    }


def seed_incidents():
    """Generate and POST 50 fake incidents to the backend."""
    print("=" * 65)
    print("  RAKSHA-NET Incident Seeder")
    print(f"  Target: {BACKEND_URL}")
    print(f"  Incidents: {TOTAL_INCIDENTS}")
    print("=" * 65)
    print()

    stats = {"success": 0, "failed": 0, "types": {}, "sources": {}}

    for i in range(1, TOTAL_INCIDENTS + 1):
        incident = generate_incident(i)

        try:
            response = requests.post(BACKEND_URL, json=incident, timeout=5)
            if response.status_code in (200, 201):
                stats["success"] += 1
                status_icon = "✅"
            else:
                stats["failed"] += 1
                status_icon = "⚠️"
        except requests.exceptions.ConnectionError:
            stats["failed"] += 1
            status_icon = "❌"
        except requests.exceptions.Timeout:
            stats["failed"] += 1
            status_icon = "⏱️"

        # Track stats
        t = incident["type"]
        s = incident["source"]
        stats["types"][t] = stats["types"].get(t, 0) + 1
        stats["sources"][s] = stats["sources"].get(s, 0) + 1

        print(
            f"  {status_icon} Seeded {i:>2}/{TOTAL_INCIDENTS} | "
            f"{t:<8} | {s:<6} | reporters: {incident['reporterCount']} | "
            f"({incident['lat']:.4f}, {incident['lng']:.4f})"
        )

        time.sleep(DELAY_BETWEEN_POSTS)

    # ── Summary table ──────────────────────────────────────────────────
    print()
    print("=" * 65)
    print("  SEEDING SUMMARY")
    print("=" * 65)
    print(f"  ✅ Successful : {stats['success']}")
    print(f"  ❌ Failed     : {stats['failed']}")
    print()
    print("  ── By Type ──────────────────────────")
    for t in TYPES:
        count = stats["types"].get(t, 0)
        bar = "█" * count
        print(f"    {t:<10} : {count:>3}  {bar}")
    print()
    print("  ── By Source ────────────────────────")
    for s in SOURCES:
        count = stats["sources"].get(s, 0)
        bar = "█" * count
        print(f"    {s:<8} : {count:>3}  {bar}")
    print()
    print("=" * 65)
    print("  Seeding complete!")
    print("=" * 65)


if __name__ == "__main__":
    seed_incidents()
