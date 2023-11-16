package com.efundzz.dmnservice.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.camunda.bpm.dmn.engine.DmnDecisionRuleResult;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.engine.DecisionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.efundzz.dmnservice.dto.CRMBreFormRequestDTO;
import com.efundzz.dmnservice.dto.DMNInputDTO;
import com.efundzz.dmnservice.dto.DecisionResultDTO;
import com.efundzz.dmnservice.entity.BankData;
import com.efundzz.dmnservice.repository.BankDataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class DMNEvaluator {
    private static final Logger logger = LoggerFactory.getLogger(DMNEvaluator.class);

    @Autowired
    private DecisionService decisionService;
    @Autowired
    private BankDataRepository bankDataRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> evaluateDecision(String decisionKey, CRMBreFormRequestDTO inputVariables) {
        logger.debug("Start evaluateDecision method");
        DMNInputDTO dmnInputDTO = createDMNEvaluationDTO(inputVariables);
        logger.debug("inputVariables before evaluation: {}", dmnInputDTO);
        Map<String, Object> inputVariablesMap = objectMapper.convertValue(dmnInputDTO, Map.class);
        DmnDecisionTableResult result = decisionService.evaluateDecisionTableByKey(decisionKey, (Map<String, Object>) inputVariablesMap);
        List<Map<String, Object>> outputList = new ArrayList<>();
        for (DmnDecisionRuleResult ruleResult : result) {
            DecisionResultDTO decisionDTO = convertToDecisionResultDTO(ruleResult);
            String bankName = decisionDTO.getEntity();
            String companyCategory = inputVariables.getCompanyCategory();
            double roi = getRoiFromDatabase(bankName, companyCategory);
            double tenure = getTenureFromDatabase(bankName, companyCategory);
            decisionDTO.setRoi(roi);
            decisionDTO.setTenure(tenure);
            double proposedEmi = calculateEmi(inputVariables.getLoanAmount(), roi, tenure);
            decisionDTO.setProposedEmi(proposedEmi);
            if (decisionDTO != null) {
                outputList.add(decisionDTO.toMap());
            }
        }
        logger.debug("Decision evaluation result: {}", outputList);
        logger.debug("End evaluateDecision method");
        return outputList;
    }
    private DecisionResultDTO convertToDecisionResultDTO(DmnDecisionRuleResult ruleResult) {
        DecisionResultDTO decisionDTO = new DecisionResultDTO();
        decisionDTO.setEntity((String) ruleResult.get("entity"));
        return decisionDTO;
    }
    private DMNInputDTO createDMNEvaluationDTO(CRMBreFormRequestDTO inputVariables) {
        DMNInputDTO dmnInputDTO = new DMNInputDTO();
        dmnInputDTO.setCreditScore(inputVariables.getCreditScore());
        dmnInputDTO.setTakeHomeSalaryMonthly(inputVariables.getTakeHomeSalaryMonthly());
        dmnInputDTO.setAge(inputVariables.getAge());
        dmnInputDTO.setCompanyCategory(inputVariables.getCompanyCategory());
        dmnInputDTO.setExperience(inputVariables.getExperience());
        dmnInputDTO.setFoir(inputVariables.getFoir());
        dmnInputDTO.setJobStability(inputVariables.getJobStability());
        dmnInputDTO.setLoanAmount(inputVariables.getLoanAmount());
        dmnInputDTO.setResidentType(inputVariables.getResidentType());
        dmnInputDTO.setOwnHouse(inputVariables.getOwnHouse());
        dmnInputDTO.setSalaryCreditType(inputVariables.getSalaryCreditType());
        return dmnInputDTO;
    }
    private double calculateEmi(double loanAmount, double roi, double tenure) {
        double emi = (loanAmount * (roi / (12 * 100))
                * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1);
        return emi;
    }

    private double getTenureFromDatabase(String bankName, String companyCategory) {
        Optional<BankData> bankData = bankDataRepository.findByBankNameAndCompanyCategory(bankName,
                companyCategory);
        return bankData.map(BankData::getTenure).orElse(0.0);
    }

    private double getRoiFromDatabase(String bankName, String companyCategory) {
        Optional<BankData> bankData = bankDataRepository.findByBankNameAndCompanyCategory(bankName,
                companyCategory);
        return bankData.map(BankData::getRoi).orElse(0.0);
    }
}
