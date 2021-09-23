package com.skg.apimonkey.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class ApiMonkeyController {

    @GetMapping("/")
    public String homePage(Model model) {

        model.addAttribute("title", "APIMonkey");
        model.addAttribute("description", "The No-code API testing solution");
        model.addAttribute("robots", "index");

        return "index";
    }

    @GetMapping("/get-started")
    public String scholarshipSearch(Model model,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {

        model.addAttribute("title", "Get started");
        model.addAttribute("description", "Get started with testing your API's");
        model.addAttribute("robots", "noindex");

        return "run_page";
    }
}