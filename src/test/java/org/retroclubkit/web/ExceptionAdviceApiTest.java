package org.retroclubkit.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({ExceptionAdvice.class, FakeTestController.class})
public class ExceptionAdviceApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void handleUsernameAlreadyExist_shouldRedirectWithFlashMessage() throws Exception {
        mockMvc.perform(post("/test/username-exists")
                        .with(csrf())
                        .with(user("testUser").roles("USER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register"))
                .andExpect(flash().attribute("usernameAlreadyExistMessage", "Username is already taken"));
    }

    @Test
    void handleEmailAlreadyExist_shouldRedirectWithFlashMessage() throws Exception {
        mockMvc.perform(post("/test/email-exists")
                        .with(csrf())
                        .with(user("testUser").roles("USER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register"))
                .andExpect(flash().attribute("emailAlreadyExistMessage", "Email is already used"));
    }

    @Test
    void handlePasswordsNotMatch_shouldRedirectWithFlashMessage() throws Exception {
        mockMvc.perform(post("/test/passwords-not-match")
                        .with(csrf())
                        .with(user("testUser").roles("USER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register"))
                .andExpect(flash().attribute("passwordsNotMatch", "Passwords do not match"));
    }

    @Test
    void handleTeamAlreadyExist_shouldRedirectWithFlashMessage() throws Exception {
        mockMvc.perform(post("/test/team-exists")
                        .with(csrf())
                        .with(user("testUser").roles("USER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teams/add"))
                .andExpect(flash().attribute("teamAlreadyExistMessage", "Team already exists"));
    }

    @Test
    void handleTshirtAlreadyExist_shouldRedirectWithFlashMessage() throws Exception {
        mockMvc.perform(post("/test/tshirt-exists")
                        .with(csrf())
                        .with(user("testUser").roles("USER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tshirts/add"))
                .andExpect(flash().attribute("tshirtAlreadyExistMessage", "T-shirt already exists"));
    }
}
