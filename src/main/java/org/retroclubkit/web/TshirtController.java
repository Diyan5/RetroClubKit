package org.retroclubkit.web;

import jakarta.validation.Valid;
import org.retroclubkit.security.AuthenticationMetadata;
import org.retroclubkit.team.service.TeamService;
import org.retroclubkit.tshirt.model.Category;
import org.retroclubkit.tshirt.model.Size;
import org.retroclubkit.tshirt.model.Tshirt;
import org.retroclubkit.tshirt.service.TshirtService;
import org.retroclubkit.user.model.User;
import org.retroclubkit.user.service.UserService;
import org.retroclubkit.web.dto.CreatedNewTshirt;

import org.retroclubkit.web.dto.UpdateTshirtRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tshirts")
public class TshirtController {

    private final UserService userService;
    private final TshirtService tshirtService;
    private final TeamService teamService;

    @Autowired
    public TshirtController(UserService userService, TshirtService tshirtService, TeamService teamService) {
        this.userService = userService;
        this.tshirtService = tshirtService;
        this.teamService = teamService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ModelAndView getAllTshirts(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());

        List<Tshirt> tshirts = tshirtService.getAllTshirtsWhichIsNotDeleted();
        ModelAndView modelAndView = new ModelAndView("all-tshirts");
        modelAndView.addObject("tshirts", tshirts);
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ModelAndView getAvailableTshirts(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());

        ModelAndView modelAndView = new ModelAndView("all-tshirts");
        modelAndView.addObject("tshirts", tshirtService.getAvailableTshirts());
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @GetMapping("/unavailable")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ModelAndView getUnavailableTshirts(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());

        ModelAndView modelAndView = new ModelAndView("all-tshirts");
        modelAndView.addObject("tshirts", tshirtService.getUnavailableTshirts());
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @DeleteMapping("/delete/{id}")
    public ModelAndView deleteTshirt(@PathVariable UUID id) {
        tshirtService.deleteTshirtById(id);
        return new ModelAndView("redirect:/tshirts");
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ModelAndView updateTshirt(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());

        Tshirt tshirt = tshirtService.getById(id);
        ModelAndView modelAndView = new ModelAndView("edit-tshirt");
        modelAndView.addObject("updateTshirtRequest", tshirt);
        modelAndView.addObject("allSizes", Size.values());
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @PutMapping("/update")
    public ModelAndView updateTshirt(
            @Valid @ModelAttribute("updateTshirtRequest") UpdateTshirtRequest updateTshirtRequest,
            BindingResult bindingResult,
            @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        ModelAndView modelAndView = new ModelAndView("edit-tshirt");
        User user = userService.getById(authenticationMetadata.getUserId());

        if (bindingResult.hasErrors()) {
            modelAndView.addObject("user", user);
            modelAndView.addObject("updateTshirtRequest", updateTshirtRequest);
            modelAndView.addObject("allSizes", Size.values());
            return modelAndView;
        }

        tshirtService.updateTshirtBySizeAndPrice(
                updateTshirtRequest.getId(),
                updateTshirtRequest.getName(),
                updateTshirtRequest.getImage(),
                updateTshirtRequest.getSizes(),
                updateTshirtRequest.getPrice()
        );

        return new ModelAndView("redirect:/tshirts");
    }


    @PostMapping("/availability/{id}")
    public ModelAndView toggleProductAvailability(@PathVariable UUID id) {
        tshirtService.toggleAvailability(id);
        return new ModelAndView("redirect:/tshirts");
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ModelAndView showAddPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());

        ModelAndView modelAndView = new ModelAndView("add-tshirt");
        modelAndView.addObject("tshirt", new CreatedNewTshirt());
        modelAndView.addObject("teams", teamService.getAllTeamsWhichIsNotDeleted());
        modelAndView.addObject("categories", Arrays.asList(Category.values()));
        modelAndView.addObject("sizes", Arrays.asList(Size.values()));
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @PostMapping("/add")
    public ModelAndView saveNewTshirt(@ModelAttribute CreatedNewTshirt createdNewTshirt, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("redirect:/tshirts/add");
        }
        tshirtService.saveTshirt(createdNewTshirt);
        return new ModelAndView("redirect:/tshirts");
    }

    @GetMapping("/retro")
    public ModelAndView getRetroTshirts(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());

        List<Tshirt> retroTshirts = tshirtService.getTshirtsByCategoryAndAvailable(Category.RETRO);

        ModelAndView modelAndView = new ModelAndView("retro");
        modelAndView.addObject("retroTshirts", retroTshirts);
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @GetMapping("/national")
    public ModelAndView getNationalTshirts(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());

        List<Tshirt> nationalTshirts = tshirtService.getTshirtsByCategoryAndAvailable(Category.NATIONAL);

        ModelAndView modelAndView = new ModelAndView("national");
        modelAndView.addObject("nationalTshirts", nationalTshirts);
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @GetMapping("/new")
    public ModelAndView getNewTshirts(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());

        List<Tshirt> newTshirts = tshirtService.getTshirtsByCategoryAndAvailable(Category.NEW);

        ModelAndView modelAndView = new ModelAndView("new");
        modelAndView.addObject("newTshirts", newTshirts);
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @GetMapping("/search")
    public ModelAndView searchTshirts(@RequestParam("team") String teamName,@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());

        List<Tshirt> tshirts = tshirtService.findTshirtsByTeam(teamName);

        if(tshirts.isEmpty()) {

            return new ModelAndView("redirect:/tshirts/emptySearch");
        }

        ModelAndView modelAndView = new ModelAndView("search-results");
        modelAndView.addObject("teamName", teamName);
        modelAndView.addObject("tshirts", tshirts);
        modelAndView.addObject("user", user);

        return modelAndView;
    }
    @GetMapping("/emptySearch")
    public ModelAndView getEmptyPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());

        ModelAndView modelAndView = new ModelAndView("empty-team-page");
        modelAndView.addObject("user", user);

        return modelAndView;
    }
}
