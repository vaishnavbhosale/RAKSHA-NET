package com.RAKSHA.NET.service;

import com.RAKSHA.NET.dto.SosRequest;
import com.RAKSHA.NET.enums.IncidentStatus;
import com.RAKSHA.NET.model.Incident;
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

        return incidentRepository.save(incident);
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
}