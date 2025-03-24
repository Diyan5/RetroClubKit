package org.retroclubkit.web;

import org.junit.jupiter.api.Test;
import org.retroclubkit.orderItem.model.OrderItem;
import org.retroclubkit.payment.model.Payment;
import org.retroclubkit.payment.model.PaymentMethod;
import org.retroclubkit.security.AuthenticationMetadata;
import org.retroclubkit.order.model.Order;
import org.retroclubkit.order.service.OrderService;
import org.retroclubkit.tshirt.model.Tshirt;
import org.retroclubkit.user.model.User;
import org.retroclubkit.user.model.UserRole;
import org.retroclubkit.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private OrderService orderService;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getOrders_ShouldReturnOrdersViewWithUserAndOrders() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "admin", "123", UserRole.ADMIN, true);
        User user = new User();
        user.setId(userId);
        user.setUsername("admin");
        user.setRole(UserRole.ADMIN);

        Order order1 = new Order();
        Order order2 = new Order();

        List<Order> orders = List.of(order1, order2);

        when(userService.getById(userId)).thenReturn(user);
        when(orderService.getOrdersByUserCreatedAtDesc(userId)).thenReturn(orders);

        // Act & Assert
        mockMvc.perform(get("/orders").with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(view().name("orders"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("orders"));
    }

    @Test
    void getUserOrders_ShouldReturnUserOrdersViewWithOrders() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "admin", "123", UserRole.ADMIN, true);

        User user = new User();
        user.setId(userId);
        user.setUsername("admin");
        user.setRole(UserRole.ADMIN);

        Order order1 = new Order();
        Order order2 = new Order();
        List<Order> orders = List.of(order1, order2);

        // Mock services
        when(userService.getById(userId)).thenReturn(user);
        when(orderService.getOrdersByUser(user)).thenReturn(orders);

        // When & Then
        mockMvc.perform(get("/orders/user/{id}", userId)
                        .with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(view().name("user-orders"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("orders"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getOrderDetailsAdmin_ShouldReturnAllOrderDetailsView() throws Exception {
        // Arrange
        UUID adminId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        AuthenticationMetadata principal = new AuthenticationMetadata(adminId, "admin", "123", UserRole.ADMIN, true);

        // Логнатият админ
        User admin = new User();
        admin.setId(adminId);
        admin.setUsername("admin");
        admin.setRole(UserRole.ADMIN);

        // Потребителят, който е направил поръчката
        User userWhoOrdered = new User();
        userWhoOrdered.setId(UUID.randomUUID());
        userWhoOrdered.setUsername("john");

        // Примерна тениска
        Tshirt tshirt = new Tshirt();
        tshirt.setName("Classic Retro Tee");
        tshirt.setPrice(BigDecimal.valueOf(29.99));


        // Примерен артикул
        OrderItem item = new OrderItem();
        item.setTshirt(tshirt);
        item.setQuantity(2);

        // Примерно плащане
        Payment payment = new Payment();
        payment.setPaymentMethod(PaymentMethod.CREDIT_CARD);

        // Самата поръчка
        Order order = new Order();
        order.setId(orderId);
        order.setUser(userWhoOrdered);
        order.setItems(List.of(item));
        order.setPayment(payment);

        // Mock services
        when(userService.getById(adminId)).thenReturn(admin);
        when(orderService.getById(orderId)).thenReturn(order);

        // Act & Assert
        mockMvc.perform(get("/orders/details/user/{id}", orderId)
                        .with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(view().name("all-order-details"))
                .andExpect(model().attributeExists("order"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("paymentMethod"))
                .andExpect(model().attribute("user", admin)); // логнатият админ
    }

}

