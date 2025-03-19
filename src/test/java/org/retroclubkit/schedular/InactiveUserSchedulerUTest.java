package org.retroclubkit.schedular;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.retroclubkit.notification.service.NotificationService;
import org.retroclubkit.schedular.InactiveUserScheduler;
import org.retroclubkit.user.model.User;
import org.retroclubkit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class InactiveUserSchedulerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private InactiveUserScheduler inactiveUserScheduler;

    private User inactiveUser;

    @BeforeEach
    void setUp() {
        inactiveUser = new User();
        inactiveUser.setId(java.util.UUID.randomUUID());
        inactiveUser.setEmail("test@example.com");
        inactiveUser.setUsername("testuser");
        inactiveUser.setActive(true);
        inactiveUser.setUpdatedOn(LocalDateTime.now().minusMonths(6).minusDays(1));
    }

    @Test
    void deactivateInactiveUsers_ShouldDeactivateUsersAndSendEmail() {
        when(userRepository.findByIsActiveTrueAndUpdatedOnBefore(any(LocalDateTime.class)))
                .thenReturn(List.of(inactiveUser));

        inactiveUserScheduler.deactivateInactiveUsers();

        verify(userRepository, times(1)).save(inactiveUser);
        verify(notificationService, times(1))
                .sendNotification(eq(inactiveUser.getId()), anyString(), anyString());
    }

    @Test
    void deactivateInactiveUsers_ShouldDoNothingWhenNoInactiveUsers() {
        when(userRepository.findByIsActiveTrueAndUpdatedOnBefore(any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        inactiveUserScheduler.deactivateInactiveUsers();

        verify(userRepository, never()).save(any(User.class));
        verify(notificationService, never()).sendNotification(any(), anyString(), anyString());
    }

    @Test
    void alertInactiveUsers_ShouldSendAlertsToInactiveUsers() {
        when(userRepository.findByIsActiveTrueAndUpdatedOnBefore(any(LocalDateTime.class)))
                .thenReturn(List.of(inactiveUser));

        inactiveUserScheduler.alertInactiveUsers();

        verify(notificationService, times(1))
                .sendNotification(eq(inactiveUser.getId()), anyString(), anyString());
    }

    @Test
    void alertInactiveUsers_ShouldDoNothingWhenNoUsersToAlert() {
        when(userRepository.findByIsActiveTrueAndUpdatedOnBefore(any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        inactiveUserScheduler.alertInactiveUsers();

        verify(notificationService, never()).sendNotification(any(), anyString(), anyString());
    }
}
