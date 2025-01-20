package org.retroclubkit.user.service;

import org.retroclubkit.user.model.User;
import org.retroclubkit.user.repository.UserRepository;
import org.retroclubkit.web.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(RegisterRequest registerRequest) {

        //TODO make registerRequest dto with annotation
        return null;
    }
}
