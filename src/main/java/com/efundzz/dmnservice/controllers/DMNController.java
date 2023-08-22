package com.efundzz.dmnservice.controllers;

import com.efundzz.dmnservice.services.DMNService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class DMNController {

    @Autowired
    private DMNService dmnService;

    @PostMapping("/evaluate")
    public ResponseEntity<String> evaluateDecision(@RequestBody Map<String, Object> inputVariables) {
        String decisionResult = dmnService.evaluateDecision(inputVariables);
        return ResponseEntity.ok(decisionResult);
    }

}
