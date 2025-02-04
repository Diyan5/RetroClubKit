package org.retroclubkit.order.service;

import org.retroclubkit.order.model.Order;
import org.retroclubkit.orderItem.model.OrderItem;
import org.retroclubkit.order.model.Status;
import org.retroclubkit.orderItem.repository.OrderItemRepository;
import org.retroclubkit.order.repository.OrderRepository;
import org.retroclubkit.orderItem.service.OrderItemService;
import org.retroclubkit.tshirt.model.Tshirt;
import org.retroclubkit.tshirt.service.TshirtService;
import org.retroclubkit.user.model.User;
import org.retroclubkit.web.dto.OrderRequest;
import org.retroclubkit.web.dto.TshirtOrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final TshirtService tshirtService;
    private final OrderItemService orderItemService;


    @Autowired
    public OrderService(OrderRepository orderRepository, TshirtService tshirtService, OrderItemService orderItemService) {
        this.orderRepository = orderRepository;
        this.tshirtService = tshirtService;
        this.orderItemService = orderItemService;
    }




    public Order createOrder(User user, OrderRequest orderRequest) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (TshirtOrderRequest itemRequest : orderRequest.getCartItems()) {
            Tshirt tshirt = tshirtService.getById(itemRequest.getId());

            if (tshirt == null) {
                throw new IllegalArgumentException("Tshirt not found");
            }

            int quantity = itemRequest.getQuantity();
            String size = itemRequest.getSize();

            BigDecimal itemPrice = tshirt.getPrice().multiply(BigDecimal.valueOf(quantity));
            totalPrice = totalPrice.add(itemPrice);

            OrderItem orderItem = OrderItem.builder()
                    .tshirt(tshirt)
                    .quantity(quantity)
                    .size(size)
                    .build(); // ❌ НЕ сетваме Order тук (ще го сетнем след Order-а)
            orderItems.add(orderItem);
        }

        totalPrice = totalPrice.setScale(2, RoundingMode.HALF_UP);

        // ✅ Първо запазваме Order, без да има items
        Order order = Order.builder()
                .user(user)
                .fullName(orderRequest.getName())
                .phoneNumber(orderRequest.getPhone())
                .totalPrice(totalPrice)
                .status(Status.PENDING)
                .createdAt(LocalDateTime.now())
                .deliveryAddress(orderRequest.getAddress() + ", " + orderRequest.getCity() + ", " + orderRequest.getCountry())
                .items(new ArrayList<>()) // ❌ Празен списък засега
                .build();

        // ✅ Запазваме Order, за да има ID
        Order savedOrder = orderRepository.save(order);

        // ✅ Сега сетваме order_id на OrderItem обектите
        for (OrderItem item : orderItems) {
            item.setOrder(savedOrder); // ✅ Вече има order_id
        }

        // ✅ Запазваме OrderItem обектите
        orderItemService.saveAll(orderItems);

        // ✅ Обновяваме Order с OrderItem обектите и го запазваме отново
        savedOrder.setItems(orderItems);
        return orderRepository.save(savedOrder);
    }


    public List<Order> getOrdersByUserCreatedAtDesc(UUID userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Order getById(UUID orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }
}
