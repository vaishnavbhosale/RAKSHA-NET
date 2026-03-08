package com.RAKSHA.NET.controller;

import com.RAKSHA.NET.model.Alert;
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

    @PostMapping
    public Alert createAlert(@RequestBody Alert alert) {
        return alertService.createAlert(alert);
    }

    @GetMapping
    public List<Alert> getAlerts() {
        return alertService.getAllAlerts();
    }
}