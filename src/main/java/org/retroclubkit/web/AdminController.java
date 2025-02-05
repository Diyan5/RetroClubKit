package org.retroclubkit.web;

import jakarta.servlet.http.HttpSession;
import org.retroclubkit.order.model.Order;
import org.retroclubkit.order.service.OrderService;
import org.retroclubkit.team.service.TeamService;
import org.retroclubkit.tshirt.model.Category;
import org.retroclubkit.tshirt.model.Size;
import org.retroclubkit.tshirt.model.Tshirt;
import org.retroclubkit.tshirt.service.TshirtService;
import org.retroclubkit.user.model.User;
import org.retroclubkit.user.service.UserService;
import org.retroclubkit.web.dto.NewTeamRequest;
import org.retroclubkit.web.dto.TshirtAdminRequest;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ModelAndView getAllUsers(HttpSession session) {

        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);

        List<User> users = userService.getAllUsers();
        ModelAndView modelAndView = new ModelAndView("all-users");
        modelAndView.addObject("users", users);
        modelAndView.addObject("user", user);

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
    public ModelAndView getUserOrders(@PathVariable UUID id, HttpSession session) {
        //User for header
        UUID userId = (UUID) session.getAttribute("user_id");
        User userAdmin = userService.getById(userId);

        //user for view his orders
        User user = userService.getById(id);
        List<Order> orders = orderService.getOrdersByUser(user);

        ModelAndView modelAndView = new ModelAndView("user-orders");
        modelAndView.addObject("userAdmin", userAdmin);
        modelAndView.addObject("user", user);
        modelAndView.addObject("orders", orders);

        return modelAndView;
    }

    @GetMapping("/admin/order-details/{id}")
    public ModelAndView getOrderDetails(@PathVariable UUID id, HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);

        Order order = orderService.getById(id);
        ModelAndView modelAndView = new ModelAndView("all-order-details");
        modelAndView.addObject("order", order);
        modelAndView.addObject("user", order.getUser()); // Показваме кой е направил поръчката
        modelAndView.addObject("items", order.getItems()); // Всички артикули в поръчката
        modelAndView.addObject("paymentMethod", order.getPayment().getPaymentMethod());
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    // ✅ Показване на всички тениски
    @GetMapping("/all-tshirts")
    public ModelAndView getAllProducts(HttpSession session) {

        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);

        List<Tshirt> tshirts = tshirtService.getAllTshirts();
        ModelAndView modelAndView = new ModelAndView("all-tshirts");
        modelAndView.addObject("tshirts", tshirts);
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    // ✅ Филтриране само на наличните продукти
    @GetMapping("/admin/products/available")
    public ModelAndView getAvailableProducts(HttpSession session) {

        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);

        ModelAndView modelAndView = new ModelAndView("all-tshirts");
        modelAndView.addObject("tshirts", tshirtService.getAvailableTshirts());
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    // ✅ Филтриране само на неналичните продукти
    @GetMapping("/admin/products/unavailable")
    public ModelAndView getUnavailableProducts(HttpSession session) {

        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);

        ModelAndView modelAndView = new ModelAndView("all-tshirts");
        modelAndView.addObject("tshirts", tshirtService.getUnavailableTshirts());
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    // ✅ Изтриване на продукт
    @PostMapping("/admin/products/delete/{id}")
    public String deleteProduct(@PathVariable UUID id) {
        tshirtService.deleteTshirt(id);
        return "redirect:/all-tshirts";
    }

    @GetMapping("/admin/products/edit/{id}")
    public ModelAndView editTshirtSizes(@PathVariable UUID id, HttpSession session) {

        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);

        Tshirt tshirt = tshirtService.getById(id);
        ModelAndView modelAndView = new ModelAndView("edit-tshirt");
        modelAndView.addObject("tshirt", tshirt);
        modelAndView.addObject("allSizes", Size.values());
        modelAndView.addObject("user", user);

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

    @GetMapping("/admin/add")
    public ModelAndView showAddPage(HttpSession session) {

        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);

        ModelAndView modelAndView = new ModelAndView("add-tshirt-or-team");
        modelAndView.addObject("tshirt", new TshirtAdminRequest());
        modelAndView.addObject("teams", teamService.getAllTeams());
        modelAndView.addObject("categories", Arrays.asList(Category.values()));
        modelAndView.addObject("sizes", Arrays.asList(Size.values()));

        modelAndView.addObject("team", new NewTeamRequest());

        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @PostMapping("/admin/tshirts/save")
    public ModelAndView  saveNewTshirt(@ModelAttribute TshirtAdminRequest tshirtRequest) {
        ModelAndView modelAndView = new ModelAndView("add-tshirt-or-team");

        if (tshirtService.existsByName(tshirtRequest.getName())) {
            modelAndView.addObject("tshirtError", "T-shirt with this name already exists!");
            modelAndView.addObject("tshirt", new TshirtAdminRequest());
            modelAndView.addObject("team", new NewTeamRequest());
            modelAndView.addObject("teams", teamService.getAllTeams());
            modelAndView.addObject("categories", Arrays.asList(Category.values()));
            modelAndView.addObject("sizes", Arrays.asList(Size.values()));
            return modelAndView; // Връща се същата страница с грешката
        }

        modelAndView.setViewName("all-tshirts");
        tshirtService.saveTshirt(tshirtRequest);
        return modelAndView;
    }

    @PostMapping("/admin/team/save")
    public ModelAndView saveNewTeam(@ModelAttribute NewTeamRequest newTeamRequest) {
        ModelAndView modelAndView = new ModelAndView("add-tshirt-or-team");

        if (teamService.existsByName(newTeamRequest.getName())) {
            modelAndView.addObject("teamError", "Team with this name already exists!");
            modelAndView.addObject("tshirt", new TshirtAdminRequest());
            modelAndView.addObject("team", new NewTeamRequest());
            modelAndView.addObject("teams", teamService.getAllTeams());
            modelAndView.addObject("categories", Arrays.asList(Category.values()));
            modelAndView.addObject("sizes", Arrays.asList(Size.values()));
            return modelAndView; // Връща се същата страница с грешката
        }

        modelAndView.setViewName("all-tshirts");
        teamService.saveTeam(newTeamRequest);
        return modelAndView;
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

    @PutMapping("/users/{id}/status") // PUT /users/{id}/status
    public String switchUserStatus(@PathVariable UUID id) {

        userService.switchStatus(id);

        return "redirect:/all-users";
    }

    @PutMapping("/users/{id}/role") // PUT /users/{id}/role
    public String switchUserRole(@PathVariable UUID id) {

        userService.switchRole(id);

        return "redirect:/all-users";
    }

}
