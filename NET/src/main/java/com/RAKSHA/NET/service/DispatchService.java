package com.RAKSHA.NET.service;


import com.RAKSHA.NET.dto.DispatchRequest;
import com.RAKSHA.NET.enums.IncidentStatus;
import com.RAKSHA.NET.enums.ResponderStatus;
import com.RAKSHA.NET.model.DispatchLog;
import com.RAKSHA.NET.model.Incident;
import com.RAKSHA.NET.model.Responder;
import com.RAKSHA.NET.repository.DispatchLogRepository;
import com.RAKSHA.NET.repository.ResponderRepository;
import com.RAKSHA.NET.Util.GeoUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DispatchService {

    private final IncidentService incidentService;
    private final ResponderRepository responderRepository;
    private final DispatchLogRepository dispatchLogRepository;

    @CacheEvict(cacheNames = IncidentService.CACHE_ACTIVE_INCIDENTS, allEntries = true)
    public DispatchLog dispatchNearest(DispatchRequest req) {
        Incident incident = incidentService.getIncident(req.getIncidentId());

        if (incident.getStatus() != IncidentStatus.ACTIVE) {
            throw new IllegalArgumentException("Incident is not ACTIVE. Current status: " + incident.getStatus());
        }

        List<Responder> available = responderRepository.findByStatus(ResponderStatus.AVAILABLE);
        if (available.isEmpty()) {
            throw new IllegalArgumentException("No available responders");
        }

        Responder nearest = available.stream()
                .min(Comparator.comparingDouble(r ->
                        GeoUtils.haversineKm(
                                incident.getLatitude(),
                                incident.getLongitude(),
                                r.getLatitude(),
                                r.getLongitude()
                        )))
                .orElseThrow(() -> new EntityNotFoundException("No responders found"));

        // Assign responder
        nearest.setStatus(ResponderStatus.BUSY);
        responderRepository.save(nearest);

        // Update incident status
        incidentService.updateStatus(incident.getId(), IncidentStatus.DISPATCHED);

        DispatchLog log = DispatchLog.builder()
                .incidentId(incident.getId())
                .responderId(nearest.getId())
                .dispatchTime(Instant.now())
                .status(IncidentStatus.DISPATCHED)
                .build();

        return dispatchLogRepository.save(log);
    }
}
