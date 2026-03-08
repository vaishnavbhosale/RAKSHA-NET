package com.RAKSHA.NET.controller;

import com.RAKSHA.NET.model.AlertEntity;
import com.RAKSHA.NET.service.AlertService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    // Create alert
    @PostMapping
    public Alertentity createAlert(@RequestBody AlertEntity alertentity) {
        return alertService.createAlert(alert);
    }

    // Get all alerts
    @GetMapping
    public List<AlertEntity> getAllAlerts() {
        return alertService.getAllAlerts();
    }

    // Get alert by id
    @GetMapping("/{id}")
    public AlertEntity getAlert(@PathVariable Long id) {
        return alertService.getAlertById(id);
    }

    // Delete alert
    @DeleteMapping("/{id}")
    public void deleteAlert(@PathVariable Long id) {
        alertService.deleteAlert(id);
    }
}
