package com.RAKSHA.NET.dto;

import com.RAKSHA.NET.enums.Severity;
import com.RAKSHA.NET.enums.IncidentTypes;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SosRequest {

    @NotNull
    @DecimalMin(value = "-90.0", message = "latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "latitude must be <= 90")
    private Double latitude;

    @NotNull
    @DecimalMin(value = "-180.0", message = "longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "longitude must be <= 180")
    private Double longitude;

    @NotNull
    private IncidentTypes incidentType;

    @NotNull
    private Severity severity;

    @Size(max = 2000)
    private String description;
}
