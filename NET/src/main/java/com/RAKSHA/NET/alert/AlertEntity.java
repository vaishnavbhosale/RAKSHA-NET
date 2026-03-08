package com.RAKSHA.NET.alert;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class AlertEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String message;

    private String emergencyType;

    private String priority;

    private double latitude;

    private double longitude;

    private double radiusKm;

    private LocalDateTime createdAt;

    public AlertEntity() {}

    public AlertEntity(String title, String message, String emergencyType, String priority,
                 double latitude, double longitude, double radiusKm) {

        this.title = title;
        this.message = message;
        this.emergencyType = emergencyType;
        this.priority = priority;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radiusKm = radiusKm;
        this.createdAt = LocalDateTime.now();
    }
}
