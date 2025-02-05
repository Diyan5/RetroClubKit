package org.retroclubkit.web;

import jakarta.servlet.http.HttpSession;
import org.retroclubkit.tshirt.model.Category;
import org.retroclubkit.tshirt.model.Tshirt;
import org.retroclubkit.tshirt.service.TshirtService;
import org.retroclubkit.user.model.User;
import org.retroclubkit.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@RestController
public class TshirtController {

    private final UserService userService;
    private final TshirtService tshirtService;

    @Autowired
    public TshirtController(UserService userService, TshirtService tshirtService) {
        this.userService = userService;
        this.tshirtService = tshirtService;
    }

    @GetMapping("/retro")
    public ModelAndView getRetroTshirts(HttpSession session) {

        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);

        List<Tshirt> retroTshirts = tshirtService.getTshirtsByCategoryAndAvailable(Category.RETRO);

        ModelAndView modelAndView = new ModelAndView("retro");
        modelAndView.addObject("retroTshirts", retroTshirts);
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @GetMapping("/national")
    public ModelAndView getNationalTshirts(HttpSession session) {

        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);

        List<Tshirt> nationalTshirts = tshirtService.getTshirtsByCategoryAndAvailable(Category.NATIONAL);

        ModelAndView modelAndView = new ModelAndView("national");
        modelAndView.addObject("nationalTshirts", nationalTshirts);
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @GetMapping("/new")
    public ModelAndView getNewTshirts(HttpSession session) {

        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);

        List<Tshirt> newTshirts = tshirtService.getTshirtsByCategoryAndAvailable(Category.NEW);

        ModelAndView modelAndView = new ModelAndView("new");
        modelAndView.addObject("newTshirts", newTshirts);
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @GetMapping("/search")
    public ModelAndView searchTshirts(@RequestParam("team") String teamName,HttpSession session) {

        UUID userId = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userId);

        List<Tshirt> tshirts = tshirtService.findTshirtsByTeam(teamName);

        ModelAndView modelAndView = new ModelAndView("search-results");
        modelAndView.addObject("teamName", teamName);
        modelAndView.addObject("tshirts", tshirts);
        modelAndView.addObject("user", user);

        return modelAndView;
    }

}
