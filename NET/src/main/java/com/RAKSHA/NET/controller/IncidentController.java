package com.RAKSHA.NET.controller;


import com.RAKSHA.NET.dto.SosRequest;
import com.RAKSHA.NET.enums.IncidentStatus;
import com.RAKSHA.NET.model.Incident;
import com.RAKSHA.NET.service.IncidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService incidentService;

    @PostMapping("/sos")
    public ResponseEntity<Incident> createSos(@Valid @RequestBody SosRequest req) {
        return ResponseEntity.ok(incidentService.createSosIncident(req));
    }

    @GetMapping
    public ResponseEntity<List<Incident>> getActive() {
        return ResponseEntity.ok(incidentService.getActiveIncidents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Incident> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(incidentService.getIncident(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Incident> patchStatus(@PathVariable Long id, @RequestParam IncidentStatus status) {
        return ResponseEntity.ok(incidentService.updateStatus(id, status));
    }

    @GetMapping("/heatmap")
    public ResponseEntity<List<Map<String, Double>>> heatmap() {
        return ResponseEntity.ok(incidentService.heatmapActive());
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<Incident>> nearby(@RequestParam double lat,
                                                 @RequestParam double lng,
                                                 @RequestParam double radius) {
        return ResponseEntity.ok(incidentService.getNearby(lat, lng, radius));
    }
}
