package com.RAKSHA.NET.service;


import com.RAKSHA.NET.dto.StatusUpdateRequest;
import com.RAKSHA.NET.model.CitizenStatusUpdate;
import com.RAKSHA.NET.repository.CitizenStatusUpdateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CitizenStatusService {

    private final CitizenStatusUpdateRepository citizenStatusUpdateRepository;

    public CitizenStatusUpdate create(StatusUpdateRequest req) {
        CitizenStatusUpdate update = CitizenStatusUpdate.builder()
                .userId(req.getUserId())
                .status(req.getStatus())
                .location(req.getLocation())
                .timestamp(Instant.now())
                .build();

        return citizenStatusUpdateRepository.save(update);
    }
}