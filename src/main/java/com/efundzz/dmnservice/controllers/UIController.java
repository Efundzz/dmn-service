package com.efundzz.dmnservice.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UIController {

    @GetMapping("/sample")
    public String home() {
        return "index";
    }
}
