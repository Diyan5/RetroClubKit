package org.retroclubkit.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateProfileRequest {

    @NotBlank(message = "Username name cannot be empty")
    @NotNull(message = "Username cannot be empty")
    private String username;

    @NotBlank(message = "First name cannot be empty")
    @NotNull(message = "First name cannot be empty")
    private String firstName;

    @NotBlank(message = "Last name cannot be empty")
    @NotNull(message = "Last name cannot be empty")
    private String lastName;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Not a valid email format")
    private String email;
}
