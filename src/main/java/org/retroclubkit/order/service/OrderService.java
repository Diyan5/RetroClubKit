package org.retroclubkit.order.service;

import org.retroclubkit.order.model.Order;
import org.retroclubkit.order.model.OrderItem;
import org.retroclubkit.order.model.Status;
import org.retroclubkit.order.repository.OrderItemRepository;
import org.retroclubkit.order.repository.OrderRepository;
import org.retroclubkit.payment.service.PaymentService;
import org.retroclubkit.tshirt.model.Tshirt;
import org.retroclubkit.tshirt.repository.TshirtRepository;
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
    private final OrderItemRepository orderItemRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, TshirtService tshirtService, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.tshirtService = tshirtService;
        this.orderItemRepository = orderItemRepository;
    }


    //TODO orderItem link to Order
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

            // ✅ Преобразуваме double -> BigDecimal и умножаваме
            BigDecimal itemPrice = BigDecimal.valueOf(tshirt.getPrice()).multiply(BigDecimal.valueOf(quantity));

            // ✅ Добавяме цената към totalPrice
            totalPrice = totalPrice.add(itemPrice);

            OrderItem orderItem = OrderItem.builder()
                    .tshirt(tshirt)
                    .quantity(quantity)
                    .size(String.valueOf(size))
                    .order(null) // Свързваме по-късно
                    .build();
            orderItems.add(orderItem);
        }

        // ✅ Закръгляме до 2 знака
        totalPrice = totalPrice.setScale(2, RoundingMode.HALF_UP);

        Order order = Order.builder()
                .user(user)
                .fullName(orderRequest.getName())
                .phoneNumber(orderRequest.getPhone())
                .totalPrice(totalPrice)
                .status(Status.PENDING)
                .createdAt(LocalDateTime.now())
                .deliveryAddress(orderRequest.getAddress() + ", " + orderRequest.getCity() + ", " + orderRequest.getCountry())
                .items(orderItems)
                .build();
        // ✅ Запазваме Order в базата първо
        Order savedOrder = orderRepository.save(order);

        // ✅ След това сетваме OrderItem обектите към този Order
        for (OrderItem item : orderItems) {
            item.setOrder(savedOrder);
        }

        // ✅ Запазваме OrderItem-ите
        orderItemRepository.saveAll(orderItems);

        // ✅ Сетваме вече свързаните items към Order и го обновяваме
        savedOrder.setItems(orderItems);
        return orderRepository.save(savedOrder); // Обновяваме Order с items
    }

    public List<Order> getOrdersByUserCreatedAtDesc(UUID userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Order getById(UUID orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }
}
