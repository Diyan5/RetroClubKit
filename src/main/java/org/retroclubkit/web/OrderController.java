package org.retroclubkit.web;


import org.retroclubkit.order.model.Order;
import org.retroclubkit.order.service.OrderService;
import org.retroclubkit.security.AuthenticationMetadata;
import org.retroclubkit.user.model.User;
import org.retroclubkit.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView getOrders(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());

        List<Order> orders = orderService.getOrdersByUserCreatedAtDesc(user.getId());

        ModelAndView modelAndView = new ModelAndView("orders");
        modelAndView.addObject("user", user);
        modelAndView.addObject("orders", orders);

        return modelAndView;
    }

    @GetMapping("/details/{orderId}") // user see your orders
    public ModelAndView getOrderDetails(@PathVariable UUID orderId, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());
        Order order = orderService.getById(orderId);

        ModelAndView modelAndView = new ModelAndView("order-details");
        modelAndView.addObject("user", user);
        modelAndView.addObject("order", order);
        modelAndView.addObject("orderItems", order.getItems());

        return modelAndView;
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ModelAndView getUserOrders(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());
        List<Order> orders = orderService.getOrdersByUser(user);

        ModelAndView modelAndView = new ModelAndView("user-orders");
        modelAndView.addObject("user", user);
        modelAndView.addObject("orders", orders);

        return modelAndView;
    }

    @GetMapping("/details/user/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')") //admin see user's orders
    public ModelAndView getOrderDetailsAdmin(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getById(authenticationMetadata.getUserId());

        Order order = orderService.getById(id);
        ModelAndView modelAndView = new ModelAndView("all-order-details");
        modelAndView.addObject("order", order);
        modelAndView.addObject("user", order.getUser());
        modelAndView.addObject("items", order.getItems());
        modelAndView.addObject("paymentMethod", order.getPayment().getPaymentMethod());
        modelAndView.addObject("user", user);
        return modelAndView;
    }
}
