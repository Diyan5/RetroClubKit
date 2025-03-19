package org.retroclubkit.web;

import org.retroclubkit.exception.*;
import org.retroclubkit.user.model.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class ExceptionAdvice {

    // 1. (First) POST HTTP Request -> /register -> redirect:/register
    // 2. (Second) GET HTTP Request -> /register -> register.html view

    // ВАЖНО: При редирект не връщаме @ResponseStatus(...)!!!
    // 1
    @ExceptionHandler(UsernameAlreadyExistException.class)
    public String handleUsernameAlreadyExist(HttpServletRequest request, RedirectAttributes redirectAttributes, UsernameAlreadyExistException exception) {

        String message = exception.getMessage();

        redirectAttributes.addFlashAttribute("usernameAlreadyExistMessage", message);
        return "redirect:/register";
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public String handleEmailAlreadyExist(HttpServletRequest request, RedirectAttributes redirectAttributes, EmailAlreadyExistException exception) {

        String message = exception.getMessage();

        redirectAttributes.addFlashAttribute("emailAlreadyExistMessage", message);
        return "redirect:/register";
    }

    @ExceptionHandler(PasswordsNotMatchException.class)
    public String handlePasswordsNotMatch(HttpServletRequest request, RedirectAttributes redirectAttributes, PasswordsNotMatchException exception) {

        String message = exception.getMessage();

        redirectAttributes.addFlashAttribute("passwordsNotMatch", message);
        return "redirect:/register";
    }

    @ExceptionHandler(TshirtAlreadyExistException.class)
    public String handleTshirtAlreadyExist(HttpServletRequest request, RedirectAttributes redirectAttributes, TshirtAlreadyExistException exception) {

        String message = exception.getMessage();

        redirectAttributes.addFlashAttribute("tshirtAlreadyExistMessage", message);
        return "redirect:/tshirts/add";
    }

    @ExceptionHandler(TeamAlreadyExistException.class)
    public String handleTeamAlreadyExist(HttpServletRequest request, RedirectAttributes redirectAttributes, TeamAlreadyExistException exception) {

        // Option 1
        //Autowire HttpServletRequest request
        //String username = request.getParameter("username");
        //String message = "%s is already in use!".formatted(username);

        // Option 2
        String message = exception.getMessage();

        redirectAttributes.addFlashAttribute("teamAlreadyExistMessage", message);
        return "redirect:/teams/add";
    }

    @ExceptionHandler(MustBePositiveException.class)
    public String getPositiveNumber(HttpServletRequest request, RedirectAttributes redirectAttributes, MustBePositiveException exception) {

        // Извличане на ID от заявката
        String id = request.getParameter("id");

        // Добавяне на Flash атрибут за съобщението
        String message = exception.getMessage();
        redirectAttributes.addFlashAttribute("positiveMessage", message);

        // Редирект към страницата за редактиране на тениската
        return "redirect:/tshirts/edit/" + id;
    }

    @ExceptionHandler(NotificationServiceFeignCallException.class)
    public String handleNotificationFeignCallException(RedirectAttributes redirectAttributes, NotificationServiceFeignCallException exception) {

        String message = exception.getMessage();

        redirectAttributes.addFlashAttribute("clearHistoryErrorMessage", message);
        return "redirect:/notifications";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            AccessDeniedException.class, // Когато се опотва да достъпи ендпойнт, до който не му е позволено/нямам достъп
            NoResourceFoundException.class, // Когато се опитва да достъпи невалиден ендпойнт
            MethodArgumentTypeMismatchException.class,
            MissingRequestValueException.class,
            InvalidDataAccessResourceUsageException.class,
            DomainException.class,
            DataIntegrityViolationException.class
    })
    public ModelAndView handleNotFoundExceptions(Exception exception) {

        return new ModelAndView("not-found");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAnyException(Exception exception) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("internal-server-error");
        modelAndView.addObject("errorMessage", exception.getClass().getSimpleName());

        return modelAndView;
    }
}
