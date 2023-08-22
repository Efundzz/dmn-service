package com.efundzz.dmnservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;


import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;

import org.camunda.bpm.engine.DecisionService;

@Component
public class DMNEvaluator {

    @Autowired
    private DecisionService decisionService;

    public String evaluateDecision(String decisionKey, Map<String, Object> variables) {
        DmnDecisionTableResult result = decisionService.evaluateDecisionTableByKey(decisionKey, variables);
        return result.getSingleEntry();
    }
}

