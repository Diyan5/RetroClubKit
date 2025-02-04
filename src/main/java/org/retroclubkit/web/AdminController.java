package org.retroclubkit.web;

import org.retroclubkit.order.model.Order;
import org.retroclubkit.order.service.OrderService;
import org.retroclubkit.team.service.TeamService;
import org.retroclubkit.tshirt.model.Category;
import org.retroclubkit.tshirt.model.Size;
import org.retroclubkit.tshirt.model.Tshirt;
import org.retroclubkit.tshirt.service.TshirtService;
import org.retroclubkit.user.model.User;
import org.retroclubkit.user.model.UserRole;
import org.retroclubkit.user.service.UserService;
import org.retroclubkit.web.dto.TshirtAdminRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Controller
public class AdminController {

    private final TshirtService tshirtService;
    private final OrderService orderService;
    private final UserService userService;
    private final TeamService teamService;

    @Autowired
    public AdminController(TshirtService tshirtService, OrderService orderService, UserService userService, TeamService teamService) {
        this.tshirtService = tshirtService;
        this.orderService = orderService;
        this.userService = userService;
        this.teamService = teamService;
    }

    @GetMapping("/all-users")
    public ModelAndView getAllUsers() {

        List<User> users = userService.getAllUsers();
        ModelAndView modelAndView = new ModelAndView("all-users");
        modelAndView.addObject("users", users);
        return modelAndView;
    }

    @PostMapping("/toggle-user-status/{id}")
    public ModelAndView toggleUserStatus(@PathVariable UUID id) {
        User user = userService.getById(id);
        ModelAndView modelAndView = new ModelAndView("all-users");
        modelAndView.addObject("user", user);
        user.setActive(!user.isActive());
        userService.save(user);
        return modelAndView;
    }

    @GetMapping("/user-orders/{id}")
    public ModelAndView getUserOrders(@PathVariable UUID id) {
        User user = userService.getById(id);
        List<Order> orders = orderService.getOrdersByUser(user);

        ModelAndView modelAndView = new ModelAndView("user-orders");
        modelAndView.addObject("user", user);
        modelAndView.addObject("orders", orders);

        return modelAndView;
    }

    @GetMapping("/admin/order-details/{id}")
    public ModelAndView getOrderDetails(@PathVariable UUID id) {
        Order order = orderService.getById(id);
        ModelAndView modelAndView = new ModelAndView("all-order-details");
        modelAndView.addObject("order", order);
        modelAndView.addObject("user", order.getUser()); // Показваме кой е направил поръчката
        modelAndView.addObject("items", order.getItems()); // Всички артикули в поръчката
        modelAndView.addObject("paymentMethod", order.getPayment().getPaymentMethod());
        return modelAndView;
    }

    // ✅ Показване на всички тениски
    @GetMapping("/all-tshirts")
    public ModelAndView getAllProducts() {
        List<Tshirt> tshirts = tshirtService.getAllTshirts();
        ModelAndView modelAndView = new ModelAndView("all-tshirts");
        modelAndView.addObject("tshirts", tshirts);
        return modelAndView;
    }

    // ✅ Филтриране само на наличните продукти
    @GetMapping("/admin/products/available")
    public ModelAndView getAvailableProducts() {
        ModelAndView modelAndView = new ModelAndView("all-tshirts");
        modelAndView.addObject("tshirts", tshirtService.getAvailableTshirts());
        return modelAndView;
    }

    // ✅ Филтриране само на неналичните продукти
    @GetMapping("/admin/products/unavailable")
    public ModelAndView getUnavailableProducts() {
        ModelAndView modelAndView = new ModelAndView("all-tshirts");
        modelAndView.addObject("tshirts", tshirtService.getUnavailableTshirts());
        return modelAndView;
    }

    // ✅ Изтриване на продукт
    @PostMapping("/admin/products/delete/{id}")
    public String deleteProduct(@PathVariable UUID id) {
        tshirtService.deleteTshirt(id);
        return "redirect:/all-tshirts";
    }

    @GetMapping("/admin/products/edit/{id}")
    public ModelAndView editTshirtSizes(@PathVariable UUID id) {
        Tshirt tshirt = tshirtService.getById(id);
        ModelAndView modelAndView = new ModelAndView("edit-tshirt");
        modelAndView.addObject("tshirt", tshirt);
        modelAndView.addObject("allSizes", Size.values());
        return modelAndView;
    }

    @PostMapping("/admin/products/update")
    public String updateTshirt(
            @RequestParam("id") UUID id,
            @RequestParam("sizes") List<Size> sizes,
            @RequestParam("price") BigDecimal price) {

        tshirtService.updateTshirtBySizeAndPrice(id, sizes, price);
        return "redirect:/all-tshirts";
    }

    @GetMapping("/admin/tshirts/add")
    public ModelAndView showAddTshirtPage() {
        ModelAndView modelAndView = new ModelAndView("add-tshirt");
        modelAndView.addObject("tshirt", new TshirtAdminRequest()); // Празен обект за формата
        modelAndView.addObject("teams", teamService.getAllTeams()); // Подаваме всички отбори
        modelAndView.addObject("categories", Arrays.asList(Category.values())); // Подаваме категориите
        modelAndView.addObject("sizes", Arrays.asList(Size.values())); // Подаваме размерите
        return modelAndView;
    }

    @PostMapping("/admin/tshirts/save")
    public String saveNewTshirt(@ModelAttribute TshirtAdminRequest tshirtRequest) {
        tshirtService.saveTshirt(tshirtRequest); // Изпращаме DTO-то директно
        return "redirect:/all-tshirts";
    }

    @PostMapping("/admin/products/toggle-availability/{id}")
    public String toggleProductAvailability(@PathVariable UUID id) {
        tshirtService.toggleAvailability(id);
        return "redirect:/all-tshirts";
    }

//    @PostMapping("/change-user-role/{id}")
//    public String changeUserRole(@PathVariable UUID id) {
//        User user = userService.getById(id);
//
//        if (user.getRole().equals("ADMIN")) {
//            user.setRole(UserRole.USER);
//        } else {
//            user.setRole(UserRole.ADMIN);
//        }
//
//        userService.updateUser(user); // Записваме промяната в базата
//
//        return "redirect:/admin/users"; // Пренасочване към страницата
//    }

//    @GetMapping("/change-user-role/{id}")
//    public String changeUserRole(@PathVariable Long id) {
//        userService.toggleRole(id);
//        return "redirect:/all-users";
//    }

}
