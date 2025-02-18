package org.retroclubkit.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.util.UUID;

@Data
public class CreatedNewTeam {

    private UUID id;
    @NotNull
    private String name;
    @NotNull
    private String country;
    @Positive(message = "Must be positive")
    private long trophies;
}
