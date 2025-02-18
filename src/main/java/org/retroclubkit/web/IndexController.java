package org.retroclubkit.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.retroclubkit.security.AuthenticationMetadata;
import org.retroclubkit.tshirt.model.Tshirt;
import org.retroclubkit.tshirt.service.TshirtService;
import org.retroclubkit.user.model.User;
import org.retroclubkit.user.service.UserService;
import org.retroclubkit.web.dto.LoginRequest;
import org.retroclubkit.web.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;


@Controller
public class IndexController {

    private final TshirtService tshirtService;
    private final UserService userService;

    @Autowired
    public IndexController(TshirtService tshirtService, UserService userService) {
        this.tshirtService = tshirtService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String getIndexPage() {

        return "index";
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage() {

        ModelAndView modelAndView = new ModelAndView("login");
        modelAndView.addObject("loginRequest", new LoginRequest());

        return modelAndView;
    }


    @GetMapping("/register")
    public ModelAndView getRegisterPage() {

        ModelAndView modelAndView = new ModelAndView("register");
        modelAndView.addObject("registerRequest", new RegisterRequest());

        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView registerNewUser(@Valid RegisterRequest registerRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("register");
        }

        userService.register(registerRequest);

        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/home")
    public ModelAndView getHomePage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());

        List<Tshirt> tshirtsLimit = tshirtService.getAllTshirtsLimitAndAvailableTrue();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        modelAndView.addObject("user", user);
        modelAndView.addObject("tshirts", tshirtsLimit);


        return modelAndView;
    }

//    @GetMapping("/admin")
//    public ModelAndView getAdminPage(HttpSession session) {
//        UUID userId = (UUID) session.getAttribute("user_id");
//
//        User user = userService.getById(userId);
//        ModelAndView modelAndView = new ModelAndView("admin");
//
//        modelAndView.addObject("user", user);
//        return modelAndView;
//    }
}
