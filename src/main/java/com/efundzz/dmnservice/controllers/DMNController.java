package com.efundzz.dmnservice.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.efundzz.dmnservice.Exception.ValidationException;
import com.efundzz.dmnservice.services.DMNService;

@RestController
@CrossOrigin
public class DMNController {

    @Autowired
    DMNService dmnService;

    @PostMapping("/evaluate")
    public ResponseEntity<?> evaluateDecision(@RequestBody Map<String, Object> inputVariables) {
        try {
            List<String> decisionResult = dmnService.evaluateDecision(inputVariables);
            if(decisionResult.isEmpty()) {
                return ResponseEntity.noContent().build();
            }else {
                return ResponseEntity.ok(decisionResult);
            }
        } catch (ValidationException ex) {
            // If validation fails return a structured error response
            Map<String, String> fieldErrors = ex.getFieldErrors();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(fieldErrors);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
