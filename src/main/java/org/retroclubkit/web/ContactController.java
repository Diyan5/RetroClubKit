package org.retroclubkit.web;

import org.retroclubkit.contact.service.ContactService;
import org.retroclubkit.security.AuthenticationMetadata;
import org.retroclubkit.user.model.User;
import org.retroclubkit.web.dto.ContactRequest;
import org.retroclubkit.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/contact")
public class ContactController {

    private final UserService userService;
    private final ContactService contactService;

    @Autowired
    public ContactController(UserService userService, ContactService contactService) {
        this.userService = userService;
        this.contactService = contactService;
    }

    @GetMapping
    public ModelAndView showContactForm(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getById(authenticationMetadata.getUserId());
        ModelAndView modelAndView = new ModelAndView("contact");
        modelAndView.addObject("contactRequest", new ContactRequest());
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @PostMapping
    public String handleContactForm(@ModelAttribute ContactRequest contactRequest) {
        contactService.saveMessage(contactRequest);
        contactService.sendEmail(contactRequest);
        return "redirect:/contact";
    }
}
