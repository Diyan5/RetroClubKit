package org.retroclubkit.user;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Spy;
import org.retroclubkit.exception.DomainException;
import org.retroclubkit.exception.EmailAlreadyExistException;
import org.retroclubkit.exception.PasswordsNotMatchException;
import org.retroclubkit.exception.UsernameAlreadyExistException;
import org.retroclubkit.notification.service.NotificationService;

import org.retroclubkit.user.model.User;
import org.retroclubkit.user.model.UserRole;
import org.retroclubkit.user.repository.UserRepository;
import org.retroclubkit.user.service.UserService;

import org.retroclubkit.web.dto.RegisterRequest;
import org.retroclubkit.web.dto.UpdateProfileRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private NotificationService notificationService;

    @Spy
    @InjectMocks
    private UserService userService;



    @Test
    void loadUserByUsername_ShouldUpdateLastLogin() {
        // Given
        String username = "testUser";
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .username(username)
                .password("encodedPassword")
                .role(UserRole.USER)
                .isActive(true)
                .updatedOn(LocalDateTime.now().minusDays(1))
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        doNothing().when(userService).updateLastLogin(any(UUID.class));


        // When
        UserDetails userDetails = userService.loadUserByUsername(username);

        // Then
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        verify(userService, times(1)).updateLastLogin(userId);
    }


    @ParameterizedTest
    @MethodSource("userRolesArguments")
    void whenChangeUserRole_theCorrectRoleIsAssigned(UserRole currentUserRole, UserRole expectedUserRole) {

        // Given
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .role(currentUserRole)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        userService.switchRole(userId);

        // Then
        assertEquals(expectedUserRole, user.getRole());
    }

    private static Stream<Arguments> userRolesArguments() {

        return Stream.of(
                Arguments.of(UserRole.USER, UserRole.ADMIN),
                Arguments.of(UserRole.ADMIN, UserRole.USER)
        );
    }

    @Test
    void update_ShouldUpdateUserProfile() {
        // Given
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .username("oldUsername")
                .firstName("OldFirst")
                .lastName("OldLast")
                .email("old@example.com")
                .build();

        UpdateProfileRequest updateProfileRequest = UpdateProfileRequest.builder()
                .username("newUsername")
                .firstName("NewFirst")
                .lastName("NewLast")
                .email("new@example.com")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        userService.update(userId, updateProfileRequest);

        // Then
        assertEquals("newUsername", user.getUsername());
        assertEquals("NewFirst", user.getFirstName());
        assertEquals("NewLast", user.getLastName());
        assertEquals("new@example.com", user.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void givenUserWithStatusActive_whenSwitchStatus_thenUserStatusBecomeInactive() {

        // Given
        User user = User.builder()
                .id(UUID.randomUUID())
                .isActive(true)
                .build();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // When
        userService.switchStatus(user.getId());

        // Then
        assertFalse(user.isActive());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void givenUserWithStatusInactive_whenSwitchStatus_thenUserStatusBecomeActive() {

        // Given
        User user = User.builder()
                .id(UUID.randomUUID())
                .isActive(false)
                .build();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // When
        userService.switchStatus(user.getId());

        // Then
        assertTrue(user.isActive());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void register_ShouldCreateNewUser_WhenValidRequest() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("testUser")
                .password("password123")
                .confirmPassword("password123")
                .firstName("John")
                .lastName("Doe")
                .email("test@example.com")
                .build();
        UserRole assignedRole = (userRepository.count() == 0) ? UserRole.ADMIN : UserRole.USER;
        User user = User.builder()
                .id(UUID.randomUUID())
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .role(assignedRole)
                .isActive(true)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());
        when(userRepository.count()).thenReturn(0L); // First user gets ADMIN role
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        User registeredUser = userService.register(registerRequest);

        // Then
        assertNotNull(registeredUser);
        assertEquals("testUser", registeredUser.getUsername());
        assertEquals(UserRole.ADMIN, registeredUser.getRole());
        verify(notificationService, times(1)).saveNotificationPreference(user.getId(), true, user.getEmail());
        verify(notificationService, times(1)).sendNotification(user.getId(), "Successfully register", "You successfully registered!");
    }



    @Test
    void register_ShouldThrowException_WhenUsernameExists() {
        // Given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("testUser")
                .password("password123")
                .confirmPassword("password123")
                .firstName("John")
                .lastName("Doe")
                .email("test@example.com")
                .build();
        when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.of(new User()));

        // Then
        assertThrows(UsernameAlreadyExistException.class, () -> userService.register(registerRequest));
    }

    @Test
    void register_ShouldThrowException_WhenEmailExists() {
        // Given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("testUser")
                .password("password123")
                .confirmPassword("password123")
                .firstName("John")
                .lastName("Doe")
                .email("test@example.com")
                .build();
        when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.of(new User()));

        // Then
        assertThrows(EmailAlreadyExistException.class, () -> userService.register(registerRequest));
    }

    @Test
    void register_ShouldThrowException_WhenPasswordsDoNotMatch() {
        // Given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("testUser")
                .password("password123")
                .confirmPassword("wrongPassword")
                .firstName("John")
                .lastName("Doe")
                .email("test@example.com")
                .build();

        // Then
        assertThrows(PasswordsNotMatchException.class, () -> userService.register(registerRequest));
    }

    @Test
    void givenMissingUserFromDatabase_whenEditUserDetails_thenExceptionIsThrown() {

        UUID userId = UUID.randomUUID();
        UpdateProfileRequest dto = UpdateProfileRequest.builder().build();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> userService.update(userId, dto));
    }

    @Test
    void getAllUsers_ShouldReturnUserList() {
        // Given
        List<User> users = List.of(
                User.builder().id(UUID.randomUUID()).username("user1").email("user1@example.com").build(),
                User.builder().id(UUID.randomUUID()).username("user2").email("user2@example.com").build()
        );
        when(userRepository.findAll()).thenReturn(users);

        // When
        List<User> result = userService.getAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void updateLastLogin_ShouldUpdateLastLoginTime() {
        // Given
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .username("testUser")
                .email("test@example.com")
                .updatedOn(LocalDateTime.now().minusDays(1))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        userService.updateLastLogin(userId);

        // Then
        assertNotNull(user.getUpdatedOn());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
    }

}
