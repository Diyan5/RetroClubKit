package org.retroclubkit;

import lombok.experimental.UtilityClass;
import org.retroclubkit.user.model.User;
import org.retroclubkit.user.model.UserRole;

import java.util.UUID;
import java.time.LocalDateTime;
@UtilityClass
public class TestBuilder {

    public static User aRandomUser() {

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("User")
                .firstName("test1")
                .lastName("test2")
                .password("123123")
                .role(UserRole.USER)
                .isActive(true)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        return user;
    }
}
