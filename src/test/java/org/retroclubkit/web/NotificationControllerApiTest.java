package org.retroclubkit.web;

import org.junit.jupiter.api.Test;
import org.retroclubkit.notification.service.NotificationService;
import org.retroclubkit.security.AuthenticationMetadata;
import org.retroclubkit.user.model.User;
import org.retroclubkit.user.model.UserRole;
import org.retroclubkit.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(NotificationController.class)
public class NotificationControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private UserService userService;

    @Test
    void getContactPage_ShouldReturnContactViewWithUserAndEmptyContactRequest() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user", "pass", UserRole.USER, true);

        User user = new User();
        user.setId(userId);
        user.setUsername("user");
        user.setRole(UserRole.USER);

        when(userService.getById(userId)).thenReturn(user);

        // Act & Assert
        mockMvc.perform(get("/notifications/contact").with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(view().name("contact"))
                .andExpect(model().attributeExists("contactRequest"))
                .andExpect(model().attribute("user", user));
    }

    @Test
    void submitContactMessage_ShouldSendContactAndRedirectToHome() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user", "pass", UserRole.USER, true);

        // Act & Assert
        mockMvc.perform(post("/notifications/contact")
                        .with(user(principal))
                        .param("subject", "Test Subject")
                        .param("message", "This is a test message.")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        // Verify
        verify(notificationService).sendContactMessage(argThat(contactRequest ->
                contactRequest.getUserId().equals(userId) &&
                        contactRequest.getSubject().equals("Test Subject") &&
                        contactRequest.getMessage().equals("This is a test message.")
        ));
    }

    @Test
    void updateUserPreference_ShouldUpdateAndRedirect() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user", "pass", UserRole.USER, true);

        // Act & Assert
        mockMvc.perform(put("/notifications/user-preference")
                        .param("enabled", "true")
                        .with(user(principal))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/" + userId + "/profile"));

        // Verify that the service method was called with correct values
        verify(notificationService).updateNotificationPreference(userId, true);
    }

}
