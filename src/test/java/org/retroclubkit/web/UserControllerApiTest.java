package org.retroclubkit.web;

import org.retroclubkit.notification.client.dto.NotificationPreference;
import org.retroclubkit.notification.service.NotificationService;
import org.retroclubkit.security.AuthenticationMetadata;
import org.retroclubkit.user.model.User;
import org.retroclubkit.user.model.UserRole;
import org.retroclubkit.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(UserController.class)
public class UserControllerApiTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private NotificationService notificationService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void putUnauthorizedRequestToSwitchRole_shouldReturn404AndNotFoundView() throws Exception {

        // 1. Build Request
        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "User123", "123123", UserRole.USER, true);
        MockHttpServletRequestBuilder request = put("/users/{id}/role", UUID.randomUUID())
                .with(user(principal))
                .with(csrf());

        // 2. Send Request
        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(view().name("not-found"));
    }

    @Test
    void putAuthorizedRequestToSwitchRole_shouldRedirectToUsers() throws Exception {

        // 1. Build Request
        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "User123", "123123", UserRole.ADMIN, true);
        MockHttpServletRequestBuilder request = put("/users/{id}/role", UUID.randomUUID())
                .with(user(principal))
                .with(csrf());

        // 2. Send Request
        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
        verify(userService, times(1)).switchRole(any());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllUsers_AsAdmin_ShouldReturnAllUsersViewWithModel() throws Exception {
        // Arrange
        UUID adminId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(adminId, "admin", "admin123", UserRole.ADMIN, true);

        User adminUser = new User();
        adminUser.setId(adminId);
        adminUser.setUsername("admin");
        adminUser.setRole(UserRole.ADMIN);

        User user1 = new User();
        user1.setUsername("user1");
        user1.setRole(UserRole.ADMIN);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setRole(UserRole.USER);

        List<User> users = List.of(user1, user2);

        when(userService.getById(adminId)).thenReturn(adminUser);
        when(userService.getAllUsers()).thenReturn(users);

        // Act & Assert
        mockMvc.perform(get("/users").with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(view().name("all-users"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getProfileMenu_ShouldReturnMyAccountViewWithData() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "admin", "admin123", UserRole.ADMIN, true);

        User user = new User();
        user.setId(userId);
        user.setUsername("admin");
        user.setRole(UserRole.ADMIN);

        NotificationPreference preference = new NotificationPreference();
        preference.setEnabled(true);
        preference.setType("EMAIL");
        preference.setContactInfo("test@example.com");

        // Mock-ове
        when(userService.getById(userId)).thenReturn(user);
        when(notificationService.getNotificationPreference(userId)).thenReturn(preference);

        // Act & Assert
        mockMvc.perform(get("/users/{id}/profile", userId).with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(view().name("my-account"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("updateProfileRequest"))
                .andExpect(model().attributeExists("notificationPreference"));
    }

    @Test
    void updateUserProfile_WithValidationErrors_ShouldReturnMyAccountView() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user", "pass", UserRole.USER, true);

        User user = new User();
        user.setId(userId);
        user.setRole(UserRole.USER);

        NotificationPreference preference = new NotificationPreference();
        preference.setEnabled(true);
        preference.setContactInfo("user@example.com");
        preference.setType("EMAIL");

        when(userService.getById(userId)).thenReturn(user);
        when(notificationService.getNotificationPreference(userId)).thenReturn(preference);

        // Act & Assert
        mockMvc.perform(put("/users/{id}/profile", userId)
                        .with(user(principal))
                        .param("email", "")
                        .param("username", "")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("my-account"))
                .andExpect(model().attributeExists("updateProfileRequest"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("notificationPreference"));
    }

    @Test
    void updateUserProfile_WithValidDataAndEmail_ShouldUpdateAndRedirect() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user", "pass", UserRole.USER, true);

        User user = new User();
        user.setId(userId);
        user.setRole(UserRole.USER);

        when(userService.getById(userId)).thenReturn(user);

        NotificationPreference preference = new NotificationPreference();
        preference.setEnabled(true);
        preference.setContactInfo("test@example.com");
        preference.setType("EMAIL");

        when(notificationService.getNotificationPreference(userId)).thenReturn(preference);

        mockMvc.perform(put("/users/{id}/profile", userId)
                        .with(user(principal))
                        .param("username", "newusername")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "test@example.com")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(userService).update(eq(userId), any());
        verify(notificationService).sendNotification(eq(userId), eq("Edit profile"), eq("You successfully edit your profile!"));
        verify(notificationService).saveNotificationPreference(userId, true, "test@example.com");
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void switchUserStatus_AsAdmin_ShouldSwitchAndRedirect() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(put("/users/{id}/status", userId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        // Verify that the service was called
        verify(userService, times(1)).switchStatus(userId);
    }

}
