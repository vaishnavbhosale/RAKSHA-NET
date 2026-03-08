package com.RAKSHA.NET.service;


import com.RAKSHA.NET.dto.SosRequest;
import com.raksha.net.entity.Incident;
import com.raksha.net.enums.IncidentStatus;
import com.raksha.net.repository.IncidentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;

    // Constructor injection
    public IncidentService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    /**
     * Convert SosRequest DTO into Incident entity, set status ACTIVE, and save.
     */
    public Incident createIncident(SosRequest request) {
        Incident incident = new Incident();

        // TODO: Map SosRequest -> Incident (edit these to match your real fields)
        // Example:
        // incident.setLatitude(request.getLatitude());
        // incident.setLongitude(request.getLongitude());
        // incident.setMessage(request.getMessage());
        // incident.setUserId(request.getUserId());
        // incident.setPhoneNumber(request.getPhoneNumber());
        // incident.setAddress(request.getAddress());

        incident.setStatus(IncidentStatus.ACTIVE);

        return incidentRepository.save(incident);
    }

    public List<Incident> getAllIncidents() {
        return incidentRepository.findAll();
    }

    public Incident updateIncidentStatus(Long id, IncidentStatus status) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found with id: " + id));

        incident.setStatus(status);
        return incidentRepository.save(incident);
    }
}