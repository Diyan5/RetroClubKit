package org.retroclubkit.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.retroclubkit.order.model.Order;
import org.retroclubkit.order.service.OrderService;
import org.retroclubkit.payment.model.PaymentMethod;
import org.retroclubkit.payment.service.PaymentService;
import org.retroclubkit.tshirt.service.TshirtService;
import org.retroclubkit.user.model.User;
import org.retroclubkit.user.service.UserService;
import org.retroclubkit.web.dto.OrderRequest;
import org.retroclubkit.web.dto.UpdateProfileRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class HomeController {

    private final UserService userService;
    private final OrderService orderService;
    private final TshirtService tshirtService;
    private final PaymentService paymentService;


    public HomeController(UserService userService, OrderService orderService, TshirtService tshirtService, PaymentService paymentService) {
        this.userService = userService;
        this.orderService = orderService;
        this.tshirtService = tshirtService;
        this.paymentService = paymentService;
    }


    @GetMapping("/checkout")
    public String checkout() {
        return "checkout";
    }

    @PostMapping("/checkout")
    @ResponseBody
    public ResponseEntity<Map<String, String>> submitOrder(@RequestBody OrderRequest orderRequest, HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);

        Map<String, String> response = new HashMap<>();

        // ✅ Проверка дали потребителят е активен
        if (!user.isActive()) {
            response.put("error", "Your account is inactive. You cannot place orders.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        // ✅ Ако е активен, продължаваме с поръчката
        Order order = orderService.createOrder(user, orderRequest);
        PaymentMethod paymentMethod = PaymentMethod.valueOf(orderRequest.getPaymentMethod());
        paymentService.processPayment(order, paymentMethod);

        response.put("success", "Your order has been placed successfully!");
        return ResponseEntity.ok(response);
    }


    @GetMapping("/my-account")
    public ModelAndView getUserAccount(HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");

        User user = userService.getById(userId);

        // ✅ Създаваме UpdateProfileRequest и попълваме с текущите данни на потребителя
        UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest();
        updateProfileRequest.setUsername(user.getUsername());
        updateProfileRequest.setFirstName(user.getFirstName());
        updateProfileRequest.setLastName(user.getLastName());
        updateProfileRequest.setEmail(user.getEmail());

        ModelAndView modelAndView = new ModelAndView("my-account");
        modelAndView.addObject("user", user);
        modelAndView.addObject("updateProfileRequest", updateProfileRequest); // ✅ Изпращаме попълнен DTO

        return modelAndView;
    }

    @PostMapping("/my-account")
    public ModelAndView updateProfile(
            @Valid @ModelAttribute("updateProfileRequest") UpdateProfileRequest request,
            BindingResult bindingResult,
            HttpSession session) {

        ModelAndView modelAndView = new ModelAndView("my-account");

        UUID userId = (UUID) session.getAttribute("user_id");
        if (userId == null) {
            return new ModelAndView("redirect:/login");
        }

        if (bindingResult.hasErrors()) {
            modelAndView.addObject("updateProfileRequest", request);
            return modelAndView; // Връщаме обратно страницата с грешките
        }

        User user = userService.getById(userId);
        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());

        userService.save(user);

        return new ModelAndView("redirect:/my-account?success");
    }

    @GetMapping("/orders")
    public ModelAndView getOrders(HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");

        List<Order> orders = orderService.getOrdersByUserCreatedAtDesc(userId); // Взимаме поръчките на потребителя

        ModelAndView modelAndView = new ModelAndView("orders");
        modelAndView.addObject("orders", orders); // Добавяме поръчките в модела

        return modelAndView;
    }
    @GetMapping("/order-details/{orderId}")
    public ModelAndView getOrderDetails(@PathVariable UUID orderId, HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");

        Order order = orderService.getById(orderId);

        ModelAndView modelAndView = new ModelAndView("order-details");
        modelAndView.addObject("order", order);
        modelAndView.addObject("orderItems", order.getItems());

        return modelAndView;
    }

}
