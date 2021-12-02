package com.skg.apimonkey.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Slf4j
@Controller
public class AuthenticationController {

    @GetMapping("/pages/500")
    public String page500(Model model) {
        return "error/500";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("title", "Login");
        model.addAttribute("description", "Login page");
        model.addAttribute("robots", "noindex");
        return "auth/sign-in";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "/auth/access-denied";
    }
}