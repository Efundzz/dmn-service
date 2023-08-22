package com.efundzz.dmnservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DMNService {

    @Autowired
    private DMNEvaluator dmnEvaluator;

    public String evaluateDecision(Map<String, Object> inputVariables) {
        String decisionKey = (String) inputVariables.get("decisionKey");

        inputVariables.remove("decisionKey");

        // TODO: Validate input variables

        return dmnEvaluator.evaluateDecision(decisionKey, inputVariables);
    }
}
