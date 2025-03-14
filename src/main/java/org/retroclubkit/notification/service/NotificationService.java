package org.retroclubkit.notification.service;

import org.retroclubkit.notification.client.dto.*;
import org.retroclubkit.security.AuthenticationMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.retroclubkit.exception.NotificationServiceFeignCallException;
import org.retroclubkit.notification.client.NotificationClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class NotificationService {

    private final NotificationClient notificationClient;

    @Value("${notification-svc.failure-message.clear-history}")
    private String clearHistoryFailedMessage;

    @Autowired
    public NotificationService(NotificationClient notificationClient) {
        this.notificationClient = notificationClient;
    }

    public void saveNotificationPreference(UUID userId, boolean isEmailEnabled, String email) {

        UpsertNotificationPreference notificationPreference = UpsertNotificationPreference.builder()
                .userId(userId)
                .contactInfo(email)
                .type("EMAIL")
                .notificationEnabled(isEmailEnabled)
                .build();

        // Invoke Feign client and execute HTTP Post Request.
        ResponseEntity<Void> httpResponse = notificationClient.upsertNotificationPreference(notificationPreference);
        if (!httpResponse.getStatusCode().is2xxSuccessful()) {
            log.error("[Feign call to notification-svc failed] Can't save user preference for user with id = [%s]".formatted(userId));
        }
    }

    public NotificationPreference getNotificationPreference(UUID userId) {

        ResponseEntity<NotificationPreference> httpResponse = notificationClient.getUserPreference(userId);

        if (!httpResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Notification preference for user id [%s] does not exist.".formatted(userId));
        }

        return httpResponse.getBody();
    }

    public List<Notification> getNotificationHistory(UUID userId) {

        ResponseEntity<List<Notification>> httpResponse = notificationClient.getNotificationHistory(userId);

        System.out.println(httpResponse.getBody());
        return httpResponse.getBody();
    }

    public void sendNotification(UUID userId, String emailSubject, String emailBody) {

        NotificationRequest notificationRequest = NotificationRequest.builder()
                .userId(userId)
                .subject(emailSubject)
                .body(emailBody)
                .build();

        // Servive to Service
        ResponseEntity<Void> httpResponse;
        try {
            httpResponse = notificationClient.sendNotification(notificationRequest);
            if (!httpResponse.getStatusCode().is2xxSuccessful()) {
                log.error("[Feign call to notification-svc failed] Can't send email to user with id = [%s]".formatted(userId));
            }
        } catch (Exception e) {
            log.warn("Can't send email to user with id = [%s] due to 500 Internal Server Error.".formatted(userId));
        }
    }

    public void updateNotificationPreference(UUID userId, boolean enabled) {

        try {
            notificationClient.updateNotificationPreference(userId, enabled);
        } catch (Exception e) {
            log.warn("Can't update notification preferences for user with id = [%s].".formatted(userId));
        }
    }

    public void clearHistory(UUID userId) {

        try {
            notificationClient.clearHistory(userId);
        } catch (Exception e) {
            log.error("Unable to call notification-svc for clear notification history.".formatted(userId));
            throw new NotificationServiceFeignCallException(clearHistoryFailedMessage);
        }
    }

    public void retryFailed(UUID userId) {

        try {
            notificationClient.retryFailedNotifications(userId);
        } catch (Exception e) {
            log.error("Unable to call notification-svc for clear notification history.".formatted(userId));
            throw new NotificationServiceFeignCallException(clearHistoryFailedMessage);
        }
    }


    public void sendContactMessage(ContactRequest contactRequest) {
        // Извикваме метода от NotificationClient, който изпраща контактното съобщение
        notificationClient.sendContactMessage(contactRequest);
    }
}
