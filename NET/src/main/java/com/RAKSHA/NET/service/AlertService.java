package com.RAKSHA.NET.service;

import com.RAKSHA.NET.model.Alert;
import com.RAKSHA.NET.repository.AlertRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertService {

    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public Alert createAlert(Alert alert) {

        alert.setCreatedAt(LocalDateTime.now());

        return alertRepository.save(alert);
    }

    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }
}