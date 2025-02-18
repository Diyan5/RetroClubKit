package org.retroclubkit.schedular;

import org.retroclubkit.contact.service.ContactService;
import org.retroclubkit.user.model.User;
import org.retroclubkit.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class InactiveUserScheduler {
    private final UserRepository userRepository;
    private final ContactService contactService;

    @Autowired
    public InactiveUserScheduler(UserRepository userRepository, ContactService contactService) {
        this.userRepository = userRepository;
        this.contactService = contactService;
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

    private void sendDeactivationEmail(User user) {
        String subject = "Your account has been deactivated";
        String body = "Dear " + user.getUsername() + ",\n\n" +
                "Your account has been deactivated due to inactivity for more than 6 months.\n" +
                "If you want to reactivate it, please contact our support team.\n\n" +
                "Best regards,\nRetro Club Kit Team";

        contactService.sendUserEmail(user.getEmail(), subject, body);
    }
}
