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

    public @Size(min = 6, message = "Username must be at least 6 characters") String getUsername() {
        return username;
    }

    public void setUsername(@Size(min = 6, message = "Username must be at least 6 characters") String username) {
        this.username = username;
    }

    public @Size(min = 6, message = "Username must be at least 6 characters") String getFirstName() {
        return firstName;
    }

    public void setFirstName(@Size(min = 6, message = "Username must be at least 6 characters") String firstName) {
        this.firstName = firstName;
    }

    public @Size(min = 6, message = "Username must be at least 6 characters") String getLastName() {
        return lastName;
    }

    public void setLastName(@Size(min = 6, message = "Username must be at least 6 characters") String lastName) {
        this.lastName = lastName;
    }

    public @Size(min = 6, message = "Username must be at least 6 characters") String getEmail() {
        return email;
    }

    public void setEmail(@Size(min = 6, message = "Username must be at least 6 characters") String email) {
        this.email = email;
    }

    public @Size(min = 6, message = "Password must be exactly 6 digits") String getPassword() {
        return password;
    }

    public void setPassword(@Size(min = 6, message = "Password must be exactly 6 digits") String password) {
        this.password = password;
    }

    public @Size(min = 6, message = "Password must be exactly 6 digits") String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(@Size(min = 6, message = "Password must be exactly 6 digits") String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
