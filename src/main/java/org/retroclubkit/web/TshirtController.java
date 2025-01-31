package org.retroclubkit.web;

import org.retroclubkit.tshirt.model.Category;
import org.retroclubkit.tshirt.model.Tshirt;
import org.retroclubkit.tshirt.service.TshirtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
public class TshirtController {

    private final TshirtService tshirtService;

    @Autowired
    public TshirtController(TshirtService tshirtService) {
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

}
