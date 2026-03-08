/**
 * RAKSHA-NET — Shared Frontend JavaScript
 * API integration, live clock, demo mode fallback
 */

const API = {
    AI_SERVICE: 'http://localhost:8001',
    BACKEND: 'http://localhost:8080/api/incidents/sos',
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

async function sendSOS(incident, aiResult) {
    try {
        // Map AI severity to backend enum
        const severityMap = {
            'CRITICAL': 'HIGH',
            'HIGH': 'HIGH',
            'MEDIUM': 'MEDIUM',
            'LOW': 'LOW'
        };
        const sosRequest = {
            latitude: incident.lat,
            longitude: incident.lng,
            incidentType: incident.type,
            severity: severityMap[aiResult.severity] || 'MEDIUM',
            description: incident.description
        };
        const res = await fetch(API.BACKEND, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(sosRequest),
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

// ── Alert Templates ──────────────────────────────────────────
const ALERT_TEMPLATES = {
    FLOOD: {
        subject: 'FLOOD WARNING — Immediate Evacuation Required',
        body: 'CRITICAL FLOOD ALERT: Heavy waterlogging detected in {zone}. Residents must evacuate to nearest safe zone immediately. Avoid low-lying areas and underpasses. Emergency helpline: 112.',
    },
    EARTHQUAKE: {
        subject: 'EARTHQUAKE ALERT — Drop, Cover, Hold On',
        body: 'SEISMIC ACTIVITY DETECTED in {zone}. Magnitude: {magnitude}. Move to open areas away from buildings. Do not use elevators. Aftershocks expected. Emergency helpline: 112.',
    },
    FIRE: {
        subject: 'FIRE EMERGENCY — Evacuate Area',
        body: 'FIRE REPORTED in {zone}. All residents within 500m radius must evacuate immediately. Fire units dispatched. Do not use lifts. Emergency helpline: 101.',
    },
    MEDICAL: {
        subject: 'HEALTH EMERGENCY — Medical Alert',
        body: 'MEDICAL EMERGENCY in {zone}. Ambulance units dispatched. If you require immediate assistance, call 108. Nearest hospital teams activated.',
    },
    SECURITY: {
        subject: 'SECURITY THREAT — Stay Indoors',
        body: 'SECURITY ALERT for {zone}. Public safety threat detected. Residents advised to stay indoors and lock all entry points. Police units deployed. Emergency: 100.',
    },
    CYCLONE: {
        subject: 'CYCLONE WARNING — Seek Shelter Immediately',
        body: 'CYCLONE APPROACHING {zone}. Wind speeds exceeding 120 km/h expected. Move to reinforced shelters. Secure loose objects. Stock emergency supplies. Helpline: 112.',
    },
    OTHER: {
        subject: 'GENERAL EMERGENCY — Public Advisory',
        body: 'EMERGENCY ALERT registered for {zone}. Authorities are investigating the situation. Please remain calm, stay alert, and await further official instructions. Emergency: 112.',
    },
};

// ── Multi-Channel Broadcast Simulator ────────────────────────
function simulateBroadcast(alertData, onUpdate, onComplete) {
    const channels = [
        { id: 'sms', name: 'SMS', icon: 'sms', total: Math.floor(Math.random() * 5000) + 8000, delay: 300 },
        { id: 'email', name: 'EMAIL', icon: 'email', total: Math.floor(Math.random() * 3000) + 5000, delay: 500 },
        { id: 'push', name: 'PUSH_NOTIF', icon: 'notifications_active', total: Math.floor(Math.random() * 10000) + 15000, delay: 200 },
        { id: 'social', name: 'SOCIAL', icon: 'share', total: 1, delay: 800 },
    ];

    let completed = 0;
    channels.forEach(ch => {
        let sent = 0;
        const failed = Math.floor(ch.total * (Math.random() * 0.03)); // 0-3% failure
        const interval = setInterval(() => {
            sent = Math.min(sent + Math.ceil(ch.total / 10), ch.total);
            const delivered = Math.max(0, sent - failed);
            if (onUpdate) onUpdate(ch, { sent, delivered, failed, total: ch.total, progress: sent / ch.total });
            if (sent >= ch.total) {
                clearInterval(interval);
                completed++;
                if (completed === channels.length && onComplete) onComplete(channels);
            }
        }, ch.delay);
    });

    return channels;
}

// ── Multi-Language Translations ──────────────────────────────
const TRANSLATIONS = {
    en: {
        sos_title: 'SOS EMERGENCY TERMINAL',
        sos_subtitle: 'DISTRESS SIGNAL NETWORK // PRIORITY CHANNEL',
        select_emergency: 'SELECT EMERGENCY TYPE',
        fire: 'FIRE', flood: 'FLOOD', medical: 'MEDICAL', earthquake: 'EARTHQUAKE', security: 'SECURITY', cyclone: 'CYCLONE', other: 'OTHER',
        send_sos: 'TRANSMIT DISTRESS SIGNAL',
        signal_transmitted: 'SIGNAL TRANSMITTED — HELP IS ON THE WAY',
        recording: 'RECORDING AUDIO MESSAGE',
        stop_recording: 'STOP & ATTACH',
        play: 'PLAY', delete_audio: 'DELETE',
        history_title: 'SOS HISTORY / ACTIVE EMERGENCIES',
        broadcast_channels: 'BROADCASTING ON ALL CHANNELS',
        sms_sent: 'SMS ALERT SENT', email_sent: 'EMAIL DISPATCHED', push_sent: 'PUSH NOTIFICATION SENT', social_sent: 'SOCIAL MEDIA POSTED',
        language: 'LANGUAGE',
    },
    hi: {
        sos_title: 'SOS आपातकालीन टर्मिनल',
        sos_subtitle: 'संकट संकेत नेटवर्क // प्राथमिकता चैनल',
        select_emergency: 'आपातकालीन प्रकार चुनें',
        fire: 'आग', flood: 'बाढ़', medical: 'चिकित्सा', earthquake: 'भूकंप', security: 'सुरक्षा', cyclone: 'चक्रवात', other: 'अन्य',
        send_sos: 'संकट संकेत प्रसारित करें',
        signal_transmitted: 'संकेत प्रसारित — मदद आ रही है',
        recording: 'ऑडियो संदेश रिकॉर्ड हो रहा है',
        stop_recording: 'रोकें और जोड़ें',
        play: 'चलाएं', delete_audio: 'हटाएं',
        history_title: 'SOS इतिहास / सक्रिय आपातकालीन',
        broadcast_channels: 'सभी चैनलों पर प्रसारण',
        sms_sent: 'SMS भेजा गया', email_sent: 'ईमेल भेजा', push_sent: 'पुश नोटिफिकेशन भेजी', social_sent: 'सोशल मीडिया पोस्ट',
        language: 'भाषा',
    },
    mr: {
        sos_title: 'SOS आणीबाणी टर्मिनल',
        sos_subtitle: 'संकट सिग्नल नेटवर्क // प्राधान्य चॅनेल',
        select_emergency: 'आणीबाणी प्रकार निवडा',
        fire: 'आग', flood: 'पूर', medical: 'वैद्यकीय', earthquake: 'भूकंप', security: 'सुरक्षा', cyclone: 'चक्रीवादळ', other: 'इतर',
        send_sos: 'संकट सिग्नल प्रसारित करा',
        signal_transmitted: 'सिग्नल प्रसारित — मदत येत आहे',
        recording: 'ऑडिओ संदेश रेकॉर्ड होत आहे',
        stop_recording: 'थांबा आणि जोडा',
        play: 'प्ले', delete_audio: 'हटवा',
        history_title: 'SOS इतिहास / सक्रिय आणीबाणी',
        broadcast_channels: 'सर्व चॅनेलवर प्रसारण',
        sms_sent: 'SMS पाठवला', email_sent: 'ईमेल पाठवला', push_sent: 'पुश नोटिफिकेशन पाठवली', social_sent: 'सोशल मीडिया पोस्ट',
        language: 'भाषा',
    },
};

let currentLang = 'en';
function t(key) { return (TRANSLATIONS[currentLang] && TRANSLATIONS[currentLang][key]) || TRANSLATIONS.en[key] || key; }
