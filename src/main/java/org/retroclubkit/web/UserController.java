package org.retroclubkit.web;

import jakarta.validation.Valid;
import org.retroclubkit.notification.client.dto.NotificationPreference;
import org.retroclubkit.notification.service.NotificationService;
import org.retroclubkit.security.AuthenticationMetadata;
import org.retroclubkit.user.model.User;
import org.retroclubkit.user.service.UserService;
import org.retroclubkit.web.dto.UpdateProfileRequest;
import org.retroclubkit.web.mapper.DtoMapper;
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
    private final NotificationService notificationService;

    public UserController(UserService userService, NotificationService notificationService) {

        this.userService = userService;
        this.notificationService = notificationService;
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

        ModelAndView modelAndView = new ModelAndView("my-account");
        modelAndView.addObject("user", user);
        modelAndView.addObject("updateProfileRequest", DtoMapper.mapUserToUserEditRequest(user));

        NotificationPreference notificationPreference = notificationService.getNotificationPreference(user.getId());
        modelAndView.addObject("notificationPreference", notificationPreference);
        return modelAndView;
    }

    @PutMapping("/{id}/profile")
    public ModelAndView updateUserProfile(
            @Valid @ModelAttribute("updateProfileRequest") UpdateProfileRequest updateProfileRequest,
            BindingResult bindingResult,
            @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        ModelAndView modelAndView = new ModelAndView("my-account");

        User user = userService.getById(authenticationMetadata.getUserId());

        if (bindingResult.hasErrors()) {
            modelAndView.addObject("user", user);
            modelAndView.addObject("updateProfileRequest", updateProfileRequest);
            modelAndView.addObject("notificationPreference", notificationService.getNotificationPreference(user.getId()));
            return modelAndView;
        }

        userService.update(user.getId(), updateProfileRequest);
        String emailBody = "You successfully edit your profile!";


        NotificationPreference notificationPreference = notificationService.getNotificationPreference(user.getId());
        notificationService.saveNotificationPreference(user.getId(), notificationPreference.isEnabled(), updateProfileRequest.getEmail());


        notificationService.sendNotification(user.getId(), "Edit profile", emailBody);

        return new ModelAndView("redirect:/home");
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public String switchUserStatus(@PathVariable UUID id) {

        userService.switchStatus(id);

        return "redirect:/users";
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public String switchUserRole(@PathVariable UUID id) {

        userService.switchRole(id);

        return "redirect:/users";
    }
}
