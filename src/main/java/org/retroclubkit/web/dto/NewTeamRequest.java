package org.retroclubkit.web.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
public class NewTeamRequest {
    private UUID id;
    private String name;
    private String country;
    private long trophies;
}
