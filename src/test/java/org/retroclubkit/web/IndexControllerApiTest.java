package org.retroclubkit.web;

import org.retroclubkit.security.AuthenticationMetadata;
import org.retroclubkit.tshirt.service.TshirtService;
import org.retroclubkit.user.model.UserRole;
import org.retroclubkit.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.retroclubkit.TestBuilder.aRandomUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IndexController.class)
public class IndexControllerApiTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TshirtService tshirtService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getRequestToIndexEndpoint_shouldReturnIndexView() throws Exception {

        // 1. Build Request
        MockHttpServletRequestBuilder request = get("/");

        // 2. Send Request
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void getRequestToRegisterEndpoint_shouldReturnRegisterView() throws Exception {

        // 1. Build Request
        MockHttpServletRequestBuilder request = get("/register");

        // 2. Send Request
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("registerRequest"));
    }

    @Test
    void getRequestToLoginEndpoint_shouldReturnLoginView() throws Exception {

        // 1. Build Request
        MockHttpServletRequestBuilder request = get("/login");

        // 2. Send Request
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("loginRequest"));
    }

    @Test
    void getRequestToLoginEndpointWithErrorParameter_shouldReturnLoginViewAndErrorMessageAttribute() throws Exception {

        // 1. Build Request
        MockHttpServletRequestBuilder request = get("/login").param("error", "");

        // 2. Send Request
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("loginRequest", "errorMessage"));
    }

    @Test
    void postRequestToRegisterEndpoint_happyPath() throws Exception {

        // 1. Build Request
        MockHttpServletRequestBuilder request = post("/register")
                .formField("username", "test123")
                .formField("firstName", "first")
                .formField("lastName", "last")
                .formField("email", "test@gmail.com")
                .formField("password", "123123")
                .formField("confirmPassword", "123123")
                .with(csrf());

        // 2. Send Request
        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        verify(userService, times(1)).register(any());
    }

    @Test
    void getAuthenticatedRequestToHome_returnsHomeView() throws Exception {

        // 1. Build Request
        when(userService.getById(any())).thenReturn(aRandomUser());

        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "User123", "123123", UserRole.USER, true);
        MockHttpServletRequestBuilder request = get("/home")
                .with(user(principal));

        // 2. Send Request
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("user"));
        verify(userService, times(1)).getById(userId);
    }

    @Test
    void getUnauthenticatedRequestToHome_redirectToLogin() throws Exception {

        // 1. Build Request
        MockHttpServletRequestBuilder request = get("/home");

        // 2. Send Request
        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection());
        verify(userService, never()).getById(any());
    }
}