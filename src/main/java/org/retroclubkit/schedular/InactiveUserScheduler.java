package org.retroclubkit.schedular;

import org.retroclubkit.notification.service.NotificationService;
import org.retroclubkit.user.model.User;
import org.retroclubkit.user.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class InactiveUserScheduler {
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public InactiveUserScheduler(UserRepository userRepository, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void deactivateInactiveUsers() {

        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);


        List<User> inactiveUsers = userRepository.findByIsActiveTrueAndUpdatedOnBefore(sixMonthsAgo);

        for (User user : inactiveUsers) {
            user.setActive(false);
            userRepository.save(user);

            sendDeactivationEmail(user);
            System.out.println("User " + user.getEmail() + " has been deactivated and notified.");
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void alertInactiveUsers() {

        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);


        List<User> inactiveUsers = userRepository.findByIsActiveTrueAndUpdatedOnBefore(threeMonthsAgo);

        for (User user : inactiveUsers) {

            sendAlertEmail(user);
            System.out.println("User " + user.getEmail() + " alert.");
        }
    }

    private void sendAlertEmail(User user) {
        String subject = "Reminder: Your Account Has Been Inactive for 3 Months";
        String body = "Dear " + user.getUsername() + ",\n\n" +
                "We noticed that you haven't logged into your account for 3 months. " +
                "Please note that if you remain inactive for 6 months, your account will be deactivated.\n\n" +
                "To keep your account active, simply log in within the next 3 months.\n\n" +
                "Thank you for using our services.\n\n" +
                "Best regards,\n" +
                "The [Application/Platform Name] Team";

        notificationService.sendNotification(user.getId(), subject, body);
    }
//
    private void sendDeactivationEmail(User user) {
        String subject = "Your account has been deactivated";
        String body = "Dear " + user.getUsername() + ",\n\n" +
                "Your account has been deactivated due to inactivity for more than 6 months.\n" +
                "If you want to reactivate it, please contact our support team.\n\n" +
                "Best regards,\nRetro Club Kit Team";

        notificationService.sendNotification(user.getId(), subject, body);
    }
}
