package org.retroclubkit.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatedNewTeam {

    private UUID id;
    @NotNull(message = "Team name cannot be null!")
    private String name;
    @NotNull(message = "Country cannot be null!")
    private String country;
}
