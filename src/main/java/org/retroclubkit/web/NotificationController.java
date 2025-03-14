package org.retroclubkit.web;

import org.retroclubkit.notification.client.dto.ContactRequest;
import org.retroclubkit.notification.client.dto.Notification;
import org.retroclubkit.notification.client.dto.NotificationPreference;
import org.retroclubkit.notification.service.NotificationService;
import org.retroclubkit.security.AuthenticationMetadata;
import org.retroclubkit.user.model.User;
import org.retroclubkit.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

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

    @GetMapping
    public ModelAndView getNotificationPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());

        NotificationPreference notificationPreference = notificationService.getNotificationPreference(user.getId());
        List<Notification> notificationHistory = notificationService.getNotificationHistory(user.getId());
        long succeededNotificationsNumber = notificationHistory.stream().filter(notification -> notification.getStatus().equals("SUCCEEDED")).count();
        long failedNotificationsNumber = notificationHistory.stream().filter(notification -> notification.getStatus().equals("FAILED")).count();
        notificationHistory = notificationHistory.stream().limit(5).toList();

        ModelAndView modelAndView = new ModelAndView("notifications");
        modelAndView.addObject("user", user);
        modelAndView.addObject("notificationPreference", notificationPreference);
        modelAndView.addObject("succeededNotificationsNumber", succeededNotificationsNumber);
        modelAndView.addObject("failedNotificationsNumber", failedNotificationsNumber);
        modelAndView.addObject("notificationHistory", notificationHistory);

        return modelAndView;
    }

    @PutMapping("/user-preference")
    public String updateUserPreference(@RequestParam(name = "enabled") boolean enabled, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        notificationService.updateNotificationPreference(authenticationMetadata.getUserId(), enabled);

        return "redirect:/notifications";
    }

    @DeleteMapping
    public String deleteNotificationHistory(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        UUID userId = authenticationMetadata.getUserId();

        notificationService.clearHistory(userId);

        return "redirect:/notifications";
    }

    @PutMapping
    public String retryFailedNotifications(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        UUID userId = authenticationMetadata.getUserId();

        notificationService.retryFailed(userId);

        return "redirect:/notifications";
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
        // Добавяме потребителския ID към заявката
        contactRequest.setUserId(authenticationMetadata.getUserId());

        // Изпращаме съобщението към NotificationClient
        notificationService.sendContactMessage(contactRequest);

        return "redirect:/home"; // Пренасочваме обратно към страницата с нотификациите
    }
}
