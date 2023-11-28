package com.efundzz.dmnservice.services;

import com.efundzz.dmnservice.dto.CRMBreFormRequestDTO;
import com.efundzz.dmnservice.entity.DMNEvaluationData;
import com.efundzz.dmnservice.exception.ValidationException;
import com.efundzz.dmnservice.repository.DMNEvaluationDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.efundzz.dmnservice.constants.AppConstants.PL_BRE;
import static java.time.format.DateTimeFormatter.ofPattern;

@Service
public class DMNService {
    @Autowired
    private DMNEvaluationDataRepository dmnDataRepository;

    private final DMNEvaluator dmnEvaluator;

    @Autowired
    private ProbabilityCalServices probabilityCalServices;
    @Autowired
    public DMNService(DMNEvaluator dmnEvaluator) {
        this.dmnEvaluator = dmnEvaluator;
    }
    private final Logger logger = LoggerFactory.getLogger(DMNService.class);
    public List<Map<String, Object>> evaluateDecision(CRMBreFormRequestDTO inputVariables ) {
        validateInputVariables(inputVariables);
        List<Map<String, Object>> decisionResult = dmnEvaluator.evaluateDecision(PL_BRE, inputVariables);
        probabilityCalServices.calculateProbabilityForBanks(decisionResult, inputVariables);
        saveEvaluationData(inputVariables, decisionResult);
        return decisionResult;
    }
    private void saveEvaluationData(CRMBreFormRequestDTO inputVariables, List<Map<String,Object>> decisionResult) {
        DMNEvaluationData evaluationData = new DMNEvaluationData();
        evaluationData.setCreditScore(inputVariables.getCreditScore());
        evaluationData.setBrand(inputVariables.getBrand());
        evaluationData.setAgentId(inputVariables.getAgentId());
        evaluationData.setTakeHomeSalaryMonthly(inputVariables.getTakeHomeSalaryMonthly());
        evaluationData.setAge(inputVariables.getAge());
        evaluationData.setExperience(inputVariables.getExperience());
        evaluationData.setFoir(inputVariables.getFoir());
        evaluationData.setCompanyCategory(inputVariables.getCompanyCategory());
        evaluationData.setLoanAmount(inputVariables.getLoanAmount());
        evaluationData.setResidentType(inputVariables.getResidentType());
        evaluationData.setJobStability(inputVariables.getJobStability());
        evaluationData.setOwnHouse(inputVariables.getOwnHouse());
        evaluationData.setSalaryCreditType(inputVariables.getSalaryCreditType());
        evaluationData.setResponse(decisionResult);
        dmnDataRepository.save(evaluationData);
    }
    // Creating a method validateInputVariables for calling each variable validating
    private void validateInputVariables(CRMBreFormRequestDTO inputVariables) {
        validateCreditScore(inputVariables);
        validateHomeSalary(inputVariables);
        validateAge(inputVariables);
        validateExperience(inputVariables);
        validateResidentType(inputVariables);
        validateLoanAmount(inputVariables);
        validateFoir(inputVariables);
        validateCompanyCategory(inputVariables);
        validateSalaryCreditType(inputVariables);
        validateOwnHouseAnyWhere(inputVariables);
        validateCurrentJobStability(inputVariables);
    }
    // validating Creditscore method
    private void validateCreditScore(CRMBreFormRequestDTO requestDTO) {
        String creditScore = requestDTO.getCreditScore();
        if ( creditScore== null) {
            Map<String, String> fieldErrors = new HashMap<>();
            fieldErrors.put("creditScore", "Missing 'creditScore' in input variables.");
            throw new ValidationException(fieldErrors);
        }
        if (creditScore.equals("F") || creditScore.equals("G")) {
            Map<String, String> fieldErrors = new HashMap<>();
            fieldErrors.put("creditScore", "You are not eligible for your CreditScore concern.");
            throw new ValidationException(fieldErrors);
        }
    }
    //validating takeHomeSalary method
    private void validateHomeSalary(CRMBreFormRequestDTO requestDTO) {
        String salary = requestDTO.getTakeHomeSalary();
        if (salary == null) {
            throw new IllegalArgumentException("Missing 'TakeHomeSalaryMonthly' in input variables.");
        }

        try {
            long takeHomeSalaryMonthly = Long.parseLong((String) salary);
            requestDTO.setTakeHomeSalaryMonthly(takeHomeSalaryMonthly);
            System.out.println(takeHomeSalaryMonthly);
            if (takeHomeSalaryMonthly < 12000) {
                Map<String, String> fieldErrors = new HashMap<>();
                fieldErrors.put("Monthly salary", "'You are not Eligible for your salary concern'");
                throw new ValidationException(fieldErrors);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'TakeHomeSalaryMonthly' must be a valid Number.");
        }
    }
    //Validating age method
    private void validateAge(CRMBreFormRequestDTO requestDTO) {
        String dOB = requestDTO.getDateOfBirth();
        System.out.println(dOB);
        if (dOB == null) {
            throw new IllegalArgumentException("Missing 'Date of Birth'.");
        }
        DateTimeFormatter dateFormatter = ofPattern("yyyy-MM-dd");
        try {
            LocalDate dob = LocalDate.parse((String) dOB, dateFormatter);
            int age = calculateAge(dob);
            requestDTO.setAge(age);
            System.out.println(age);
            if (age < 18 || age > 61) {
                Map<String, String> fieldErrors = new HashMap<>();
                fieldErrors.put("age", "'You are not eligible for your age concern'.");
                throw new ValidationException(fieldErrors);
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("'Date of Birth' must be a valid Date Format.");
        }
    }
    private int calculateAge(LocalDate dob) {
        LocalDate currentDate = LocalDate.now();
        return Period.between(dob, currentDate).getYears();
    }
    //Validating experience method
    private void validateExperience(CRMBreFormRequestDTO requestDTO) {
        String experienceYearsObj = requestDTO.getExperienceYears();
        String experienceMonthsObj = requestDTO.getExperienceMonths();
        if (experienceYearsObj != null && experienceMonthsObj != null) {
            try {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = 0;
                if (experienceYears == 0 && experienceMonths != 0) {
                    experience = experienceMonths;
                    requestDTO.setExperience(experience);
                    System.out.println(experience);

                }
                if (experienceYears != 0 && experienceMonths != 0) {
                    experience = experienceYears * 12 + experienceMonths;
                    requestDTO.setExperience(experience);
                    System.out.println(experience);
                }
                if (experienceYears != 0 && experienceMonths == 0) {
                    experience = experienceYears * 12;
                    requestDTO.setExperience(experience);
                    System.out.println(experience);
                }
                if (experience < 1) {
                    Map<String, String> fieldErrors = new HashMap<>();
                    fieldErrors.put("experience", "You are not eligible for you Experience concern.");
                    throw new ValidationException(fieldErrors);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("'experienceYears' and 'experienceMonths' must be valid integers.");
            }
        }
    }
    //Validating ResidentType method
    private void validateResidentType(CRMBreFormRequestDTO requestDTO) {
        String residentObj = requestDTO.getResidentType();
        if (residentObj == null) {
            throw new IllegalArgumentException("Missing 'ResidentType' in input variables.");
        }
        requestDTO.setResidentType((String) residentObj);
        System.out.println((String) residentObj);
        if (!(((String) residentObj).equalsIgnoreCase("Owned") || ((String) residentObj).equalsIgnoreCase("Rented")
                || ((String) residentObj).equalsIgnoreCase("PG") || ((String) residentObj).equalsIgnoreCase("Staying with Friends")
                || ((String) residentObj).equalsIgnoreCase("Company Accommodation"))) {
            throw new IllegalArgumentException("'ResidentType' must be in the given list");
        }
    }
    //Validating LoanAmount method
    private void validateLoanAmount(CRMBreFormRequestDTO requestDTO) {
        String amount = requestDTO.getAmount();
        if (amount == null) {
            throw new IllegalArgumentException("Missing 'LoanAmount' in input variables.");
        }
        try {
            long loanAmount = Long.parseLong(amount);
            requestDTO.setLoanAmount(loanAmount);
            System.out.println(loanAmount);
            if (loanAmount < 1000 || loanAmount > 10000000) {
                Map<String, String> fieldErrors = new HashMap<>();
                fieldErrors.put("loanAmount", "'No such bank offering for your bargaining Loan Amount '");
                throw new ValidationException(fieldErrors);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'LoanAmount' must be Number.");
        }
    }
    //Validating Foir method
    private void validateFoir(CRMBreFormRequestDTO requestDTO) {
        String emiObj = requestDTO.getEmi();
        String salaryObj = requestDTO.getTakeHomeSalary();
        String companyObj = requestDTO.getCompanyCategory();
        String amountObj = requestDTO.getAmount();
        int proposedEmi = 0;
        double totalDebt = 0;
        int foir = 0;
        if (emiObj == null) {
            throw new IllegalArgumentException("'EMI' must be in input Variables");
        }
        try {
            double emi = Double.parseDouble(emiObj);
            String companyCategory = companyObj;
            double takeHomeSalaryMonthly = Double.parseDouble(salaryObj);
            double loanAmount = Double.parseDouble(amountObj);
            double tenure;
            double roi;
            if (emi == 0) {
                if (companyCategory.equalsIgnoreCase("SA") || companyCategory.equalsIgnoreCase("A")) {
                    roi = 12.0;
                    tenure = 60.0;
                    proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                    totalDebt = proposedEmi;
                    foir = (int) ((totalDebt / takeHomeSalaryMonthly) * 100);
                    System.out.println("foir is" + foir);
                    requestDTO.setFoir(foir);

                } else if (companyCategory.equalsIgnoreCase("B")) {
                    roi = 15.0;
                    tenure = 48.0;
                    proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                    totalDebt = proposedEmi;
                    foir = (int) ((totalDebt / takeHomeSalaryMonthly) * 100);
                    System.out.println("foir is" + foir);
                    requestDTO.setFoir(foir);

                } else if (companyCategory.equalsIgnoreCase("C")) {
                    roi = 18.0;
                    tenure = 36.0;
                    proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                    totalDebt = proposedEmi;
                    foir = (int) ((totalDebt / takeHomeSalaryMonthly) * 100);
                    System.out.println("foir is" + foir);
                    requestDTO.setFoir(foir);

                } else if (companyCategory.equalsIgnoreCase("D") || companyCategory.equalsIgnoreCase("E") || companyCategory.equalsIgnoreCase("O")) {
                    roi = 20.0;
                    tenure = 36.0;
                    proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                    totalDebt = proposedEmi;
                    foir = (int) ((totalDebt / takeHomeSalaryMonthly) * 100);
                    System.out.println("foir is" + foir);
                    requestDTO.setFoir(foir);
                }
            }
            if (emi != 0) {
                if (companyCategory.equalsIgnoreCase("SA") || companyCategory.equalsIgnoreCase("A")) {
                    roi = 12.0;
                    tenure = 60.0;
                    proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                    totalDebt = emi + proposedEmi;
                    foir = (int) ((totalDebt / takeHomeSalaryMonthly) * 100);
                    System.out.println("foir is" + foir);
                    requestDTO.setFoir(foir);
                } else if (companyCategory.equalsIgnoreCase("B")) {
                    roi = 15.0;
                    tenure = 48.0;
                    proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                    totalDebt = emi + proposedEmi;
                    foir = (int) ((totalDebt / takeHomeSalaryMonthly) * 100);
                    System.out.println("foir is" + foir);
                    requestDTO.setFoir(foir);
                } else if (companyCategory.equalsIgnoreCase("C")) {
                    roi = 18.0;
                    tenure = 36.0;
                    proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                    totalDebt = emi + proposedEmi;
                    foir = (int) ((totalDebt / takeHomeSalaryMonthly) * 100);
                    System.out.println("foir is" + foir);
                    requestDTO.setFoir(foir);

                } else if (companyCategory.equalsIgnoreCase("D") || companyCategory.equalsIgnoreCase("E") || companyCategory.equalsIgnoreCase("O")) {
                    roi = 20.0;
                    tenure = 36.0;
                    proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                    totalDebt = emi + proposedEmi;
                    foir = (int) ((totalDebt / takeHomeSalaryMonthly) * 100);
                    System.out.println("foir is" + foir);
                    requestDTO.setFoir(foir);
                }
            }
            if (foir > 100) {
                Map<String, String> fieldErrors = new HashMap<>();
                fieldErrors.put("foir", "Your are not eligible due to your 'FOIR'");
                throw new ValidationException(fieldErrors);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'FOIR' must be a number");
        }
    }
    //Validating the Company Category method
    private void validateCompanyCategory(CRMBreFormRequestDTO requestDTO) {
        String companyObj = requestDTO.getCompanyCategory();
        if (companyObj == null) {
            throw new IllegalArgumentException("CompanyCategory must be in inputvariables");
        }
        requestDTO.setCompanyCategory(companyObj);
        System.out.println(companyObj);
        if (!(companyObj.equalsIgnoreCase("SA") || companyObj.equalsIgnoreCase("A") || companyObj.equalsIgnoreCase("B") || companyObj.equalsIgnoreCase("C") || companyObj.equalsIgnoreCase("D") || companyObj.equalsIgnoreCase("E") || companyObj.equalsIgnoreCase("O"))) {
            throw new IllegalArgumentException("Company Category must be in the given List");
        }
    }
    //Validating SalaryCreditType method
    private void validateSalaryCreditType(CRMBreFormRequestDTO requestDTO) {
        String creditObj = requestDTO.getSalaryCreditType();
        if (creditObj == null) {
            throw new IllegalArgumentException("SalaryCredit must be in InputVariables");
        }
        requestDTO.setSalaryCreditType(creditObj);
        System.out.println(creditObj);
        if(!(creditObj.equalsIgnoreCase("Yes") || creditObj.equalsIgnoreCase("No"))) {
            throw new IllegalArgumentException("SalaryCreditType must be in the given list only");
        }
        if(creditObj.equalsIgnoreCase("No")) {
            Map<String, String> fieldErrors = new HashMap<>();
            fieldErrors.put("salaryCreditType", "No such bank is providing loan without salary credit in bank account");
            throw new ValidationException(fieldErrors);
        }
    }
    //Validating OwnHouseAnyWhere method
    private void validateOwnHouseAnyWhere(CRMBreFormRequestDTO requestDTO) {
        String ownHouseObj = requestDTO.getOwnHouse();
        if (ownHouseObj == null) {
            throw new IllegalArgumentException("OwnHouse Anywhere must be in inputVariables");
        }
        requestDTO.setOwnHouse(ownHouseObj);
        System.out.println(ownHouseObj);
        if(!(ownHouseObj.equalsIgnoreCase("Yes") || ownHouseObj.equalsIgnoreCase("No"))) {
            throw new IllegalArgumentException("OwnHouse Anywhere must be in the given list only");
        }
    }
    //Validating CurrentJobStability method
    private void validateCurrentJobStability(CRMBreFormRequestDTO requestDTO) {
        String currentJobObj = requestDTO.getCurrentjobStability();
        if (currentJobObj == null) {
            throw new IllegalArgumentException("CurrentJobStability must be in the inputVariables");
        }
        try {
            int jobStability = Integer.parseInt(currentJobObj);
            requestDTO.setJobStability(jobStability);
            System.out.println(jobStability);
            if (jobStability < 1) {
                Map<String, String> fieldErrors = new HashMap<>();
                fieldErrors.put("incontestability", "You are not eligible for your current job stability.");
                throw new ValidationException(fieldErrors);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("current job stability must be a number");
        }
    }
}