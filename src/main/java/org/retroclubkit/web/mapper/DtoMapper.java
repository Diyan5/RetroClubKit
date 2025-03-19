package org.retroclubkit.web.mapper;

import org.retroclubkit.user.model.User;
import org.retroclubkit.web.dto.UpdateProfileRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DtoMapper {

    public static UpdateProfileRequest mapUserToUserEditRequest(User user) {

        return UpdateProfileRequest.builder()
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }
}
