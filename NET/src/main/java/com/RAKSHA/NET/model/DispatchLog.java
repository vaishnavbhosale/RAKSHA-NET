package com.RAKSHA.NET.model;


import com.RAKSHA.NET.enums.IncidentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "dispatch_logs", indexes = {
        @Index(name = "idx_dispatch_incident", columnList = "incidentId"),
        @Index(name = "idx_dispatch_responder", columnList = "responderId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DispatchLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long incidentId;

    @Column(nullable = false)
    private Long responderId;

    @Column(nullable = false)
    private Instant dispatchTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentStatus status;
}