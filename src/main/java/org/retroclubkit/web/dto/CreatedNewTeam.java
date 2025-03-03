package org.retroclubkit.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class CreatedNewTeam {

    private UUID id;
    @NotNull(message = "Team name cannot be null!")
    private String name;
    @NotNull(message = "Country cannot be null!")
    private String country;
}
