package org.retroclubkit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.retroclubkit.notification.service.NotificationService;
import org.retroclubkit.security.AuthenticationMetadata;
import org.retroclubkit.team.model.Team;
import org.retroclubkit.team.repository.TeamRepository;
import org.retroclubkit.tshirt.model.Category;
import org.retroclubkit.tshirt.model.Size;
import org.retroclubkit.tshirt.model.Tshirt;
import org.retroclubkit.tshirt.repository.TshirtRepository;
import org.retroclubkit.user.model.User;
import org.retroclubkit.user.service.UserService;
import org.retroclubkit.web.dto.OrderRequest;
import org.retroclubkit.web.dto.RegisterRequest;
import org.retroclubkit.web.dto.TshirtOrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class OrderCheckoutApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TshirtRepository tshirtRepository;

    @MockitoBean
    private NotificationService notificationService;

    private User testUser;

    @BeforeEach
    void setup() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("testuser")
                .firstName("Test")
                .lastName("User")
                .email("testuser@example.com")
                .password("123123")
                .confirmPassword("123123")
                .build();

        testUser = userService.register(registerRequest);
    }

    @Test
    void testCheckout_withTwoItems_success() throws Exception {
        Team team = teamRepository.save(Team.builder().name("Germany").country("Germany").build());

        Tshirt tshirt1 = tshirtRepository.save(Tshirt.builder()
                .name("Retro Germany")
                .price(BigDecimal.valueOf(49.99))
                .image("germany.jpg")
                .category(Category.NATIONAL)
                .sizes(List.of(Size.M, Size.L))
                .isAvailable(true)
                .team(team)
                .build());

        Tshirt tshirt2 = tshirtRepository.save(Tshirt.builder()
                .name("Retro Germany 90s")
                .price(BigDecimal.valueOf(59.99))
                .image("germany90s.jpg")
                .category(Category.NATIONAL)
                .sizes(List.of(Size.S, Size.M))
                .isAvailable(true)
                .team(team)
                .build());

        TshirtOrderRequest item1 = new TshirtOrderRequest();
        item1.setId(tshirt1.getId());
        item1.setQuantity(1);
        item1.setSize("M");

        TshirtOrderRequest item2 = new TshirtOrderRequest();
        item2.setId(tshirt2.getId());
        item2.setQuantity(2);
        item2.setSize("S");

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setPhone("0888123456");
        orderRequest.setAddress("Test Str 123");
        orderRequest.setCity("Berlin");
        orderRequest.setCountry("Germany");
        orderRequest.setPaymentMethod("CASH_ON_DELIVERY");
        orderRequest.setCartItems(List.of(item1, item2));

        AuthenticationMetadata principal = new AuthenticationMetadata(
                testUser.getId(),
                testUser.getUsername(),
                testUser.getPassword(),
                testUser.getRole(),
                testUser.isActive()
        );

        MockHttpServletRequestBuilder request = post("/checkout")
                .with(user(principal))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("Your order has been submitted"))
                .andExpect(jsonPath("$.redirect").value("/home"));
    }
}