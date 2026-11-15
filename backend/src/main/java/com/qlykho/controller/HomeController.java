package com.qlykho.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final String frontendUrl;

    public HomeController(@Value("${app.frontend.url:http://localhost:5173}") String frontendUrl) {
        this.frontendUrl = frontendUrl;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:" + frontendUrl;
    }
}
