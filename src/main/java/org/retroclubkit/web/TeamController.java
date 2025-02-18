package org.retroclubkit.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.retroclubkit.security.AuthenticationMetadata;
import org.retroclubkit.team.service.TeamService;
import org.retroclubkit.tshirt.model.Category;
import org.retroclubkit.tshirt.model.Size;
import org.retroclubkit.user.model.User;
import org.retroclubkit.user.service.UserService;
import org.retroclubkit.web.dto.CreatedNewTeam;
import org.retroclubkit.web.dto.CreatedNewTshirt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.UUID;


@Controller
@RequestMapping
public class TeamController {

    private final TeamService teamService;
    private final UserService userService;

    @Autowired
    public TeamController(TeamService teamService, UserService userService) {
        this.teamService = teamService;
        this.userService = userService;
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ModelAndView showAddPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata, @ModelAttribute("message") String message) {

        User user = userService.getById(authenticationMetadata.getUserId());

        ModelAndView modelAndView = new ModelAndView("add-tshirt-or-team");
//        if (message != null && !message.isEmpty()) {
//            modelAndView.addObject("message", message);
//        }
        modelAndView.addObject("team", new CreatedNewTeam());

        modelAndView.addObject("tshirt", new CreatedNewTshirt());
        modelAndView.addObject("teams", teamService.getAllTeams());
        modelAndView.addObject("categories", Arrays.asList(Category.values()));
        modelAndView.addObject("sizes", Arrays.asList(Size.values()));



        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @PostMapping("/teams/add")
    public ModelAndView saveNewTeam(@Valid @ModelAttribute CreatedNewTeam createdNewTeam,
                                    BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("redirect:/add");
        }
        teamService.saveTeam(createdNewTeam);
        return new ModelAndView("redirect:/tshirts");
    }

//    @GetMapping("/search/{teamName}")
//    public ModelAndView getTeamProducts(@PathVariable("teamName") String teamName) {
//        // Създаваме нов ModelAndView с шаблона "search-results"
//        ModelAndView modelAndView = new ModelAndView("search-results");
//
//        // Извличане на тениските на отбора
//        List<Tshirt> products = tshirtRepository.getProductsByNameIgnoreCase(teamName);
//
//        // Винаги добавяме teamName към модела
//        modelAndView.addObject("teamName", teamName);
//
//        // Проверка за налични продукти
//        if (products.isEmpty()) {
//            modelAndView.addObject("error", "Няма налични тениски за отбора: " + teamName);
//        } else {
//            modelAndView.addObject("products", products);
//        }
//
//        return modelAndView;
//    }

}
