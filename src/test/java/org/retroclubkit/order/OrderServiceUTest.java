//package org.retroclubkit.order;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.retroclubkit.notification.service.NotificationService;
//import org.retroclubkit.order.model.Order;
//import org.retroclubkit.order.model.Status;
//import org.retroclubkit.order.repository.OrderRepository;
//import org.retroclubkit.order.service.OrderService;
//import org.retroclubkit.orderItem.service.OrderItemService;
//import org.retroclubkit.tshirt.model.Tshirt;
//import org.retroclubkit.tshirt.service.TshirtService;
//import org.retroclubkit.user.model.User;
//import org.retroclubkit.web.dto.OrderRequest;
//import org.retroclubkit.web.dto.TshirtOrderRequest;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//@ExtendWith(MockitoExtension.class)
//public class OrderServiceUTest {
//
//    @Mock
//    private OrderRepository orderRepository;
//    @Mock
//    private TshirtService tshirtService;
//    @Mock
//    private OrderItemService orderItemService;
//    @Mock
//    private NotificationService notificationService;
//
//    @InjectMocks
//    private OrderService orderService;
//
//    private User user;
//    private OrderRequest orderRequest;
//    private Tshirt tshirt;
//
//    @BeforeEach
//    void setUp() {
//        user = new User();
//        user.setId(UUID.randomUUID());
//        user.setUsername("testUser");
//
//        tshirt = new Tshirt();
//        tshirt.setId(UUID.randomUUID());
//        tshirt.setPrice(BigDecimal.valueOf(20.00));
//
//        TshirtOrderRequest tshirtOrderRequest = new TshirtOrderRequest();
//        tshirtOrderRequest.setId(tshirt.getId());
//        tshirtOrderRequest.setQuantity(2);
//        tshirtOrderRequest.setSize("L");
//
//        orderRequest = new OrderRequest();
//        orderRequest.setPhone("123456789");
//        orderRequest.setAddress("Main Street 10");
//        orderRequest.setCity("Sofia");
//        orderRequest.setCountry("Bulgaria");
//        orderRequest.setCartItems(List.of(tshirtOrderRequest));
//    }
//
//    @Test
//    void getOrdersByUserCreatedAtDesc_ShouldReturnOrders() {
//        // Given
//        UUID userId = UUID.randomUUID();
//        List<Order> orders = List.of(
//                new Order(UUID.randomUUID(), user, "123456789", BigDecimal.valueOf(50), Status.PENDING, LocalDateTime.now(), new ArrayList<>(), "Address", null),
//                new Order(UUID.randomUUID(), user, "987654321", BigDecimal.valueOf(30), Status.PENDING, LocalDateTime.now().minusDays(1), new ArrayList<>(), "Address", null)
//        );
//        when(orderRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(orders);
//
//        // When
//        List<Order> result = orderService.getOrdersByUserCreatedAtDesc(userId);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(2, result.size());
//        verify(orderRepository, times(1)).findByUserIdOrderByCreatedAtDesc(userId);
//    }
//
//    @Test
//    void getById_ShouldReturnOrder_WhenOrderExists() {
//        // Given
//        UUID orderId = UUID.randomUUID();
//        Order order = new Order(orderId, user, "123456789", BigDecimal.valueOf(50), Status.PENDING, LocalDateTime.now(), new ArrayList<>(), "Address", null);
//        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
//
//        // When
//        Order result = orderService.getById(orderId);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(orderId, result.getId());
//        verify(orderRepository, times(1)).findById(orderId);
//    }
//
//    @Test
//    void getById_ShouldReturnNull_WhenOrderDoesNotExist() {
//        // Given
//        UUID orderId = UUID.randomUUID();
//        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
//
//        // When
//        Order result = orderService.getById(orderId);
//
//        // Then
//        assertNull(result);
//        verify(orderRepository, times(1)).findById(orderId);
//    }
//
//    @Test
//    void getOrdersByUser_ShouldReturnOrders() {
//        // Given
//        List<Order> orders = List.of(
//                new Order(UUID.randomUUID(), user, "123456789", BigDecimal.valueOf(50), Status.PENDING, LocalDateTime.now(), new ArrayList<>(), "Address", null),
//                new Order(UUID.randomUUID(), user, "987654321", BigDecimal.valueOf(30), Status.PENDING, LocalDateTime.now().minusDays(1), new ArrayList<>(), "Address", null)
//        );
//        when(orderRepository.findByUser(user)).thenReturn(orders);
//
//        // When
//        List<Order> result = orderService.getOrdersByUser(user);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(2, result.size());
//        verify(orderRepository, times(1)).findByUser(user);
//    }
//
//
//    @Test
//    void createOrder_ShouldCreateAndSaveOrder() {
//        // Given
//        when(tshirtService.getById(tshirt.getId())).thenReturn(tshirt);
//        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        // When
//        Order createdOrder = orderService.createOrder(user, orderRequest);
//
//        // Then
//        assertNotNull(createdOrder);
//        assertEquals(user, createdOrder.getUser());
//        assertEquals(new BigDecimal("40.00"), createdOrder.getTotalPrice());
//        assertEquals(Status.PENDING, createdOrder.getStatus());
//        assertEquals("Main Street 10, Sofia, Bulgaria", createdOrder.getDeliveryAddress());
//        verify(orderRepository, times(2)).save(any(Order.class));
//        verify(orderItemService, times(1)).saveAll(anyList());
//        verify(notificationService, times(1)).sendNotification(eq(user.getId()), eq("New Purchase"), anyString());
//    }
//
//    @Test
//    void createOrder_ShouldThrowException_WhenTshirtNotFound() {
//        // Given
//        when(tshirtService.getById(any(UUID.class))).thenReturn(null);
//
//        // Then
//        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(user, orderRequest));
//    }
//}
