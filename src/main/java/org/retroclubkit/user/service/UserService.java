package org.retroclubkit.user.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import org.retroclubkit.exception.DomainException;
import org.retroclubkit.exception.*;
import org.retroclubkit.notification.client.dto.NotificationPreference;
import org.retroclubkit.notification.service.NotificationService;
import org.retroclubkit.security.AuthenticationMetadata;
import org.retroclubkit.user.model.User;
import org.retroclubkit.user.model.UserRole;
import org.retroclubkit.user.repository.UserRepository;
import org.retroclubkit.web.dto.RegisterRequest;
import org.retroclubkit.web.dto.UpdateProfileRequest;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService implements UserDetailsService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, NotificationService notificationService) {
        this.userRepository = userRepository;

        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
    }

    @Transactional
    public User register(RegisterRequest registerRequest) {

        Optional<User> optionUser = userRepository.findByUsername(registerRequest.getUsername());
        Optional<User> byEmail = userRepository.findByEmail(registerRequest.getEmail());
        if (optionUser.isPresent()) {
            throw new UsernameAlreadyExistException("Username [%s] already exist.".formatted(registerRequest.getUsername()));
        }

        if (byEmail.isPresent()) {
            throw new EmailAlreadyExistException("Email [%s] already exist.".formatted(registerRequest.getEmail()));
        }

        if(!registerRequest.getConfirmPassword().equals(registerRequest.getPassword())) {
            throw new PasswordsNotMatchException("Password does not match.");
        }

        User user = userRepository.save(initializeUser(registerRequest));

        notificationService.saveNotificationPreference(user.getId(), true, user.getEmail());
        String emailBody = "You successfully registered!";
        notificationService.sendNotification(user.getId(), "Successfully register", emailBody);

        log.info("Successfully create new user account for username [%s] and id [%s]".formatted(user.getUsername(), user.getId()));

        return user;
    }

    private User initializeUser(RegisterRequest registerRequest) {
        UserRole role = userRepository.count() == 0 ? UserRole.ADMIN : UserRole.USER;

        return User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(role)
                .isActive(true)
                .email(registerRequest.getEmail())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
    }

    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    public User getById(UUID id) {

        return userRepository.findById(id).orElseThrow(() -> new DomainException("User with id [%s] does not exist.".formatted(id)));
    }

    public void switchStatus(UUID userId) {

        User user = getById(userId);
        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    public void switchRole(UUID userId) {

        User user = getById(userId);

        if (user.getRole() == UserRole.USER) {
            user.setRole(UserRole.ADMIN);
        } else {
            user.setRole(UserRole.USER);
        }

        userRepository.save(user);
    }

    public void updateLastLogin(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.updateLastLogin();
        userRepository.save(user);
    }

    @Transactional
    public void update(UUID userId, UpdateProfileRequest updateProfileRequest) {
        User user = getById(userId);

        user.setUsername(updateProfileRequest.getUsername());
        user.setFirstName(updateProfileRequest.getFirstName());
        user.setLastName(updateProfileRequest.getLastName());
        user.setEmail(updateProfileRequest.getEmail());

        userRepository.save(user);
    }

    // Всеки пък, когато потребител се логва, Spring Security ще извиква този метод
    // за да вземе детайлите на потребителя с този username
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username).orElseThrow(() -> new DomainException("User with this username does not exist."));
        updateLastLogin(user.getId());
        return new AuthenticationMetadata(user.getId(), username, user.getPassword(), user.getRole(), user.isActive());
    }
}
