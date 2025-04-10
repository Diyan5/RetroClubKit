package org.retroclubkit.web;

import jakarta.validation.Valid;
import org.retroclubkit.security.AuthenticationMetadata;
import org.retroclubkit.team.service.TeamService;
import org.retroclubkit.user.model.User;
import org.retroclubkit.user.service.UserService;
import org.retroclubkit.web.dto.CreatedNewTeam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;


@Controller
@RequestMapping("/teams")
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
    public ModelAndView saveNewTeam(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getById(authenticationMetadata.getUserId());

        ModelAndView modelAndView = new ModelAndView("add-team");
        modelAndView.addObject("createdNewTeam", new CreatedNewTeam());
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @PostMapping("/add")
    public ModelAndView saveNewTeam(@Valid CreatedNewTeam createdNewTeam,
                                    BindingResult bindingResult,
                                    @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        ModelAndView modelAndView = new ModelAndView("add-team");

        User user = userService.getById(authenticationMetadata.getUserId());

        if (bindingResult.hasErrors()) {
            modelAndView.addObject("user", user);
            modelAndView.addObject("createdNewTeam", createdNewTeam);
            return modelAndView;
        }
        teamService.saveTeam(createdNewTeam);
        return new ModelAndView("redirect:/teams");
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ModelAndView showTeams(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getById(authenticationMetadata.getUserId());
        ModelAndView modelAndView = new ModelAndView("all-teams");
        modelAndView.addObject("teams", teamService.getAllTeamsWhichIsNotDeleted());
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @DeleteMapping("/delete/{id}")
    public ModelAndView deleteTshirt(@PathVariable UUID id) {
        teamService.deleteTeamById(id);
        return new ModelAndView("redirect:/teams");
    }

}
