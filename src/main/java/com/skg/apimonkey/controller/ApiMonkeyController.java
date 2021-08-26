package com.skg.apimonkey.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ApiMonkeyController {

    @GetMapping("/")
    public String homePage(Model model) {

        model.addAttribute("title", "APIMonkey");
        model.addAttribute("description", "The No-code API testing solution");
        model.addAttribute("robots", "index");

        return "index";
    }
}