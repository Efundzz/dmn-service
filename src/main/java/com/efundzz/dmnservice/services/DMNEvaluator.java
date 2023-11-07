package com.efundzz.dmnservice.services;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.engine.DecisionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.efundzz.dmnservice.entity.BankData;
import com.efundzz.dmnservice.repository.BankDataRepository;

@Component
public class DMNEvaluator {
    private static final Logger logger = LoggerFactory.getLogger(DMNEvaluator.class);

    @Autowired
    private DecisionService decisionService;
    @Autowired
    private BankDataRepository bankDataRepository;

    public List<Map<String, Object>> evaluateDecision(String decisionKey, Map<String, Object> variables) {
        DmnDecisionTableResult result = decisionService.evaluateDecisionTableByKey(decisionKey, variables);
        List<Map<String, Object>> outputList = new ArrayList<>();
        for (Map<String, Object> ruleResult : result) {
            String bankName = (String) ruleResult.get("entity");
            if (bankName != null && !bankName.isEmpty()) {
                Map<String, Object> outputMap = new HashMap<>();
                outputMap.put("entity", bankName);
                double loanAmount = Double.parseDouble((String) variables.get("loanAmount"));
                double roi = getRoiFromDatabase(bankName, (String) variables.get("companyCategory"));
                double tenure = getTenureFromDatabase(bankName, (String) variables.get("companyCategory"));
                double emi = calculateEmi(loanAmount, roi, tenure);
                outputMap.put("proposed emi", emi);
                outputMap.put("roi", roi);
                outputMap.put("tenure", tenure);
                if (variables.containsKey("probabilityPercentage") && variables.containsKey("roi") && variables.containsKey("tenure")) {
                    double probabilityPercentage = (double) variables.get("probabilityPercentage");
                    outputMap.put("probabilityPercentage", probabilityPercentage);
                }
                outputList.add(outputMap);
            }
        }
        logger.debug("Decision evaluation result: {}", outputList);
        return outputList;
    }
    private double calculateEmi(double loanAmount, double roi, double tenure) {
        double emi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure))/ (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
        return emi;
    }
    private double getTenureFromDatabase(String bankName, String companyCategory) {
        Optional<BankData> bankData = bankDataRepository.findByBankNameAndCompanyCategory(bankName, companyCategory);
        return bankData.map(BankData::getTenure).orElse(0.0);
    }
    private double getRoiFromDatabase(String bankName, String companyCategory) {
        Optional<BankData> bankData = bankDataRepository.findByBankNameAndCompanyCategory(bankName, companyCategory);
        return bankData.map(BankData::getRoi).orElse(0.0);
    }
}