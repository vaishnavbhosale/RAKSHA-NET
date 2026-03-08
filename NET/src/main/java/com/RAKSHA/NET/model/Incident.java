package com.RAKSHA.NET.model;

import com.RAKSHA.NET.enums.IncidentStatus;
import com.RAKSHA.NET.enums.IncidentTypes;
import jakarta.persistence.*;
import lombok.*;

import javax.print.attribute.standard.Severity;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Incident {

    @Id
    @GeneratedValue
    private Long id;

    private double latitude;
    private double longitude;

    @Enumerated(EnumType.STRING)
    private IncidentTypes type;

    @Enumerated(EnumType.STRING)
    private Severity severity;

    @Enumerated(EnumType.STRING)
    private IncidentStatus status;
}