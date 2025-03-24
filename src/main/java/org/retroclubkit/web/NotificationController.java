package org.retroclubkit.web;

import org.retroclubkit.notification.client.dto.ContactRequest;
import org.retroclubkit.notification.service.NotificationService;
import org.retroclubkit.security.AuthenticationMetadata;
import org.retroclubkit.user.model.User;
import org.retroclubkit.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final UserService userService;
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(UserService userService, NotificationService notificationService) {

        this.userService = userService;
        this.notificationService = notificationService;
    }

    @PutMapping("/user-preference")
    public String updateUserPreference(@RequestParam(name = "enabled") boolean enabled, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        notificationService.updateNotificationPreference(authenticationMetadata.getUserId(), enabled);

        return "redirect:/users/" + authenticationMetadata.getUserId() + "/profile";
    }

    @GetMapping("/contact")
    public ModelAndView getContactPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getById(authenticationMetadata.getUserId());
        ModelAndView modelAndView = new ModelAndView("contact");
        modelAndView.addObject("contactRequest", new ContactRequest());
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @PostMapping("/contact")
    public String submitContactMessage(@ModelAttribute ContactRequest contactRequest, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        contactRequest.setUserId(authenticationMetadata.getUserId());
        notificationService.sendContactMessage(contactRequest);

        return "redirect:/home";
    }
}
