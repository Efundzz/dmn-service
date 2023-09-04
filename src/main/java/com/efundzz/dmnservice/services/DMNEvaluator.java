package com.efundzz.dmnservice.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;

import org.camunda.bpm.engine.DecisionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DMNEvaluator {
    private static final Logger logger = LoggerFactory.getLogger(DMNEvaluator.class);
    @Autowired
    private DecisionService decisionService;
    public List<String> evaluateDecision(String decisionKey, Map<String, Object> variables) {
        DmnDecisionTableResult result = decisionService.evaluateDecisionTableByKey(decisionKey, variables);
        List<String> outputList = new ArrayList<String>();
        for (Map<String, Object> ruleResult : result) {
            // Convert the rule result to a string representation and add to the list
            String entity = (String) ruleResult.get("entity");
            if(entity != null && !entity.isEmpty()) {
                outputList.add(entity);
            }
        }
        logger.debug("Decision evaluation result: {}", outputList);
        return outputList;
    }
}

