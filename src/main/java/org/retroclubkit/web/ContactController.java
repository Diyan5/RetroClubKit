package org.retroclubkit.web;

import org.retroclubkit.contact.service.ContactService;
import org.retroclubkit.web.dto.ContactRequest;
import org.retroclubkit.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class ContactController {

    private final UserService userService;
    private final ContactService contactService;

    @Autowired
    public ContactController(UserService userService, ContactService contactService) {
        this.userService = userService;
        this.contactService = contactService;
    }

    @GetMapping("/contact")
    public String showContactForm(Model model) {
        model.addAttribute("contactRequest", new ContactRequest());
        return "contact";
    }

    @PostMapping("/contact")
    public String handleContactForm(@ModelAttribute ContactRequest contactRequest) {
        contactService.saveMessage(contactRequest);
        contactService.sendEmail(contactRequest);
        return "redirect:/contact";
    }
}
