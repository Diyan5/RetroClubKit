package org.retroclubkit.web.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @Size(min = 6, message = "Username must be at least 6 characters")
    private String username;

    @NotNull(message = "Can not be null")
    private String firstName;

    @NotNull(message = "Can not be null")
    private String lastName;

    @Email(message = "Valid email")
    private String email;

    @Size(min = 6, message = "Password must be exactly 6 digits")
    private String password;

    @Size(min = 6, message = "Password must be exactly 6 digits")
    private String confirmPassword;


}
