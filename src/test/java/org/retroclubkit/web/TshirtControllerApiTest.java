package org.retroclubkit.web;

import org.junit.jupiter.api.Test;
import org.retroclubkit.security.AuthenticationMetadata;
import org.retroclubkit.team.service.TeamService;
import org.retroclubkit.tshirt.model.Category;
import org.retroclubkit.tshirt.model.Size;
import org.retroclubkit.tshirt.model.Tshirt;
import org.retroclubkit.tshirt.service.TshirtService;
import org.retroclubkit.user.model.User;
import org.retroclubkit.user.model.UserRole;
import org.retroclubkit.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TshirtController.class)
public class TshirtControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TshirtService tshirtService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TeamService teamService;

    @Test
    void getRetroTshirts_shouldReturnRetroViewAndModel() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "admin", "123", UserRole.ADMIN, true);
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("admin");
        mockUser.setRole(UserRole.ADMIN);

        List<Tshirt> mockTshirts = List.of();

        when(userService.getById(userId)).thenReturn(mockUser);
        when(tshirtService.getTshirtsByCategoryAndAvailable(Category.RETRO)).thenReturn(mockTshirts);

        mockMvc.perform(get("/tshirts/retro").with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(view().name("retro"))
                .andExpect(model().attributeExists("retroTshirts"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void getNewTshirts_shouldReturnNewViewAndModel() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "admin", "123", UserRole.ADMIN, true);
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("admin");
        mockUser.setRole(UserRole.ADMIN);

        List<Tshirt> mockTshirts = List.of();

        when(userService.getById(userId)).thenReturn(mockUser);
        when(tshirtService.getTshirtsByCategoryAndAvailable(Category.NEW)).thenReturn(mockTshirts);

        mockMvc.perform(get("/tshirts/new").with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(view().name("new"))
                .andExpect(model().attributeExists("newTshirts"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void getNationalTshirts_shouldReturnNationalViewAndModel() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "admin", "123", UserRole.ADMIN, true);
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("admin");
        mockUser.setRole(UserRole.ADMIN);

        List<Tshirt> mockTshirts = List.of();

        when(userService.getById(userId)).thenReturn(mockUser);
        when(tshirtService.getTshirtsByCategoryAndAvailable(Category.NATIONAL)).thenReturn(mockTshirts);

        mockMvc.perform(get("/tshirts/national").with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(view().name("national"))
                .andExpect(model().attributeExists("nationalTshirts"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void getEmptyPage_shouldReturnEmptyTeamPageView() throws Exception {
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setUsername("admin");
        user.setRole(UserRole.ADMIN);

        when(userService.getById(userId)).thenReturn(user);

        mockMvc.perform(get("/tshirts/emptySearch")
                        .with(user(new AuthenticationMetadata(userId, "admin", "123123", UserRole.ADMIN, true))))
                .andExpect(status().isOk())
                .andExpect(view().name("empty-team-page"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void postAddTshirt_shouldRedirectOnSuccess() throws Exception {
        mockMvc.perform(post("/tshirts/add")
                        .with(csrf())
                        .param("name", "Retro Milan")
                        .param("image", "milan.jpg")
                        .param("price", "49.99")
                        .param("sizes", Size.M.name())
                        .param("teamId", UUID.randomUUID().toString())
                        .param("category", Category.RETRO.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tshirts"));
    }

    @Test
    void searchTshirts_shouldRedirectIfNoResults() throws Exception {

        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "admin", "123", UserRole.ADMIN, true);
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("admin");
        mockUser.setRole(UserRole.ADMIN);

        when(userService.getById(any())).thenReturn(mockUser);
        when(tshirtService.findTshirtsByTeam("NonExistentTeam"))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/tshirts/search")
                        .param("team", "NonExistentTeam").with(user(principal)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tshirts/emptySearch"));
    }

    @Test
    void searchTshirts_shouldReturnSearchResultsPage() throws Exception {

        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "admin", "123", UserRole.ADMIN, true);

        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("admin");
        mockUser.setRole(UserRole.ADMIN);

        Tshirt tshirt = new Tshirt();
        tshirt.setId(UUID.randomUUID());
        tshirt.setName("AC Milan 2007");
        tshirt.setPrice(BigDecimal.valueOf(49.99));
        tshirt.setSizes(List.of(Size.M, Size.L));

        List<Tshirt> mockTshirts = List.of(tshirt);

        when(userService.getById(userId)).thenReturn(mockUser);
        when(tshirtService.findTshirtsByTeam("Milan")).thenReturn(mockTshirts);

        mockMvc.perform(get("/tshirts/search")
                        .param("team", "Milan")
                        .with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(view().name("search-results"))
                .andExpect(model().attributeExists("tshirts"))
                .andExpect(model().attributeExists("teamName"))
                .andExpect(model().attributeExists("user"));
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllTshirts_shouldReturnAllTshirtsView() throws Exception {
        UUID userId = UUID.randomUUID();

        AuthenticationMetadata principal = new AuthenticationMetadata(
                userId, "admin", "password", UserRole.ADMIN, true
        );

        User mockedUser = new User();
        mockedUser.setRole(UserRole.ADMIN);
        when(userService.getById(userId)).thenReturn(mockedUser);
        when(tshirtService.getAllTshirtsWhichIsNotDeleted()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/tshirts")
                        .with(SecurityMockMvcRequestPostProcessors.user(principal)))
                .andExpect(status().isOk())
                .andExpect(view().name("all-tshirts"))
                .andExpect(model().attributeExists("tshirts"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAvailableTshirts_shouldReturnAvailableTshirtsView() throws Exception {
        UUID userId = UUID.randomUUID();

        AuthenticationMetadata principal = new AuthenticationMetadata(
                userId, "admin", "password", UserRole.ADMIN, true
        );

        User mockedUser = new User();
        mockedUser.setRole(UserRole.ADMIN);
        when(userService.getById(userId)).thenReturn(mockedUser);
        when(tshirtService.getAllTshirtsWhichIsNotDeleted()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/tshirts/available")
                        .with(SecurityMockMvcRequestPostProcessors.user(principal)))
                .andExpect(status().isOk())
                .andExpect(view().name("all-tshirts"))
                .andExpect(model().attributeExists("tshirts"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getUnavailableTshirts_shouldReturnUnavailableTshirtsView() throws Exception {
        UUID userId = UUID.randomUUID();

        AuthenticationMetadata principal = new AuthenticationMetadata(
                userId, "admin", "password", UserRole.ADMIN, true
        );

        User mockedUser = new User();
        mockedUser.setRole(UserRole.ADMIN);
        when(userService.getById(userId)).thenReturn(mockedUser);
        when(tshirtService.getAllTshirtsWhichIsNotDeleted()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/tshirts/unavailable")
                        .with(SecurityMockMvcRequestPostProcessors.user(principal)))
                .andExpect(status().isOk())
                .andExpect(view().name("all-tshirts"))
                .andExpect(model().attributeExists("tshirts"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateTshirtForm_shouldReturnEditView() throws Exception {
        UUID tshirtId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Tshirt tshirt = new Tshirt();
        tshirt.setId(tshirtId);
        tshirt.setName("Test Shirt");
        tshirt.setSizes(List.of(Size.M, Size.L));

        User user = new User();
        user.setId(userId);
        user.setUsername("admin");
        user.setRole(UserRole.ADMIN);

        when(userService.getById(userId)).thenReturn(user);
        when(tshirtService.getById(tshirtId)).thenReturn(tshirt);

        mockMvc.perform(get("/tshirts/edit/" + tshirtId)
                        .with(user(new AuthenticationMetadata(userId, "admin", "123123", UserRole.ADMIN, true))))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-tshirt"))
                .andExpect(model().attributeExists("tshirt"))
                .andExpect(model().attributeExists("allSizes"))
                .andExpect(model().attributeExists("user"));
    }
}
