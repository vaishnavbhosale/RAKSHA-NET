package com.RAKSHA.NET.model;

import com.RAKSHA.NET.enums.CitizenStatusService;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "citizen_status_updates", indexes = {
        @Index(name = "idx_citizen_user", columnList = "userId"),
        @Index(name = "idx_citizen_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CitizenStatusUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String userId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CitizenStatusService status;

    @NotBlank
    @Column(nullable = false, length = 512)
    private String location;

    @Column(nullable = false)
    private Instant timestamp;
}