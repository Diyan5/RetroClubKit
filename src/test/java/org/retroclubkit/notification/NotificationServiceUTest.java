package org.retroclubkit.notification;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.retroclubkit.notification.client.NotificationClient;
import org.retroclubkit.notification.client.dto.*;
import org.retroclubkit.notification.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class NotificationServiceUTest {

    @Mock
    private NotificationClient notificationClient;

    @InjectMocks
    private NotificationService notificationService;

    private UUID userId;
    private UpsertNotificationPreference notificationPreference;
    private NotificationPreference notificationPreferenceResponse;
    private ContactRequest contactRequest;

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        // Обект за saveNotificationPreference()
        notificationPreference = UpsertNotificationPreference.builder()
                .userId(userId)
                .contactInfo("test@example.com")
                .type("EMAIL")
                .notificationEnabled(true)
                .build();

        // Обект за getNotificationPreference()
        notificationPreferenceResponse = new NotificationPreference();
        notificationPreferenceResponse.setType("EMAIL");
        notificationPreferenceResponse.setEnabled(true);
        notificationPreferenceResponse.setContactInfo("test@example.com");

        contactRequest = new ContactRequest();
        contactRequest.setUsername("Test User");
        contactRequest.setEmail("test@example.com");
        contactRequest.setMessage("Test Message");
    }

    @Test
    void updateNotificationPreference_ShouldCallFeignClient() {
        notificationService.updateNotificationPreference(userId, true);
        verify(notificationClient, times(1)).updateNotificationPreference(userId, true);
    }

    @Test
    void updateNotificationPreference_ShouldLogWarning_WhenFeignCallThrowsException() {
        doThrow(new RuntimeException("Feign exception"))
                .when(notificationClient).updateNotificationPreference(any(UUID.class), anyBoolean());

        notificationService.updateNotificationPreference(userId, true);

        verify(notificationClient, times(1)).updateNotificationPreference(userId, true);
    }

    @Test
    void sendContactMessage_ShouldCallFeignClient() {
        notificationService.sendContactMessage(contactRequest);
        verify(notificationClient, times(1)).sendContactMessage(contactRequest);
    }

    @Test
    void sendNotification_ShouldCallFeignClientSuccessfully() {
        when(notificationClient.sendNotification(any(NotificationRequest.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        notificationService.sendNotification(userId, "Test Subject", "Test Body");

        verify(notificationClient, times(1)).sendNotification(any(NotificationRequest.class));
    }

    @Test
    void sendNotification_ShouldLogError_WhenFeignCallFails() {
        when(notificationClient.sendNotification(any(NotificationRequest.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        notificationService.sendNotification(userId, "Test Subject", "Test Body");

        verify(notificationClient, times(1)).sendNotification(any(NotificationRequest.class));
    }

    @Test
    void sendNotification_ShouldLogWarning_WhenFeignCallThrowsException() {
        doThrow(new RuntimeException("Feign exception"))
                .when(notificationClient).sendNotification(any(NotificationRequest.class));

        notificationService.sendNotification(userId, "Test Subject", "Test Body");

        verify(notificationClient, times(1)).sendNotification(any(NotificationRequest.class));
    }

    @Test
    void getNotificationPreference_ShouldReturnPreference() {
        // Given
        when(notificationClient.getUserPreference(userId)).thenReturn(ResponseEntity.ok(notificationPreferenceResponse));

        // When
        NotificationPreference result = notificationService.getNotificationPreference(userId);

        // Then
        assertNotNull(result);
        assertEquals("EMAIL", result.getType());
        assertTrue(result.isEnabled());
        verify(notificationClient, times(1)).getUserPreference(userId);
    }

    @Test
    void getNotificationPreference_ShouldThrowException_WhenFeignCallFails() {
        // Given
        when(notificationClient.getUserPreference(userId)).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        // Then
        Exception exception = assertThrows(RuntimeException.class, () ->
                notificationService.getNotificationPreference(userId));

        assertEquals("Notification preference for user id [%s] does not exist.".formatted(userId), exception.getMessage());
        verify(notificationClient, times(1)).getUserPreference(userId);
    }

    @Test
    void saveNotificationPreference_ShouldCallFeignClient() {
        // Given
        when(notificationClient.upsertNotificationPreference(any(UpsertNotificationPreference.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        // When
        notificationService.saveNotificationPreference(userId, true, "test@example.com");

        // Then
        verify(notificationClient, times(1)).upsertNotificationPreference(any(UpsertNotificationPreference.class));
    }

    @Test
    void saveNotificationPreference_ShouldLogError_WhenFeignCallFails() {
        // Given
        when(notificationClient.upsertNotificationPreference(any(UpsertNotificationPreference.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        // When
        notificationService.saveNotificationPreference(userId, true, "test@example.com");

        // Then
        verify(notificationClient, times(1)).upsertNotificationPreference(any(UpsertNotificationPreference.class));
    }
}

