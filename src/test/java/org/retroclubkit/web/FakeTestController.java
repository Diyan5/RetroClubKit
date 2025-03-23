package org.retroclubkit.web;

import org.retroclubkit.exception.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
class FakeTestController {

    @PostMapping("/username-exists")
    public void throwUsernameExists() {
        throw new UsernameAlreadyExistException("Username is already taken");
    }

    @PostMapping("/email-exists")
    public void throwEmailExists() {
        throw new EmailAlreadyExistException("Email is already used");
    }

    @PostMapping("/passwords-not-match")
    public void throwPasswordsNotMatch() {
        throw new PasswordsNotMatchException("Passwords do not match");
    }

    @PostMapping("/team-exists")
    public void throwTeamExists() {
        throw new TeamAlreadyExistException("Team already exists");
    }

    @PostMapping("/tshirt-exists")
    public void throwTshirtExists() {
        throw new TshirtAlreadyExistException("T-shirt already exists");
    }
}
