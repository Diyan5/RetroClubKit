package org.retroclubkit.web;

import ch.qos.logback.core.model.Model;
import org.retroclubkit.tshirt.model.Tshirt;
import org.retroclubkit.tshirt.repository.TshirtRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class TeamController {

    private final TshirtRepository tshirtRepository;

    public TeamController(TshirtRepository tshirtRepository) {
        this.tshirtRepository = tshirtRepository;
    }

    @GetMapping("/search/{teamName}")
    public ModelAndView getTeamProducts(@PathVariable("teamName") String teamName) {
        // Създаваме нов ModelAndView с шаблона "search-results"
        ModelAndView modelAndView = new ModelAndView("search-results");

        // Извличане на тениските на отбора
        List<Tshirt> products = tshirtRepository.getProductsByNameIgnoreCase(teamName);

        // Винаги добавяме teamName към модела
        modelAndView.addObject("teamName", teamName);

        // Проверка за налични продукти
        if (products.isEmpty()) {
            modelAndView.addObject("error", "Няма налични тениски за отбора: " + teamName);
        } else {
            modelAndView.addObject("products", products);
        }

        return modelAndView;
    }

}
