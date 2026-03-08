package com.RAKSHA.NET.service;

import com.RAKSHA.NET.dto.SosRequest;
import com.RAKSHA.NET.enums.IncidentStatus;
import com.RAKSHA.NET.model.Incident;
import com.RAKSHA.NET.notification.EmailService;
import com.RAKSHA.NET.notification.NotificationProperties;
import com.RAKSHA.NET.notification.SmsService;
import com.RAKSHA.NET.repository.IncidentRepository;
import com.RAKSHA.NET.Util.GeoUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IncidentService {

    public static final String CACHE_ACTIVE_INCIDENTS = "active_incidents";

    private final IncidentRepository incidentRepository;
    private final EmailService emailService;
    private final SmsService smsService;
    private final NotificationProperties notificationProperties;

    @CacheEvict(cacheNames = CACHE_ACTIVE_INCIDENTS, allEntries = true)
    public Incident createSosIncident(SosRequest req) {
        Incident incident = Incident.builder()
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .incidentTypes(req.getIncidentType())
                .severity(req.getSeverity())
                .description(req.getDescription())
                .status(IncidentStatus.ACTIVE)
                .build();

        Incident saved = incidentRepository.save(incident);

        String subject = "🚨 RAKSHA-NET Emergency Alert – " + req.getIncidentType();
        String body = buildAlertMessage(saved);
        emailService.sendEmail(notificationProperties.getRecipientEmail(), subject, body);
        smsService.sendSms(body);

        return saved;
    }

    @Cacheable(cacheNames = CACHE_ACTIVE_INCIDENTS)
    public List<Incident> getActiveIncidents() {
        return incidentRepository.findByStatus(IncidentStatus.ACTIVE);
    }

    public Incident getIncident(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Incident not found: " + id));
    }

    @CacheEvict(cacheNames = CACHE_ACTIVE_INCIDENTS, allEntries = true)
    public Incident updateStatus(Long id, IncidentStatus newStatus) {
        Incident incident = getIncident(id);
        incident.setStatus(newStatus);
        return incidentRepository.save(incident);
    }

    public List<Map<String, Double>> heatmapActive() {
        return getActiveIncidents().stream()
                .map(i -> Map.of("latitude", i.getLatitude(), "longitude", i.getLongitude()))
                .toList();
    }

    public List<Incident> getNearby(double lat, double lng, double radiusKm) {
        // Simple in-memory filter. For city-scale, replace with PostGIS query or bounding-box + DB filtering.
        return getActiveIncidents().stream()
                .filter(i -> GeoUtils.haversineKm(lat, lng, i.getLatitude(), i.getLongitude()) <= radiusKm)
                .toList();
    }

    private String buildAlertMessage(Incident incident) {
        return String.format(
                "RAKSHA-NET Emergency Alert%n" +
                "Incident ID : %d%n" +
                "Type        : %s%n" +
                "Severity    : %s%n" +
                "Location    : %.6f, %.6f%n" +
                "Description : %s",
                incident.getId(),
                incident.getIncidentTypes(),
                incident.getSeverity(),
                incident.getLatitude(),
                incident.getLongitude(),
                incident.getDescription() != null ? incident.getDescription() : "N/A"
        );
    }
}