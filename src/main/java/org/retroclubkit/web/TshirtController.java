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
    public ModelAndView getRetroTshirts() {
        // Извличаме всички ретро тениски от сървиса
        List<Tshirt> retroTshirts = tshirtService.getTshirtsByCategoryAndAvailable(Category.RETRO);

        // Създаваме обект ModelAndView
        ModelAndView modelAndView = new ModelAndView("retro"); // Указваме изгледа
        modelAndView.addObject("retroTshirts", retroTshirts); // Добавяме данните в модела

        return modelAndView; // Връщаме ModelAndView
    }

    @GetMapping("/national")
    public ModelAndView getNationalTshirts() {
        // Извличаме всички ретро тениски от сървиса
        List<Tshirt> nationalTshirts = tshirtService.getTshirtsByCategoryAndAvailable(Category.NATIONAL);

        // Създаваме обект ModelAndView
        ModelAndView modelAndView = new ModelAndView("national"); // Указваме изгледа
        modelAndView.addObject("nationalTshirts", nationalTshirts); // Добавяме данните в модела

        return modelAndView; // Връщаме ModelAndView
    }

    @GetMapping("/new")
    public ModelAndView getNewTshirts() {
        // Извличаме всички ретро тениски от сървиса
        List<Tshirt> newTshirts = tshirtService.getTshirtsByCategoryAndAvailable(Category.NEW);

        // Създаваме обект ModelAndView
        ModelAndView modelAndView = new ModelAndView("new"); // Указваме изгледа
        modelAndView.addObject("newTshirts", newTshirts); // Добавяме данните в модела

        return modelAndView; // Връщаме ModelAndView
    }

    @GetMapping("/search")
    public ModelAndView searchTshirts(@RequestParam("team") String teamName,HttpSession session) {
        List<Tshirt> tshirts = tshirtService.findTshirtsByTeam(teamName);

        UUID userId = (UUID) session.getAttribute("user_id");

        User user = userService.getById(userId);

        ModelAndView modelAndView = new ModelAndView("search-results");
        modelAndView.addObject("teamName", teamName);
        modelAndView.addObject("tshirts", tshirts);
        modelAndView.addObject("user", user);
        return modelAndView; // Returns the search results page
    }

}
