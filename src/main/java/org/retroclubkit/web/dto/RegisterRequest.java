package org.retroclubkit.web.dto;


import jakarta.validation.constraints.Size;
import lombok.Data;



@Data  // data give setters and getter
public class RegisterRequest {

    @Size(min = 6, message = "Username must be at least 6 characters")
    private String username;

    @Size(min = 6, message = "Username must be at least 6 characters")
    private String firstName;

    @Size(min = 6, message = "Username must be at least 6 characters")
    private String lastName;

    @Size(min = 6, message = "Username must be at least 6 characters")
    private String email;

    @Size(min = 6, message = "Password must be exactly 6 digits")
    private String password;

    @Size(min = 6, message = "Password must be exactly 6 digits")
    private String confirmPassword;


}
