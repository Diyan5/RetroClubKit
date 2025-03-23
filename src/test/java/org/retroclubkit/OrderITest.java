//package org.retroclubkit;
//
//
//import org.junit.jupiter.api.Test;
//import org.retroclubkit.order.model.Order;
//import org.retroclubkit.order.model.Status;
//import org.retroclubkit.order.repository.OrderRepository;
//import org.retroclubkit.order.service.OrderService;
//import org.retroclubkit.team.model.Team;
//import org.retroclubkit.team.repository.TeamRepository;
//import org.retroclubkit.tshirt.model.Category;
//import org.retroclubkit.tshirt.model.Size;
//import org.retroclubkit.tshirt.model.Tshirt;
//import org.retroclubkit.tshirt.repository.TshirtRepository;
//import org.retroclubkit.user.model.User;
//import org.retroclubkit.user.repository.UserRepository;
//import org.retroclubkit.user.service.UserService;
//import org.retroclubkit.web.dto.OrderRequest;
//import org.retroclubkit.web.dto.RegisterRequest;
//import org.retroclubkit.web.dto.TshirtOrderRequest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.domain.EntityScan;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.DirtiesContext;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//@ActiveProfiles("test")
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
//@SpringBootTest
//public class OrderITest {
//
//    @Autowired
//    private OrderService orderService;
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private TshirtRepository tshirtRepository;
//
//    @Autowired
//    private TeamRepository teamRepository;
//
//    @Test
//    public void testCreateOrder_successfulPurchase() {
//        RegisterRequest registerRequest = RegisterRequest.builder()
//                .username("test")
//                .firstName("test")
//                .lastName("test")
//                .email("test@gmail.com")
//                .password("123123")
//                .confirmPassword("123123")
//                .build();
//
//        User registeredUser = userService.register(registerRequest);
//
//        Team team = teamRepository.save(Team.builder()
//                .name("Italy")
//                .country("Italy")
//                .build());
//
//        Tshirt tshirt = tshirtRepository.save(Tshirt.builder()
//                .name("Retro Italy")
//                .price(BigDecimal.valueOf(79.99))
//                .image("italy.jpg")
//                .category(Category.NATIONAL)
//                .sizes(List.of(Size.M, Size.L))
//                .isAvailable(true)
//                .team(team)
//                .build());
//
//        TshirtOrderRequest itemRequest = new TshirtOrderRequest();
//        itemRequest.setId(tshirt.getId());
//        itemRequest.setQuantity(2);
//        itemRequest.setSize("L");
//
//        OrderRequest orderRequest = new OrderRequest();
//        orderRequest.setPhone("0888123456");
//        orderRequest.setAddress("Via Roma 10");
//        orderRequest.setCity("Rome");
//        orderRequest.setCountry("Italy");
//        orderRequest.setCartItems(List.of(itemRequest));
//
//        Order order = orderService.createOrder(registeredUser, orderRequest);
//
//        assertNotNull(order.getId());
//        assertEquals(Status.PENDING, order.getStatus());
//        assertEquals(BigDecimal.valueOf(159.98).setScale(2), order.getTotalPrice());
//        assertEquals(1, order.getItems().size());
//        assertEquals("L", order.getItems().get(0).getSize());
//    }
//
//
//}
