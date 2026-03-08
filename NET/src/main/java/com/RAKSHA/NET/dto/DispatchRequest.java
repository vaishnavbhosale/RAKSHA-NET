package com.RAKSHA.NET.dto;


import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DispatchRequest {

    @NotNull
    private Long incidentId;
}