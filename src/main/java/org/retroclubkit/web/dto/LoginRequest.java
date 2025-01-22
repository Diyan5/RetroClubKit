package org.retroclubkit.web.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @Size(min = 6, message = "Username must be at least 6 characters")
    private String username;

    @Pattern(regexp = "\\d{6}", message = "Password must be exactly 6 digits")
    private String password;

    public @Size(min = 6, message = "Username must be at least 6 characters") String getUsername() {
        return username;
    }

    public void setUsername(@Size(min = 6, message = "Username must be at least 6 characters") String username) {
        this.username = username;
    }

    public @Pattern(regexp = "\\d{6}", message = "Password must be exactly 6 digits") String getPassword() {
        return password;
    }

    public void setPassword(@Pattern(regexp = "\\d{6}", message = "Password must be exactly 6 digits") String password) {
        this.password = password;
    }
}
