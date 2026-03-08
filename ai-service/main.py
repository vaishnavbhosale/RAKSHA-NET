"""
RAKSHA-NET — FastAPI AI Scoring Microservice
Exposes /validate and /health endpoints on port 8001.
"""

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field
from typing import Optional
from datetime import datetime
import uuid

from scorer import calculate_score

# ── App setup ──────────────────────────────────────────────────────────────
app = FastAPI(
    title="RAKSHA-NET AI Scoring Service",
    description="Rule-based confidence scoring engine for emergency SOS incidents",
    version="1.0.0",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# ── Request / Response models ──────────────────────────────────────────────
class IncidentRequest(BaseModel):
    id: Optional[str] = Field(default_factory=lambda: str(uuid.uuid4()))
    lat: float = 19.0760
    lng: float = 72.8777
    type: str = "OTHER"
    reporterCount: int = 1
    source: str = "APP"
    description: str = ""
    timestamp: Optional[str] = Field(
        default_factory=lambda: datetime.utcnow().isoformat() + "Z"
    )


class ScoreResponse(BaseModel):
    incidentId: str
    confidenceScore: int
    severity: str
    validated: bool
    status: str


class HealthResponse(BaseModel):
    status: str
    service: str


# ── Endpoints ──────────────────────────────────────────────────────────────
@app.post("/validate", response_model=ScoreResponse)
async def validate_incident(incident: IncidentRequest):
    """Score an SOS incident and return confidence, severity, and validation."""
    result = calculate_score(incident.model_dump())

    status = "VALIDATED" if result["validated"] else "PENDING"
    if result["confidenceScore"] < 30:
        status = "REJECTED"

    return ScoreResponse(
        incidentId=incident.id,
        confidenceScore=result["confidenceScore"],
        severity=result["severity"],
        validated=result["validated"],
        status=status,
    )


@app.get("/health", response_model=HealthResponse)
async def health_check():
    """Service health check."""
    return HealthResponse(status="ok", service="raksha-ai")


# ── Run directly ───────────────────────────────────────────────────────────
if __name__ == "__main__":
    import uvicorn

    uvicorn.run("main:app", host="0.0.0.0", port=8001, reload=True)
