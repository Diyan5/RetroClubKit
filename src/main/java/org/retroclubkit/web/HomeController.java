package org.retroclubkit.web;


import org.retroclubkit.order.model.Order;
import org.retroclubkit.order.service.OrderService;
import org.retroclubkit.payment.model.PaymentMethod;
import org.retroclubkit.payment.service.PaymentService;
import org.retroclubkit.security.AuthenticationMetadata;
import org.retroclubkit.user.model.User;
import org.retroclubkit.user.service.UserService;
import org.retroclubkit.web.dto.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

    private final UserService userService;
    private final OrderService orderService;
    private final PaymentService paymentService;

    @Autowired
    public HomeController(UserService userService, OrderService orderService, PaymentService paymentService) {
        this.userService = userService;
        this.orderService = orderService;
        this.paymentService = paymentService;
    }

    @GetMapping("/checkout")
    public ModelAndView checkout() {
        return new ModelAndView("checkout");
    }

    @PostMapping("/checkout")
    public ModelAndView submitOrder(
            @RequestBody OrderRequest orderRequest,
            @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());
        Order order = orderService.createOrder(user, orderRequest);
        PaymentMethod paymentMethod = PaymentMethod.valueOf(orderRequest.getPaymentMethod());
        paymentService.processPayment(order, paymentMethod);

        return new ModelAndView("redirect:/home");
    }


}
