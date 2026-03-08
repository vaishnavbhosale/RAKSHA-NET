/**
 * RAKSHA-NET — Shared Frontend JavaScript
 * API integration, live clock, demo mode fallback
 */

const API = {
    AI_SERVICE: 'http://localhost:8001',
    BACKEND: 'http://localhost:8080/api/sos',
};

// ── API Helpers ──────────────────────────────────────────────
async function validateIncident(incident) {
    try {
        const res = await fetch(`${API.AI_SERVICE}/validate`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(incident),
        });
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        return await res.json();
    } catch (e) {
        console.warn('[RAKSHA] AI service offline, using demo scoring:', e.message);
        return demoScore(incident);
    }
}

async function sendSOS(incident) {
    try {
        const res = await fetch(API.BACKEND, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(incident),
        });
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        return await res.json();
    } catch (e) {
        console.warn('[RAKSHA] Backend offline:', e.message);
        return null;
    }
}

async function checkHealth() {
    try {
        const res = await fetch(`${API.AI_SERVICE}/health`);
        const data = await res.json();
        return data.status === 'ok';
    } catch { return false; }
}

// ── Demo Mode (when backend is offline) ─────────────────────
function demoScore(incident) {
    let conf = 40;
    const rc = incident.reporterCount || 1;
    if (rc >= 5) conf = 98;
    else if (rc >= 3) conf = 85;
    else if (rc >= 2) conf = 65;
    if (['FIRE', 'FLOOD'].includes(incident.type)) conf += 10;
    if (incident.source === 'IOT') conf += 15;
    else if (incident.source === 'BLE') conf += 5;
    else if (incident.source === 'SOCIAL') conf -= 10;
    conf = Math.min(99, Math.max(0, conf));
    let sev = conf >= 90 ? 'CRITICAL' : conf >= 70 ? 'HIGH' : conf >= 50 ? 'MEDIUM' : 'LOW';
    return {
        incidentId: crypto.randomUUID(),
        confidenceScore: conf,
        severity: sev,
        validated: conf >= 60,
        status: conf >= 60 ? 'VALIDATED' : conf < 30 ? 'REJECTED' : 'PENDING'
    };
}

const MUMBAI_ZONES = [
    { name: 'Dharavi', lat: 19.0411, lng: 72.8545 },
    { name: 'Kurla', lat: 19.0728, lng: 72.8826 },
    { name: 'Andheri', lat: 19.1136, lng: 72.8697 },
    { name: 'Bandra', lat: 19.0596, lng: 72.8295 },
    { name: 'Dadar', lat: 19.0178, lng: 72.8478 },
    { name: 'Colaba', lat: 18.9067, lng: 72.8147 },
    { name: 'Borivali', lat: 19.2307, lng: 72.8567 },
];

const INCIDENT_TYPES = ['FIRE', 'FLOOD', 'MEDICAL', 'EARTHQUAKE', 'VIOLENCE', 'OTHER'];
const SOURCES = ['APP', 'IOT', 'BLE', 'SOCIAL'];
const DESCRIPTIONS = {
    FIRE: ['Building fire reported', 'Smoke detected in area', 'Electrical fire spreading'],
    FLOOD: ['Heavy waterlogging', 'Nallah overflow detected', 'Underpass submerged'],
    MEDICAL: ['Cardiac arrest reported', 'Road accident injuries', 'Fall detection alert'],
    EARTHQUAKE: ['Tremors detected in area', 'Seismic activity 2.1M', 'Building collapse reported'],
    VIOLENCE: ['Street altercation reported', 'Panic button activated', 'Break-in reported'],
    OTHER: ['Gas leak detected', 'Structural damage warning', 'Suspicious activity'],
};

function generateMockIncident() {
    const zone = MUMBAI_ZONES[Math.floor(Math.random() * MUMBAI_ZONES.length)];
    const type = INCIDENT_TYPES[Math.floor(Math.random() * INCIDENT_TYPES.length)];
    const source = SOURCES[Math.floor(Math.random() * SOURCES.length)];
    return {
        id: crypto.randomUUID().slice(0, 8).toUpperCase(),
        lat: zone.lat + (Math.random() - 0.5) * 0.01,
        lng: zone.lng + (Math.random() - 0.5) * 0.01,
        type,
        reporterCount: Math.floor(Math.random() * 7) + 1,
        source,
        description: DESCRIPTIONS[type][Math.floor(Math.random() * DESCRIPTIONS[type].length)],
        timestamp: new Date().toISOString(),
        zone: zone.name
    };
}

// ── Live Clock ──────────────────────────────────────────────
function startClock(elementId) {
    const el = document.getElementById(elementId);
    if (!el) return;
    function tick() {
        const now = new Date();
        el.textContent = now.toTimeString().split(' ')[0];
    }
    tick();
    setInterval(tick, 1000);
}

// ── Status Indicator ────────────────────────────────────────
async function updateStatus(elementId) {
    const el = document.getElementById(elementId);
    if (!el) return;
    const online = await checkHealth();
    el.innerHTML = online
        ? '<span class="size-2 rounded-full bg-primary animate-pulse inline-block"></span> SYSTEM_ONLINE'
        : '<span class="size-2 rounded-full bg-orange-500 inline-block"></span> DEMO_MODE';
}

// Severity color helper
function severityColor(sev) {
    switch (sev) {
        case 'CRITICAL': return 'text-red-500';
        case 'HIGH': return 'text-orange-400';
        case 'MEDIUM': return 'text-yellow-400';
        case 'LOW': return 'text-primary';
        default: return 'text-primary/60';
    }
}

function severityBorder(sev) {
    switch (sev) {
        case 'CRITICAL': return 'border-red-500/50 bg-red-500/10';
        case 'HIGH': return 'border-orange-400/50 bg-orange-400/10';
        case 'MEDIUM': return 'border-yellow-400/50 bg-yellow-400/10';
        case 'LOW': return 'border-primary/50 bg-primary/10';
        default: return 'border-primary/20';
    }
}

// ── Shared Profile Modal ─────────────────────────────────────
// Each page can call toggleProfile(sosCountElementId) or just toggleProfile()
function toggleProfile(sosCountId) {
    const modal = document.getElementById('profile-modal');
    if (!modal) return;
    if (modal.classList.contains('hidden')) {
        modal.classList.remove('hidden');
        modal.classList.add('flex');
        const history = JSON.parse(localStorage.getItem('raksha_sos_history') || '[]');
        // Try to find the SOS count span by provided ID or common IDs
        const ids = [sosCountId, 'profile-sos-count', 'sos-profile-count', 'profile-sos-count-hm', 'cmd-sos-count'];
        ids.filter(Boolean).forEach(id => {
            const el = document.getElementById(id);
            if (el) el.textContent = history.length;
        });
    } else {
        modal.classList.add('hidden');
        modal.classList.remove('flex');
    }
}
