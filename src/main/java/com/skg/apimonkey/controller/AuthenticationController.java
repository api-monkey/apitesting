package com.skg.apimonkey.controller;

import com.skg.apimonkey.domain.user.User;
import com.skg.apimonkey.domain.user.UserSignUp;
import com.skg.apimonkey.exception.UserAlreadyExistException;
import com.skg.apimonkey.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Objects;


@Slf4j
@Controller
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @GetMapping("/pages/500")
    public String page500(Model model) {
        return "error/500";
    }

    @GetMapping(path = {"/login", "/sign-in"})
    public String login(Model model) {
        model.addAttribute("title", "Login");
        model.addAttribute("description", "Login page");
        model.addAttribute("robots", "noindex");
        return "auth/sign-in";
    }

    @GetMapping("/sign-up")
    public String signUp(Model model) {

        UserSignUp userDto = new UserSignUp();
        model.addAttribute("user", userDto);

        model.addAttribute("title", "Register new user");
        model.addAttribute("description", "Register new user page");
        model.addAttribute("robots", "noindex");
        model.addAttribute("registered", false);
        return "auth/sign-up";
    }

    @PostMapping("/sign-up")
    public String registerUser(@ModelAttribute("user") @Valid UserSignUp userDto,
                               BindingResult bindingResult,
                               Model model,
                               HttpServletRequest request) {

        model.addAttribute("title", "Register new user");
        model.addAttribute("description", "Register new user page");
        model.addAttribute("robots", "noindex");

        model.addAttribute("user", userDto);
        boolean isRegistered = false;

        if (!bindingResult.hasErrors()) {

            try {
                User registeredUser = userService.registerNewUserAccount(userDto);
                isRegistered = Objects.nonNull(registeredUser);

            } catch (UserAlreadyExistException uaeEx) {
                model.addAttribute("errorMessage", "An account for that email already exists.");
            }
        }

        model.addAttribute("registered", isRegistered);
        return "auth/sign-up";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "/auth/access-denied";
    }
}