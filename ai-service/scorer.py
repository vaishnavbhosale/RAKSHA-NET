"""
RAKSHA-NET — Rule-Based AI Confidence Scoring Engine
Calculates confidence score, severity, and validation status for SOS incidents.
"""


def calculate_score(incident: dict) -> dict:
    """
    Evaluate an SOS incident and return confidence score, severity, and validation.

    Rules:
      1. Base confidence from reporterCount
      2. Type-based boost (FIRE/FLOOD → +10)
      3. Source-based modifier (IOT +15, BLE +5, SOCIAL -10)
      4. Validation threshold at 60
      5. Cap at 99
    """
    reporter_count = incident.get("reporterCount", 1)
    incident_type = incident.get("type", "OTHER").upper()
    source = incident.get("source", "APP").upper()

    # ── Step 1: Base confidence & severity from reporter count ──────────
    if reporter_count >= 5:
        confidence = 98
        severity = "CRITICAL"
    elif reporter_count >= 3:
        confidence = 85
        severity = "HIGH"
    elif reporter_count >= 2:
        confidence = 65
        severity = "MEDIUM"
    else:
        confidence = 40
        severity = "LOW"

    # ── Step 2: Type-based boost ────────────────────────────────────────
    if incident_type in ("FIRE", "FLOOD"):
        confidence += 10

    # ── Step 3: Source-based modifier ───────────────────────────────────
    if source == "IOT":
        confidence += 15
    elif source == "BLE":
        confidence += 5
    elif source == "SOCIAL":
        confidence -= 10

    # ── Step 4: Cap at 99 ──────────────────────────────────────────────
    confidence = min(confidence, 99)
    confidence = max(confidence, 0)

    # ── Step 5: Severity upgrade if confidence jumped high ─────────────
    if confidence >= 90:
        severity = "CRITICAL"
    elif confidence >= 70:
        severity = "HIGH"
    elif confidence >= 50:
        severity = "MEDIUM"
    else:
        severity = "LOW"

    # ── Step 6: Validation ─────────────────────────────────────────────
    validated = confidence >= 60

    return {
        "confidenceScore": confidence,
        "severity": severity,
        "validated": validated,
    }


# ── Quick self-test ────────────────────────────────────────────────────────
if __name__ == "__main__":
    test_cases = [
        {"reporterCount": 5, "type": "FIRE", "source": "IOT"},
        {"reporterCount": 3, "type": "MEDICAL", "source": "APP"},
        {"reporterCount": 2, "type": "FLOOD", "source": "BLE"},
        {"reporterCount": 1, "type": "VIOLENCE", "source": "SOCIAL"},
        {"reporterCount": 1, "type": "OTHER", "source": "APP"},
        {"reporterCount": 8, "type": "FIRE", "source": "IOT"},
    ]

    print("=" * 60)
    print("RAKSHA-NET Scorer — Self-Test")
    print("=" * 60)
    for i, tc in enumerate(test_cases, 1):
        result = calculate_score(tc)
        print(f"\nTest {i}: {tc}")
        print(f"  → Confidence: {result['confidenceScore']}")
        print(f"  → Severity:   {result['severity']}")
        print(f"  → Validated:  {result['validated']}")
    print("\n" + "=" * 60)
