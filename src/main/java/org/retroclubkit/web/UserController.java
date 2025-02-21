package org.retroclubkit.web;

import jakarta.validation.Valid;
import org.retroclubkit.security.AuthenticationMetadata;
import org.retroclubkit.user.model.User;
import org.retroclubkit.user.service.UserService;
import org.retroclubkit.web.dto.UpdateProfileRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {

        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ModelAndView getAllUsers(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());

        List<User> users = userService.getAllUsers();
        ModelAndView modelAndView = new ModelAndView("all-users");
        modelAndView.addObject("users", users);
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @GetMapping("/{id}/profile")
    public ModelAndView getProfileMenu(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getById(authenticationMetadata.getUserId());

        // ✅ Създаваме UpdateProfileRequest и попълваме с текущите данни на потребителя
        UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest();
        updateProfileRequest.setUsername(user.getUsername());
        updateProfileRequest.setFirstName(user.getFirstName());
        updateProfileRequest.setLastName(user.getLastName());
        updateProfileRequest.setEmail(user.getEmail());

        ModelAndView modelAndView = new ModelAndView("my-account");
        modelAndView.addObject("user", user);
        modelAndView.addObject("updateProfileRequest", updateProfileRequest);

        return modelAndView;
    }

    @PostMapping("/{id}/profile")
    public ModelAndView updateUserProfile(
            @Valid @ModelAttribute("updateProfileRequest") UpdateProfileRequest updateProfileRequest,
            BindingResult bindingResult,
            @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        ModelAndView modelAndView = new ModelAndView("my-account");

        User user = userService.getById(authenticationMetadata.getUserId());

        if (bindingResult.hasErrors()) {
            modelAndView.addObject("updateProfileRequest", updateProfileRequest);
            return modelAndView;
        }

        userService.update(updateProfileRequest, user);

        return new ModelAndView("redirect:/home");
    }

    @PutMapping("/{id}/status") // PUT /users/{id}/status
    public String switchUserStatus(@PathVariable UUID id) {

        userService.switchStatus(id);

        return "redirect:/users";
    }

    @PutMapping("/{id}/role") // PUT /users/{id}/role
    public String switchUserRole(@PathVariable UUID id) {

        userService.switchRole(id);

        return "redirect:/users";
    }
}
