package com.RAKSHA.NET.dto;

import com.RAKSHA.NET.enums.CitizenStatusService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusUpdateRequest {

    @NotBlank
    private String userId;

    @NotNull
    private CitizenStatusService status;

    @NotBlank
    @Size(max = 512)
    private String location;
}
