package org.retroclubkit.web.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Size(min = 6, message = "Username must be at least 6 characters")
    private String username;

    @Size(min = 6, message = "Username must be at least 6 characters")
    private String firstName;

    @Size(min = 6, message = "Username must be at least 6 characters")
    private String lastName;

    @Size(min = 6, message = "Username must be at least 6 characters")
    private String email;
}
