package com.efundzz.dmnservice.controllers;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class UIController {
    @GetMapping("/decision")
    public String showDecisionForm() {
        return "index";
    }
}
