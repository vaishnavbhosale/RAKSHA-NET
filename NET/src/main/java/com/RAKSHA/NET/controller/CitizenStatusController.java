package com.RAKSHA.NET.controller;


import com.RAKSHA.NET.dto.StatusUpdateRequest;
import com.RAKSHA.NET.model.CitizenStatusUpdate;
import com.RAKSHA.NET.service.CitizenStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/status")
@RequiredArgsConstructor
public class CitizenStatusController {

    private final CitizenStatusService citizenStatusService;

    // 6. CITIZEN STATUS SYSTEM
    @PostMapping
    public ResponseEntity<CitizenStatusUpdate> create(@Valid @RequestBody StatusUpdateRequest req) {
        return ResponseEntity.ok(citizenStatusService.create(req));
    }
}
