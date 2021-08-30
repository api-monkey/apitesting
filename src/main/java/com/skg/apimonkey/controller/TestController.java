package com.skg.apimonkey.controller;

import com.skg.apimonkey.service.SwaggerParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    SwaggerParserService parserService;

    @GetMapping("/test")
    public Object home(@RequestParam String param) {

        return parserService.getSwaggerRestApi(param);
    }
}