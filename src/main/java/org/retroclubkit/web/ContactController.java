package org.retroclubkit.web;

import jakarta.servlet.http.HttpSession;
import org.retroclubkit.contact.service.ContactService;
import org.retroclubkit.tshirt.model.Tshirt;
import org.retroclubkit.user.model.User;
import org.retroclubkit.web.dto.ContactRequest;
import org.retroclubkit.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;


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
    public ModelAndView showContactForm(HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");

        User user = userService.getById(userId);
        ModelAndView modelAndView = new ModelAndView("contact");
        modelAndView.addObject("contactRequest", new ContactRequest());
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @PostMapping("/contact")
    public String handleContactForm(@ModelAttribute ContactRequest contactRequest) {
        contactService.saveMessage(contactRequest);
        contactService.sendEmail(contactRequest);
        return "redirect:/contact";
    }
}
