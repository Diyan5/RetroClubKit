package org.retroclubkit.web;

import org.junit.jupiter.api.Test;
import org.retroclubkit.security.AuthenticationMetadata;
import org.retroclubkit.team.model.Team;
import org.retroclubkit.team.service.TeamService;
import org.retroclubkit.user.model.User;
import org.retroclubkit.user.model.UserRole;
import org.retroclubkit.user.service.UserService;
import org.retroclubkit.web.dto.CreatedNewTeam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TeamController.class)
public class TeamControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TeamService teamService;

    @Test
    void saveNewTeam_ShouldReturnAddTeamViewWithEmptyFormAndUser() throws Exception {
        // Arrange
        UUID adminId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(adminId, "admin", "adminpass", UserRole.ADMIN, true);

        User admin = new User();
        admin.setId(adminId);
        admin.setUsername("admin");
        admin.setRole(UserRole.ADMIN);

        when(userService.getById(adminId)).thenReturn(admin);

        // Act & Assert
        mockMvc.perform(get("/teams/add").with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(view().name("add-team"))
                .andExpect(model().attributeExists("team"))
                .andExpect(model().attribute("user", admin));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void saveNewTeam_WithValidData_ShouldSaveAndRedirect() throws Exception {
        mockMvc.perform(post("/teams/add")
                        .param("name", "AC Milan")
                        .param("country", "Italy")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teams"));

        verify(teamService).saveTeam(any(CreatedNewTeam.class));
    }

    @Test
    void showTeams_ShouldReturnAllTeamsViewWithTeamsAndUser() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "admin", "adminpass", UserRole.ADMIN, true);

        User admin = new User();
        admin.setId(userId);
        admin.setUsername("admin");
        admin.setRole(UserRole.ADMIN);

        Team team1 = new Team();
        Team team2 = new Team();

        when(userService.getById(userId)).thenReturn(admin);
        List<Team> teams = List.of(team1, team2);

        when(teamService.getAllTeamsWhichIsNotDeleted()).thenReturn(teams);

        // Act & Assert
        mockMvc.perform(get("/teams").with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(view().name("all-teams"))
                .andExpect(model().attributeExists("teams"))
                .andExpect(model().attribute("user", admin));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteTshirt_ShouldDeleteTeamAndRedirect() throws Exception {
        // Arrange
        UUID teamId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(delete("/teams/delete/{id}", teamId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teams"));

        // Verify
        verify(teamService).deleteTeamById(teamId);
    }

}
