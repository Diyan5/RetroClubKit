package org.retroclubkit.web.mapper;

import org.retroclubkit.user.model.User;
import org.retroclubkit.web.dto.UpdateProfileRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class DtoMapperUTest {

    @Test
    void givenHappyPath_whenMappingUserToUserEditRequest(){

        // Given
        User user = User.builder()
                .username("user")
                .firstName("test")
                .lastName("test2")
                .email("test@gmial.com")
                .build();

        // When
        UpdateProfileRequest resultDto = DtoMapper.mapUserToUserEditRequest(user);

        // Then
        assertEquals(user.getUsername(), resultDto.getUsername());
        assertEquals(user.getFirstName(), resultDto.getFirstName());
        assertEquals(user.getLastName(), resultDto.getLastName());
        assertEquals(user.getEmail(), resultDto.getEmail());

    }
}
