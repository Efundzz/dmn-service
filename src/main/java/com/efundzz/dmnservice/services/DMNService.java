package com.efundzz.dmnservice.services;

import static java.time.format.DateTimeFormatter.ofPattern;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.efundzz.dmnservice.dto.CRMBreFormRequestDTO;
import com.efundzz.dmnservice.entity.DMNEvaluationData;
import com.efundzz.dmnservice.exception.ValidationException;
import com.efundzz.dmnservice.repository.DMNEvaluationDataRepository;

@Service
public class DMNService {
    @Autowired
    private DMNEvaluationDataRepository dmnDataRepository;

    private final DMNEvaluator dmnEvaluator;
    @Autowired
    public DMNService(DMNEvaluator dmnEvaluator) {
        this.dmnEvaluator = dmnEvaluator;
    }
    private final Logger logger = LoggerFactory.getLogger(DMNService.class);
    public List<Map<String, Object>> evaluateDecision(CRMBreFormRequestDTO inputVariables ) {
        String decisionKey = "PLBRE_Decisioning";
        logger.info("Evaluating decision with key: {}", decisionKey);
        logger.debug("Input variables: {}", inputVariables);
        // TODO: Validate input variables
        validateInputVariables(inputVariables);
        logger.debug("Input variables just before evaluation: {}", inputVariables);
        List<Map<String, Object>> decisionResult = dmnEvaluator.evaluateDecision(decisionKey, inputVariables);
        // Calculating probability for each eligible bank
        calculateProbabilityForBanks(decisionResult, inputVariables);
        //Saving input and output data
        saveEvaluationData(inputVariables, decisionResult);
        logger.info("DMN evaluation completed successfully.");
        return decisionResult;
    }
    private void saveEvaluationData(CRMBreFormRequestDTO inputVariables, List<Map<String,Object>> decisionResult) {
        DMNEvaluationData evaluationData = new DMNEvaluationData();
        evaluationData.setCreditScore(inputVariables.getCreditScore());
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
        try {
            if (creditScore.equals("F") || creditScore.equals("G")) {
                Map<String, String> fieldErrors = new HashMap<>();
                fieldErrors.put("creditScore", "You are not eligible for your CreditScore concern.");
                throw new ValidationException(fieldErrors);
            }
        } catch (ValidationException e) {
            throw e;
        }
    }
    //validating takeHomeSalary method
    private void validateHomeSalary(CRMBreFormRequestDTO requestDTO) {
        String salary = requestDTO.getTakeHomeSalary();
        if (salary == null) {
            throw new IllegalArgumentException("Missing 'TakeHomeSalaryMonthly' in input variables.");
        }

        if (salary instanceof String) {
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
    }
    //Validating age method
    private void validateAge(CRMBreFormRequestDTO requestDTO) {
        String dOB = requestDTO.getDateOfBirth();
        System.out.println(dOB);
        if (dOB == null) {
            throw new IllegalArgumentException("Missing 'Date of Birth'.");
        }
        if (dOB instanceof String) {
            DateTimeFormatter dateFormatter = ofPattern("yyyy-MM-dd");
            String dobStr = (String) dOB;
            try {
                LocalDate dob = LocalDate.parse(dobStr, dateFormatter);
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
    }
    private int calculateAge(LocalDate dob) {
        LocalDate currentDate = LocalDate.now();
        return Period.between(dob, currentDate).getYears();
    }
    //Validating experience method
    private void validateExperience(CRMBreFormRequestDTO requestDTO) {
        String experienceYearsObj = requestDTO.getExperienceYears();
        String experienceMonthsObj = requestDTO.getExperienceMonths();
        if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
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
        if (!(residentObj instanceof String)) {
            throw new IllegalArgumentException("'ResidentType' must be a String.");
        }
        String residentType = (String) residentObj;
        requestDTO.setResidentType(residentType);
        System.out.println(residentType);
        if (!(residentType.equalsIgnoreCase("Owned") || residentType.equalsIgnoreCase("Rented")
                || residentType.equalsIgnoreCase("PG") || residentType.equalsIgnoreCase("Staying with Friends")
                || residentType.equalsIgnoreCase("Company Accommodation"))) {
            throw new IllegalArgumentException("'ResidentType' must be in the given list");
        }
    }
    //Validating LoanAmount method
    private void validateLoanAmount(CRMBreFormRequestDTO requestDTO) {
        String amount = requestDTO.getAmount();
        if (amount == null) {
            throw new IllegalArgumentException("Missing 'LoanAmount' in input variables.");
        }
        if (amount instanceof String) {
            try {
                long loanAmount = Long.parseLong( amount);
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
        if (emiObj instanceof String) {
            try {
                double emi = Double.parseDouble( emiObj);
                String companyCategory =  companyObj;
                double takeHomeSalaryMonthly = Double.parseDouble(salaryObj);
                double loanAmount = Double.parseDouble(amountObj);
                double tenure ;
                double roi ;
                if (emi == 0) {
                    if (companyCategory.equalsIgnoreCase("SA") ||companyCategory.equalsIgnoreCase("A")) {
                        roi = 12.0;
                        tenure = 60.0;
                        proposedEmi =  (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure))/ (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        totalDebt = proposedEmi;
                        foir = (int) ((totalDebt/takeHomeSalaryMonthly)*100);
                        System.out.println("foir is" + foir);
                        requestDTO.setFoir(foir);

                    } else if (companyCategory.equalsIgnoreCase("B")) {
                        roi = 15.0;
                        tenure = 48.0;
                        proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure))/ (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        totalDebt = proposedEmi;
                        foir = (int) ((totalDebt/takeHomeSalaryMonthly)*100);
                        System.out.println("foir is" + foir);
                        requestDTO.setFoir(foir);

                    } else if (companyCategory.equalsIgnoreCase("C")) {
                        roi = 18.0;
                        tenure = 36.0;
                        proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure))/ (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        totalDebt = proposedEmi;
                        foir = (int) ((totalDebt/takeHomeSalaryMonthly)*100);
                        System.out.println("foir is" + foir);
                        requestDTO.setFoir(foir);

                    } else if (companyCategory.equalsIgnoreCase("D") ||companyCategory.equalsIgnoreCase("E") || companyCategory.equalsIgnoreCase("O")) {
                        roi = 20.0;
                        tenure = 36.0;
                        proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure))/ (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        totalDebt = proposedEmi;
                        foir = (int) ((totalDebt/takeHomeSalaryMonthly)*100);
                        System.out.println("foir is" + foir);
                        requestDTO.setFoir(foir);
                    }
                }
                if (emi!= 0) {
                    if (companyCategory.equalsIgnoreCase("SA") ||companyCategory.equalsIgnoreCase("A")) {
                        roi = 12.0;
                        tenure = 60.0;
                        proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure))/ (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        totalDebt = emi + proposedEmi;
                        foir = (int) ((totalDebt/takeHomeSalaryMonthly)*100);
                        System.out.println("foir is" + foir);
                        requestDTO.setFoir(foir);
                    } else if (companyCategory.equalsIgnoreCase("B")) {
                        roi = 15.0;
                        tenure = 48.0;
                        proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure))/ (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        totalDebt = emi + proposedEmi;
                        foir = (int) ((totalDebt/takeHomeSalaryMonthly)*100);
                        System.out.println("foir is" + foir);
                        requestDTO.setFoir(foir);
                    } else if (companyCategory.equalsIgnoreCase("C")) {
                        roi = 18.0;
                        tenure = 36.0;
                        proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure))/ (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        totalDebt = emi + proposedEmi;
                        foir = (int) ((totalDebt/takeHomeSalaryMonthly)*100);
                        System.out.println("foir is" + foir);
                        requestDTO.setFoir(foir);

                    } else if (companyCategory.equalsIgnoreCase("D") ||companyCategory.equalsIgnoreCase("E") || companyCategory.equalsIgnoreCase("O")) {
                        roi = 20.0;
                        tenure = 36.0;
                        proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure))/ (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        totalDebt = emi + proposedEmi;
                        foir = (int) ((totalDebt/takeHomeSalaryMonthly)*100);
                        System.out.println("foir is" + foir);
                        requestDTO.setFoir(foir);
                    }
                }
                if (foir > 100) {
                    Map<String, String> fieldErrors = new HashMap<> ();
                    fieldErrors.put("foir", "Your are not eligible due to your 'FOIR'");
                    throw new ValidationException(fieldErrors);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("'FOIR' must be a number");
            }
        }
    }
    //Validating the Company Category method
    private void validateCompanyCategory(CRMBreFormRequestDTO requestDTO) {
        String companyObj = requestDTO.getCompanyCategory();
        if (companyObj == null) {
            throw new IllegalArgumentException("CompanyCategory must be in inputvariables");
        }
        if (!(companyObj instanceof String)) {
            throw new IllegalArgumentException("CompanyCategory must be a String");
        }
        String companyCategory = companyObj;
        requestDTO.setCompanyCategory(companyCategory);
        System.out.println(companyCategory);
        if (!(companyCategory.equalsIgnoreCase("SA") || companyCategory.equalsIgnoreCase("A") || companyCategory.equalsIgnoreCase("B") || companyCategory.equalsIgnoreCase("C") || companyCategory.equalsIgnoreCase("D") || companyCategory.equalsIgnoreCase("E") || companyCategory.equalsIgnoreCase("O"))) {
            throw new IllegalArgumentException("Company Category must be in the given List");
        }
    }
    //Validating SalaryCreditType method
    private void validateSalaryCreditType(CRMBreFormRequestDTO requestDTO) {
        String creditObj = requestDTO.getSalaryCreditType();
        if (creditObj == null) {
            throw new IllegalArgumentException("SalaryCredit must be in InputVariables");
        }
        if (!(creditObj instanceof String)) {
            throw new IllegalArgumentException("SalaryCreditType must be String");
        }
        String salaryCreditType =  creditObj;
        requestDTO.setSalaryCreditType(salaryCreditType);
        System.out.println(salaryCreditType);
        if(!(salaryCreditType.equalsIgnoreCase("Yes") || salaryCreditType.equalsIgnoreCase("No"))) {
            throw new IllegalArgumentException("SalaryCreditType must be in the given list only");
        }
        if(salaryCreditType.equalsIgnoreCase("No")) {
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
        if (!(ownHouseObj instanceof String)) {
            throw new IllegalArgumentException("OwmHouse Anywhere must be String");
        }
        String ownHouse = ownHouseObj;
        requestDTO.setOwnHouse(ownHouse);
        System.out.println(ownHouse);
        if(!(ownHouse.equalsIgnoreCase("Yes") || ownHouse.equalsIgnoreCase("No"))) {
            throw new IllegalArgumentException("OwnHouse Anywhere must be in the given list only");
        }
    }
    //Validating CurrentJobStability method
    private void validateCurrentJobStability(CRMBreFormRequestDTO requestDTO) {
        String currentJobObj = requestDTO.getCurrentjobStability();
        if (currentJobObj == null) {
            throw new IllegalArgumentException("CurrentJobStability must be in the inputVariables");
        }
        if((currentJobObj instanceof String)) {
            try {
                int jobStability = Integer.parseInt(currentJobObj);
                requestDTO.setJobStability(jobStability);
                System.out.println(jobStability);
                if(jobStability < 1) {
                    Map<String, String> fieldErrors = new HashMap<>();
                    fieldErrors.put("currentjobstability", "You are not eligible for your current job stability.");
                    throw new ValidationException(fieldErrors);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("current job stability must be a number");
            }
        }
    }
    // Adding the following method to calculate probability for each eligible bank
    private void calculateProbabilityForBanks(List<Map<String, Object>> decisionResult, CRMBreFormRequestDTO inputDTO) {
        for (Map<String, Object> bankResult : decisionResult) {
            String bankName = (String) bankResult.get("entity");
            double bankProbability = calculateProbabilityForBank(bankName, inputDTO);
            bankResult.put("probabilityPercentage", bankProbability);
        }
    }

    // Adding the following method to calculate probability for each bank
    private double calculateProbabilityForBank(String bankName, CRMBreFormRequestDTO inputDTO) {
        String creditScore = inputDTO.getCreditScore();
        long takeHomeSalaryMonthly = inputDTO.getTakeHomeSalaryMonthly();
        String experienceYearsObj = inputDTO.getExperienceYears();
        String experienceMonthsObj = inputDTO.getExperienceMonths();
        String residentType = inputDTO.getResidentType();
        long loanAmount = inputDTO.getLoanAmount();
        int foir = inputDTO.getFoir();
        String companyCategory = inputDTO.getCompanyCategory();
        int jobStability = inputDTO.getJobStability();

        double csProbability = 0;
        double expProbability = 0;
        double laProbability = 0;
        double resProbability = 0;
        double fProbability = 0;
        double sProbability = 0;
        double comProbability = 0;
        double jProbability = 0;
        double bankProbability = 0.0;
        //validating probability for Cashe
        if (bankName.equals("Cashe")) {
            switch (creditScore) {
                case "A+":
                    csProbability = 1.1;
                    break;
                case "A":
                    csProbability = 1.05;
                    break;
                case "B":
                    csProbability = 1.0;
                    break;
                case "C":
                    csProbability = 0.9;
                    break;
                case "D":
                    csProbability = 0.8;
                    break;
                case "NH":
                    csProbability = 1.0;
                    break;
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience >= 36) {expProbability = 1.1;
                } else if (experience > 12 && experience < 36) { expProbability = 1.0;
                } else if (experience == 12) {expProbability = 0.9;
                }else {expProbability = 0.0;
                }
            }
            if (loanAmount >= 10000 && loanAmount <= 400000) {
                laProbability = 1.1;
            }
            switch (residentType) {
                case "Owned":
                    resProbability = 1.1;
                    break;
                case "Rented":
                    resProbability = 0.9;
                    break;
                case "Company Accommodation":
                    resProbability = 0.9;
                    break;
                case " PG":
                    resProbability = 0.5;
                    break;
                case "Staying with Friends":
                    resProbability = 0.5;
                    break;
            }
            if (foir <= 20) {fProbability = 1.2;
            } else if (foir > 20 && foir <= 30) {fProbability = 1.1;
            } else if (foir > 30 && foir <= 40) {fProbability = 1.0;
            } else {fProbability = 0.9;
            }
            if(takeHomeSalaryMonthly == 12000) {sProbability = 1.0;
            } else if (takeHomeSalaryMonthly >= (12000+(12000*0.4))) {sProbability = 1.3;
            } else if(takeHomeSalaryMonthly >= (12000+(12000*0.2)) && takeHomeSalaryMonthly < (12000+(12000*0.4))) {
                sProbability = 1.1;
            }else {sProbability = 0.0;
            }
            switch (companyCategory) {
                case "SA":
                    comProbability = 1.05;
                    break;
                case "A":
                    comProbability = 1.0;
                    break;
                case "B":
                    comProbability = 0.9;
                    break;
                case "C":
                    comProbability = 0.8;
                    break;
                case "D":
                    comProbability = 0.7;
                    break;
                case "E":
                    comProbability = 0.0;
                    break;
                case "O":
                    comProbability = 0.6;
                    break;
            }
            if (jobStability >=1) {jProbability = 1.0;
            } else {jProbability = 0.0;
            }
            int probability = (int) (csProbability + expProbability + laProbability + resProbability + fProbability + comProbability + sProbability + jProbability);
            System.out.println("Overall Probability: " + probability);
            if (probability >= 8.0) {bankProbability = 95;
            }else if (probability >= 7.0 && probability < 8.0 ) {bankProbability = 90;
            }else if (probability >= 6.0 && probability < 7.0) {bankProbability = 85;
            }else if (probability >= 5.0 && probability < 6.0 ) {bankProbability = 80;
            }else if (probability >= 4.0 && probability < 5.0) {bankProbability = 75;
            } else {bankProbability = 70;
            }
            System.out.println("Final Probability Percentage for Cashe bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for MySubhLife bank
        if (bankName.equals("MyShubhLife")) {
            switch (creditScore) {
                case "A+":
                    csProbability = 1.1;
                    break;
                case "A":
                    csProbability = 1.05;
                    break;
                case "B":
                    csProbability = 1.0;
                    break;
                case "C":
                    csProbability = 0.9;
                    break;
                case "D":
                    csProbability = 0.8;
                    break;
                case "NH":
                    csProbability = 1.0;
                    break;
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience >= 36) {expProbability = 1.1;
                } else if (experience > 12 && experience < 36) {expProbability = 1.0;
                } else if (experience == 12) {expProbability = 0.9;
                }else {expProbability = 0.0;
                }
            }
            if (loanAmount >= 5000 && loanAmount <= 300000) {laProbability = 1.1;
            }
            switch (residentType) {
                case "Owned":
                    resProbability = 1.1;
                    break;
                case "Rented":
                    resProbability = 0.9;
                    break;
                case "Company Accommodation":
                    resProbability = 0.9;
                    break;
                case " PG":
                    resProbability = 0.5;
                    break;
            }
            if (foir <= 20) {
                fProbability = 1.2;
            } else if (foir > 20 && foir <= 30) {
                fProbability = 1.1;
            } else if (foir > 30 && foir <= 40) {
                fProbability = 1.0;
            } else {
                fProbability = 0.9;
            }
            if(takeHomeSalaryMonthly == 12000) {sProbability = 1.0;
            } else if (takeHomeSalaryMonthly >= (12000+(12000*0.4))) {sProbability = 1.3;
            } else if(takeHomeSalaryMonthly >= (12000+(12000*0.2)) && takeHomeSalaryMonthly < (12000+(12000*0.4))) {sProbability = 1.1;
            }
            switch (companyCategory) {
                case "SA":
                    comProbability = 1.05;
                    break;
                case "A":
                    comProbability = 1.0;
                    break;
                case "B":
                    comProbability = 0.9;
                    break;
                case "C":
                    comProbability = 0.8;
                    break;
                case "D":
                    comProbability = 0.7;
                    break;
                case "E":
                    comProbability = 0.0;
                    break;
                case "O":
                    comProbability = 0.6;
                    break;
            }
            if (jobStability >=1) {jProbability = 1.0;
            } else {jProbability = 0.0;
            }
            int probability = (int) (csProbability + expProbability + laProbability + resProbability + fProbability + comProbability + sProbability + jProbability);
            System.out.println("Overall Probability: " + probability);
            if (probability >= 8.0) {
                bankProbability = 95;
            }else if (probability >= 7.0 && probability < 8.0 ) {
                bankProbability = 90;
            }else if (probability >= 6.0 && probability < 7.0) {
                bankProbability = 85;
            }else if (probability >= 5.0 && probability < 6.0 ) {
                bankProbability = 80;
            }else if (probability >= 4.0 && probability < 5.0) {
                bankProbability = 75;
            } else {
                bankProbability = 70;
            }
            System.out.println("Final Probability Percentage for MYShubhLife Bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for IDFC bank
        if (bankName.equals("IDFC")) {
            switch (creditScore) {
                case "A+":
                    csProbability = 1.1;
                    break;
                case "A":
                    csProbability = 1.05;
                    break;
                case "B":
                    csProbability = 1.0;
                    break;
                case "NH":
                    csProbability = 1.0;
                    break;
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience >= 36) { expProbability = 1.1;
                } else if (experience > 24 && experience < 36) {expProbability = 1.0;
                } else if (experience == 24) {expProbability = 0.9;
                }else {expProbability = 0.0;
                }
            }
            if (loanAmount >=100000 && loanAmount <= 500000) {laProbability = 1.1;
            } else if (loanAmount > 500000 && loanAmount <= 1000000) {laProbability = 1.0;
            } else if (loanAmount > 1000000 && loanAmount <= 2000000) {laProbability = 0.9;
            } else if (loanAmount > 2000000 && loanAmount <= 3000000) {laProbability = 0.8;
            } else if (loanAmount > 3000000 && loanAmount <= 5000000) {laProbability = 0.7;
            } else {laProbability = 0.0;
            }
            switch (residentType) {
                case "Owned":
                    resProbability = 1.1;
                    break;
                case "Rented":
                    resProbability = 0.9;
                    break;
                case "Company Accommodation":
                    resProbability = 0.9;
                    break;
                case "PG":
                    resProbability = 0.5;
                    break;
                case "Staying with Friends":
                    resProbability = 0.5;
                    break;
            }
            if (foir <= 20) {fProbability = 1.2;
            } else if (foir > 20 && foir <= 30) {fProbability = 1.1;
            } else if (foir > 30 && foir <= 40) {fProbability = 1.0;
            } else if (foir > 40 && foir <= 50){fProbability = 0.9;
            } else if (foir > 50 && foir <= 70){fProbability = 0.7;
            }
            if(takeHomeSalaryMonthly == 20000) {sProbability = 1.0;
            } else if (takeHomeSalaryMonthly >= (20000+(20000*0.4))) {sProbability = 1.3;
            } else if(takeHomeSalaryMonthly >= (20000+(20000*0.2)) && takeHomeSalaryMonthly < (20000+(20000*0.4))) {sProbability = 1.1;
            }
            switch (companyCategory) {
                case "SA":
                    comProbability = 1.05;
                    break;
                case "A":
                    comProbability = 1.0;
                    break;
                case "B":
                    comProbability = 0.9;
                    break;
                case "C":
                    comProbability = 0.8;
                    break;
                case "D":
                    comProbability = 0.7;
                    break;
                case "O":
                    comProbability = 0.6;
                    break;
            }
            if (jobStability >=3) {jProbability = 1.0;
            } else {jProbability = 0.0;
            }
            int probability = (int) (csProbability + expProbability + laProbability + resProbability + fProbability + comProbability + sProbability + jProbability);
            System.out.println("Overall Probability: " + probability);
            if (probability >= 8.0) {
                bankProbability = 95;
            }else if (probability >= 7.0 && probability < 8.0 ) {
                bankProbability = 90;
            }else if (probability >= 6.0 && probability < 7.0) {
                bankProbability = 85;
            }else if (probability >= 5.0 && probability < 6.0 ) {
                bankProbability = 80;
            }else if (probability >= 4.0 && probability < 5.0) {
                bankProbability = 75;
            } else {
                bankProbability = 70;
            }
            System.out.println("Final Probability Percentage for IDFC bank: " + bankProbability);
            return bankProbability;
        }
        //validating probability for Axis Bank
        if (bankName.equals("Axis Bank")) {
            switch (creditScore) {
                case "A+":
                    csProbability = 1.1;
                    break;
                case "A":
                    csProbability = 1.05;
                    break;
                case "B":
                    csProbability = 1.0;
                    break;
                case "NH":
                    csProbability = 1.0;
                    break;
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience >= 36) {expProbability = 1.1;
                } else if (experience > 12 && experience < 36) {expProbability = 1.0;
                } else if (experience == 12) {expProbability = 0.9;
                }else {expProbability = 0.0;
                }
            }
            if (loanAmount >= 100000 && loanAmount <= 500000) {laProbability = 1.1;
            } else if (loanAmount > 500000 && loanAmount <= 1000000) {laProbability = 1.0;
            } else if (loanAmount > 1000000 && loanAmount <= 2000000) {laProbability = 0.9;
            } else if (loanAmount > 2000000 && loanAmount <= 3000000) {laProbability = 0.8;
            } else if (loanAmount > 3000000 && loanAmount <= 4000000) {laProbability = 0.7;
            } else {laProbability = 0.0;
            }
            switch (residentType) {
                case "Owned":
                    resProbability = 1.1;
                    break;
                case "Rented":
                    resProbability = 0.9;
                    break;
                case "Company Accommodation":
                    resProbability = 0.9;
                    break;
                case " PG":
                    resProbability = 0.5;
                    break;
                case "Staying with Friends":
                    resProbability = 0.5;
                    break;
            }
            if (foir <= 20) {fProbability = 1.2;
            } else if (foir > 20 && foir <= 30) {fProbability = 1.1;
            } else if (foir > 30 && foir <= 40) {fProbability = 1.0;
            } else if(foir > 40 && foir <= 50) {fProbability = 0.9;
            }else {fProbability = 0.8;
            }
            if(takeHomeSalaryMonthly == 25000) {sProbability = 1.0;
            } else if (takeHomeSalaryMonthly >= (25000+(25000*0.4))) {sProbability = 1.3;
            } else if(takeHomeSalaryMonthly >= (25000+(25000*0.2)) && takeHomeSalaryMonthly < (25000+(25000*0.4))) {sProbability = 1.1;
            }
            switch (companyCategory) {
                case "SA":
                    comProbability = 1.05;
                    break;
                case "A":
                    comProbability = 1.0;
                    break;
                case "B":
                    comProbability = 0.9;
                    break;
                case "C":
                    comProbability = 0.8;
                    break;
                case "D":
                    comProbability = 0.7;
                    break;
                case "E":
                    comProbability = 0.0;
                    break;
                case "O":
                    comProbability = 0.6;
                    break;
            }
            if (jobStability >=1) {jProbability = 1.0;
            } else {jProbability = 0.0;
            }
            int probability = (int) (csProbability + expProbability + laProbability + resProbability + fProbability + comProbability + sProbability + jProbability);
            System.out.println("Overall Probability: " + probability);
            if (probability >= 8.0) {
                bankProbability = 95;
            }else if (probability >= 7.0 && probability < 8.0 ) {
                bankProbability = 90;
            }else if (probability >= 6.0 && probability < 7.0) {
                bankProbability = 85;
            }else if (probability >= 5.0 && probability < 6.0 ) {
                bankProbability = 80;
            }else if (probability >= 4.0 && probability < 5.0) {
                bankProbability = 75;
            } else {
                bankProbability = 70;
            }
            System.out.println("Final Probability Percentage for Axis bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for HDFC bank
        if (bankName.equals("HDFC")) {
            switch (creditScore) {
                case "A+":
                    csProbability = 1.1;
                    break;
                case "A":
                    csProbability = 1.0;
                    break;
                case "B":
                    csProbability = 0.9;
                    break;
                case "NH":
                    csProbability = 1.0;
                    break;
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience >= 36) {expProbability = 1.1;
                } else if (experience > 12 && experience < 36) {expProbability = 1.0;
                } else if (experience == 12) {expProbability = 0.9;
                }else {expProbability = 0.0;
                }
            }
            if (loanAmount >= 50000 && loanAmount <= 500000) {laProbability = 1.1;
            } else if (loanAmount > 500000 && loanAmount <= 1000000) {laProbability = 1.0;
            } else if (loanAmount > 1000000 && loanAmount <= 2000000) {laProbability = 0.9;
            } else if (loanAmount > 2000000 && loanAmount <= 3000000) {laProbability = 0.8;
            } else if (loanAmount > 3000000 && loanAmount <= 4000000) {laProbability = 0.7;
            } else if(loanAmount > 4000000 && loanAmount <= 6000000){laProbability = 0.6;
            } else {laProbability = 0.5;
            }
            switch (residentType) {
                case "Owned":
                    resProbability = 1.1;
                    break;
                case "Rented":
                    resProbability = 0.9;
                    break;
                case "Company Accommodation":
                    resProbability = 0.9;
                    break;
                case "PG":
                    resProbability = 0.5;
                    break;
                case "Staying with Friends":
                    resProbability = 0.5;
                    break;
            }
            if (foir <= 20) {fProbability = 1.2;
            } else if (foir > 20 && foir <= 30) {fProbability = 1.1;
            } else if (foir > 30 && foir <= 40) {fProbability = 1.0;
            } else if(foir > 40 && foir <= 50){fProbability = 0.9;
            } else {fProbability = 0.8;
            }
            if(takeHomeSalaryMonthly == 25000) {sProbability = 1.0;
            } else if (takeHomeSalaryMonthly >= (25000+(25000*0.4))) {sProbability = 1.3;
            } else if(takeHomeSalaryMonthly >= (25000+(25000*0.2)) && takeHomeSalaryMonthly < (25000+(25000*0.4))) {sProbability = 1.1;
            }
            switch (companyCategory) {
                case "SA":
                    comProbability = 1.05;
                    break;
                case "A":
                    comProbability = 1.0;
                    break;
                case "B":
                    comProbability = 0.9;
                    break;
                case "C":
                    comProbability = 0.8;
                    break;
                case "D":
                    comProbability = 0.7;
                    break;
                case "E":
                    comProbability = 0.0;
                    break;
                case "O":
                    comProbability = 0.6;
                    break;
            }
            if (jobStability >=1) {jProbability = 1.0;
            } else {jProbability = 0.0;
            }
            int probability = (int) (csProbability + expProbability + laProbability + resProbability + fProbability + comProbability + sProbability + jProbability);
            System.out.println("Overall Probability: " + probability);
            if (probability >= 8.0) {bankProbability = 95;
            }else if (probability >= 7.0 && probability < 8.0 ) {bankProbability = 90;
            }else if (probability >= 6.0 && probability < 7.0) {bankProbability = 85;
            }else if (probability >= 5.0 && probability < 6.0 ) {bankProbability = 80;
            }else if (probability >= 4.0 && probability < 5.0) {bankProbability = 75;
            } else {bankProbability = 70;
            }
            System.out.println("Final Probability Percentage for HDFC Bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for ICICI bank
        if (bankName.equals("ICICI")) {
            switch (creditScore) {
                case "A+":
                    csProbability = 1.1;
                    break;
                case "A":
                    csProbability = 1.05;
                    break;
                case "B":
                    csProbability = 1.0;
                    break;
                case "C":
                    csProbability = 0.9;
                    break;
                case "NH":
                    csProbability = 1.0;
                    break;
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience >= 36) {
                    expProbability = 1.1;
                } else if (experience > 24 && experience < 36) {
                    expProbability = 1.0;
                } else if (experience == 24) {
                    expProbability = 0.9;
                }else {
                    expProbability = 0.0;
                }
            }
            if (loanAmount >=100000 && loanAmount <= 500000) {laProbability = 1.1;
            } else if (loanAmount > 500000 && loanAmount <= 1000000) {laProbability = 1.0;
            } else if (loanAmount > 1000000 && loanAmount <= 2000000) {laProbability = 0.9;
            } else if (loanAmount > 2000000 && loanAmount <= 3000000) {laProbability = 0.8;
            } else if (loanAmount > 3000000 && loanAmount <= 5000000) {laProbability = 0.7;
            } else if(loanAmount > 5000000 && loanAmount <= 7000000){laProbability = 0.6;
            } else {laProbability = 0.5;
            }
            switch (residentType) {
                case "Owned":
                    resProbability = 1.1;
                    break;
                case "Rented":
                    resProbability = 0.9;
                    break;
                case "Company Accommodation":
                    resProbability = 0.9;
                    break;
                case "PG":
                    resProbability = 0.5;
                    break;
                case "Staying with Friends":
                    resProbability = 0.5;
                    break;
            }
            if (foir <= 20) {fProbability = 1.2;
            } else if (foir > 20 && foir <= 30) {fProbability = 1.1;
            } else if (foir > 30 && foir <= 40) {fProbability = 1.0;
            } else if (foir > 40 && foir <= 50){fProbability = 0.9;
            } else if (foir > 50 && foir <= 70){fProbability = 0.7;
            }else {fProbability = 0.6;
            }
            if(takeHomeSalaryMonthly == 25000) {sProbability = 1.0;
            } else if (takeHomeSalaryMonthly >= (25000+(25000*0.4))) {sProbability = 1.3;
            } else if(takeHomeSalaryMonthly >= (25000+(25000*0.2)) && takeHomeSalaryMonthly < (25000+(25000*0.4))) {sProbability = 1.1;
            }
            switch (companyCategory) {
                case "SA":
                    comProbability = 1.05;
                    break;
                case "A":
                    comProbability = 1.0;
                    break;
                case "B":
                    comProbability = 0.9;
                    break;
            }
            if (jobStability >=1) {jProbability = 1.0;
            } else {jProbability = 0.0;
            }
            int probability = (int) (csProbability + expProbability + laProbability + resProbability + fProbability + comProbability + sProbability + jProbability);
            System.out.println("Overall Probability: " + probability);
            if (probability >= 8.0) {bankProbability = 95;
            }else if (probability >= 7.0 && probability < 8.0 ) {bankProbability = 90;
            }else if (probability >= 6.0 && probability < 7.0) {bankProbability = 85;
            }else if (probability >= 5.0 && probability < 6.0 ) {bankProbability = 80;
            }else if (probability >= 4.0 && probability < 5.0) {bankProbability = 75;
            } else {bankProbability = 70;
            }
            System.out.println("Final Probability Percentage for ICICI bank: " + bankProbability);
            return bankProbability;
        }
        //calculating Probability for Yes Bank
        if (bankName.equals("Yes Bank")) {
            switch (creditScore) {
                case "A+":
                    csProbability = 1.1;
                    break;
                case "A":
                    csProbability = 1.05;
                    break;
                case "B":
                    csProbability = 1.0;
                    break;
                case "NH":
                    csProbability = 1.0;
                    break;
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience >= 36) {
                    expProbability = 1.1;
                } else if (experience > 24 && experience < 36) {
                    expProbability = 1.0;
                } else if (experience == 24) {
                    expProbability = 0.9;
                }else {
                    expProbability = 0.0;
                }

            }
            if (loanAmount >= 50000 && loanAmount <= 500000) {laProbability = 1.1;
            }else if (loanAmount > 500000 && loanAmount <= 1000000) {laProbability = 1.0;
            } else if (loanAmount > 1000000 && loanAmount <= 2000000) {laProbability = 0.9;
            } else if (loanAmount > 2000000 && loanAmount <= 3000000) {laProbability = 0.8;
            } else if (loanAmount > 3000000 && loanAmount <= 5000000) {laProbability = 0.7;
            } else {laProbability = 0.6;
            }
            switch (residentType) {
                case "Owned":
                    resProbability = 1.1;
                    break;
                case "Rented":
                    resProbability = 0.9;
                    break;
                case "Company Accommodation":
                    resProbability = 0.9;
                    break;
                case " PG":
                    resProbability = 0.5;
                    break;
                case "Staying with Friends":
                    resProbability = 0.5;
                    break;
            }
            if (foir <= 20) {fProbability = 1.2;
            } else if (foir > 20 && foir <= 30) {fProbability = 1.1;
            } else if (foir > 30 && foir <= 40) {fProbability = 1.0;
            } else if(foir > 40 && foir <= 50){fProbability = 0.9;
            }else if(foir > 50 && foir <= 60){fProbability = 0.8;
            }else {fProbability = 0.7;
            }
            if(takeHomeSalaryMonthly == 20000) {sProbability = 1.0;
            } else if (takeHomeSalaryMonthly >= (20000+(20000*0.4))) {sProbability = 1.3;
            } else if(takeHomeSalaryMonthly >= (20000+(20000*0.2)) && takeHomeSalaryMonthly < (20000+(20000*0.4))) {sProbability = 1.1;
            }
            switch (companyCategory) {
                case "SA":
                    comProbability = 1.05;
                    break;
                case "A":
                    comProbability = 1.0;
                    break;
                case "B":
                    comProbability = 0.9;
                    break;
            }
            if (jobStability >=1) {jProbability = 1.0;
            } else {jProbability = 0.0;
            }
            int probability = (int) (csProbability + expProbability + laProbability + resProbability + fProbability + comProbability + sProbability + jProbability);
            System.out.println("Overall Probability: " + probability);
            if (probability >= 8.0) {bankProbability = 95;
            }else if (probability >= 7.0 && probability < 8.0 ) {bankProbability = 90;
            }else if (probability >= 6.0 && probability < 7.0) {bankProbability = 85;
            }else if (probability >= 5.0 && probability < 6.0 ) {bankProbability = 80;
            }else if (probability >= 4.0 && probability < 5.0) {bankProbability = 75;
            } else {bankProbability = 70;
            }
            System.out.println("Final Probability Percentage for Yes bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for Axis Finance bank
        if (bankName.equals("Axis Finance")) {
            switch (creditScore) {
                case "A+":
                    csProbability = 1.1;
                    break;
                case "A":
                    csProbability = 1.05;
                    break;
                case "B":
                    csProbability = 1.0;
                    break;
                case "C":
                    csProbability = 0.9;
                    break;
                case "D":
                    csProbability = 0.8;
                    break;
                case "NH":
                    csProbability = 1.0;
                    break;
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience >= 36) {
                    expProbability = 1.0;
                } else {
                    expProbability = 0.0;
                }
            }
            if (loanAmount >= 200000 && loanAmount <= 500000) {laProbability = 1.1;
            } else if(loanAmount > 500000 && loanAmount <= 1000000) {laProbability = 1.0;
            } else if(loanAmount > 1000000 && loanAmount <= 2000000) {laProbability = 0.9;
            } else if(loanAmount > 2000000 && loanAmount <= 3000000) {laProbability = 0.8;
            }else if(loanAmount > 3000000 && loanAmount <= 4000000) {laProbability = 0.7;
            }else if(loanAmount > 4000000 && loanAmount <= 5000000) {laProbability = 0.6;
            }else {laProbability = 0.5;
            }
            switch (residentType) {
                case "Owned":
                    resProbability = 1.1;
                    break;
                case "Rented":
                    resProbability = 0.9;
                    break;
                case "Staying with Friends":
                    if(companyCategory.equalsIgnoreCase("SA") || companyCategory.equalsIgnoreCase("A")) {resProbability = 0.5;
                    }else {resProbability = 0.0;
                    }
                    break;
            }
            if (foir <= 20) {fProbability = 1.2;
            } else if (foir > 20 && foir <= 30) {fProbability = 1.1;
            } else if (foir > 30 && foir <= 40) {fProbability = 1.0;
            } else if(foir > 40 && foir <= 50) {fProbability = 0.9;
            }else if(foir > 50 && foir <= 60) {fProbability = 0.8;
            } else {fProbability = 0.7;
            }
            if(takeHomeSalaryMonthly == 30000) {sProbability = 1.0;
            } else if (takeHomeSalaryMonthly >= (30000+(30000*0.4))) {sProbability = 1.3;
            } else if(takeHomeSalaryMonthly >= (30000+(30000*0.2)) && takeHomeSalaryMonthly < (30000+(30000*0.4))) {sProbability = 1.1;
            }
            switch (companyCategory) {
                case "SA":
                    comProbability = 1.05;
                    break;
                case "A":
                    comProbability = 1.0;
                    break;
                case "B":
                    comProbability = 0.9;
                    break;
                case "C":
                    comProbability = 0.8;
                    break;
            }
            if (jobStability >=12) {jProbability = 1.0;
            } else {jProbability = 0.0;
            }
            int probability = (int) (csProbability + expProbability + laProbability + resProbability + fProbability + comProbability + sProbability + jProbability);
            System.out.println("Overall Probability: " + probability);
            if (probability >= 8.0) {bankProbability = 95;
            }else if (probability >= 7.0 && probability < 8.0 ) {bankProbability = 90;
            }else if (probability >= 6.0 && probability < 7.0) {bankProbability = 85;
            }else if (probability >= 5.0 && probability < 6.0 ) {bankProbability = 80;
            }else if (probability >= 4.0 && probability < 5.0) {bankProbability = 75;
            } else {bankProbability = 70;
            }
            System.out.println("Final Probability Percentage for Axis Finance Bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for Bajaj Finserv bank
        if (bankName.equals("Bajaj Finserv")) {
            switch (creditScore) {
                case "A+":
                    csProbability = 1.1;
                    break;
                case "A":
                    csProbability = 1.05;
                    break;
                case "B":
                    csProbability = 1.0;
                    break;
                case "C":
                    csProbability = 0.9;
                    break;
                case "D":
                    csProbability = 0.8;
                    break;
                case "NH":
                    csProbability = 1.0;
                    break;
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience >= 36) {
                    expProbability = 1.1;
                }else if(experience == 36) {
                    expProbability = 1.0;
                } else {
                    expProbability = 0.0;
                }

            }
            if (loanAmount >=100000 && loanAmount <= 500000) {laProbability = 1.1;
            } else if (loanAmount > 500000 && loanAmount <= 1000000) {laProbability = 1.0;
            } else if (loanAmount > 1000000 && loanAmount <= 2000000) {laProbability = 0.9;
            } else if (loanAmount > 2000000 && loanAmount <= 3000000) {laProbability = 0.8;
            } else if (loanAmount > 3000000 && loanAmount <= 3500000) {laProbability = 0.7;
            } else {laProbability = 0.0;
            }
            switch (residentType) {
                case "Owned":
                    resProbability = 1.1;
                    break;
                case "Rented":
                    resProbability = 0.9;
                    break;
            }
            if (foir <= 20) {fProbability = 1.2;
            } else if (foir > 20 && foir <= 30) {fProbability = 1.1;
            } else if (foir > 30 && foir <= 40) {fProbability = 1.0;
            } else if (foir > 40 && foir <= 50){fProbability = 0.9;
            } else if (foir > 50 && foir <= 70){fProbability = 0.7;
            }
            if(takeHomeSalaryMonthly == 27000) {sProbability = 1.0;
            } else if (takeHomeSalaryMonthly >= (27000+(27000*0.4))) {sProbability = 1.3;
            } else if(takeHomeSalaryMonthly >= (27000+(27000*0.2)) && takeHomeSalaryMonthly < (27000+(27000*0.4))) {sProbability = 1.1;
            }
            switch (companyCategory) {
                case "SA":
                    comProbability = 1.05;
                    break;
                case "A":
                    comProbability = 1.0;
                    break;
                case "B":
                    comProbability = 0.9;
                    break;
                case "C":
                    comProbability = 0.8;
                    break;
                case "D":
                    comProbability = 0.7;
                    break;
                case "E":
                    comProbability = 0.6;
                    break;
                case "O":
                    comProbability = 0.5;
            }
            if (jobStability >=1) {jProbability = 1.0;
            } else {jProbability = 0.0;
            }
            int probability = (int) (csProbability + expProbability + laProbability + resProbability + fProbability + comProbability + sProbability + jProbability);
            System.out.println("Overall Probability: " + probability);
            if (probability >= 8.0) {bankProbability = 95;
            }else if (probability >= 7.0 && probability < 8.0 ) {bankProbability = 90;
            }else if (probability >= 6.0 && probability < 7.0) {bankProbability = 85;
            }else if (probability >= 5.0 && probability < 6.0 ) {bankProbability = 80;
            }else if (probability >= 4.0 && probability < 5.0) {bankProbability = 75;
            } else {bankProbability = 70;
            }
            System.out.println("Final Probability Percentage for Bajaj Finserv bank: " + bankProbability);
            return bankProbability;
        }
        //validating probability for Poonawala Bank
        if (bankName.equals("Poonawala")) {
            switch (creditScore) {
                case "A+":
                    csProbability = 1.1;
                    break;
                case "A":
                    csProbability = 1.05;
                    break;
                case "B":
                    csProbability = 1.0;
                    break;
                case "C":
                    csProbability = 0.9;
                    break;
                case "NH":
                    csProbability = 1.0;
                    break;
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience >= 36) {
                    expProbability = 1.1;
                } else if (experience > 12 || experience < 36) {
                    expProbability = 1.0;
                } else if (experience == 12) {
                    expProbability = 0.9;
                }else {
                    expProbability = 0.0;
                }
            }
            if (loanAmount >= 100000 && loanAmount <= 500000) {laProbability = 1.1;
            } else if (loanAmount > 500000 && loanAmount <= 1000000) {laProbability = 1.0;
            } else if (loanAmount > 1000000 && loanAmount <= 2000000) {laProbability = 0.9;
            } else if (loanAmount > 2000000 && loanAmount <= 3000000) {laProbability = 0.8;
            } else {laProbability = 0.0;
            }
            switch (residentType) {
                case "Owned":
                    resProbability = 1.1;
                    break;
                case "Rented":
                    resProbability = 0.9;
                    break;
                case " PG":
                    resProbability = 0.5;
                    break;
                case "Staying with Friends":
                    resProbability = 0.5;
                    break;
            }
            if (foir <= 20) {fProbability = 1.2;
            } else if (foir > 20 && foir <= 30) {fProbability = 1.1;
            } else if (foir > 30 && foir <= 40) {fProbability = 1.0;
            } else if(foir > 40 && foir <= 50) {fProbability = 0.9;
            }else if(foir > 50 && foir <= 60) {fProbability = 0.8;
            }else if(foir > 60 && foir <= 75) {fProbability = 0.7;
            }else {fProbability = 0.0;
            }
            if(takeHomeSalaryMonthly == 30000) {sProbability = 1.0;
            } else if (takeHomeSalaryMonthly >= (30000+(30000*0.4))) {sProbability = 1.3;
            } else if(takeHomeSalaryMonthly >= (30000+(30000*0.2)) && takeHomeSalaryMonthly < (30000+(30000*0.4))) {sProbability = 1.1;
            }
            switch (companyCategory) {
                case "SA":
                    comProbability = 1.05;
                    break;
                case "A":
                    comProbability = 1.0;
                    break;
                case "B":
                    comProbability = 0.9;
                    break;
                case "C":
                    comProbability = 0.8;
                    break;
            }
            if (jobStability >=1) {jProbability = 1.0;
            } else {jProbability = 0.0;
            }
            int probability = (int) (csProbability + expProbability + laProbability + resProbability + fProbability + comProbability + sProbability + jProbability);
            System.out.println("Overall Probability: " + probability);
            if (probability >= 8.0) {bankProbability = 95;
            }else if (probability >= 7.0 && probability < 8.0 ) {bankProbability = 90;
            }else if (probability >= 6.0 && probability < 7.0) {bankProbability = 85;
            }else if (probability >= 5.0 && probability < 6.0 ) {bankProbability = 80;
            }else if (probability >= 4.0 && probability < 5.0) {bankProbability = 75;
            } else {bankProbability = 70;
            }
            System.out.println("Final Probability Percentage for Poonawala bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for Fullerton bank
        if (bankName.equals("Fullerton")) {
            switch (creditScore) {
                case "A+":
                    csProbability = 1.1;
                    break;
                case "A":
                    csProbability = 1.0;
                    break;
                case "B":
                    csProbability = 0.9;
                    break;
                case "C":
                    csProbability = 0.8;
                    break;
                case "D":
                    csProbability = 0.7;
                    break;
                case "NH":
                    csProbability = 1.0;
                    break;
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience >= 36) {
                    expProbability = 1.1;
                } else if (experience > 24 || experience < 36) {
                    expProbability = 1.0;
                } else if (experience == 24) {
                    expProbability = 0.9;
                }else {
                    expProbability = 0.0;
                }

            }
            if (loanAmount >= 100000 && loanAmount <= 500000) {laProbability = 1.0;
            } else if (loanAmount > 500000 && loanAmount <= 1000000) {laProbability = 0.9;
            } else if (loanAmount > 1000000 && loanAmount <= 2000000) {laProbability = 0.8;
            } else if (loanAmount > 2000000 && loanAmount <= 2500000) {laProbability = 0.7;
            } else {laProbability = 0.0;
            }
            switch (residentType) {
                case "Owned":
                    resProbability = 1.1;
                    break;
                case "Rented":
                    resProbability = 0.9;
                    break;
            }
            if (foir <= 20) {fProbability = 1.2;
            } else if (foir > 20 && foir <= 30) {fProbability = 1.1;
            } else if (foir > 30 && foir <= 40) {fProbability = 1.0;
            } else if(foir > 40 && foir <= 50){fProbability = 0.9;
            } else if(foir > 50 && foir <=60){fProbability = 0.8;
            } else if(foir > 60 && foir <= 70) {fProbability = 0.7;
            }else {fProbability = 0.0;
            }
            if(takeHomeSalaryMonthly == 20000) {sProbability = 1.0;
            } else if (takeHomeSalaryMonthly >= (20000+(20000*0.4))) {sProbability = 1.3;
            } else if(takeHomeSalaryMonthly >= (20000+(25000*0.2)) && takeHomeSalaryMonthly < (20000+(20000*0.4))) {sProbability = 1.1;
            }
            switch (companyCategory) {
                case "SA":
                    comProbability = 1.05;
                    break;
                case "A":
                    comProbability = 1.0;
                    break;
                case "B":
                    comProbability = 0.9;
                    break;
                case "C":
                    comProbability = 0.8;
                    break;
                case "D":
                    comProbability = 0.7;
                    break;
                case "E":
                    comProbability = 0.0;
                    break;
                case "O":
                    comProbability = 0.6;
                    break;
            }
            if (jobStability >=1) {jProbability = 1.0;
            } else {jProbability = 0.0;
            }
            int probability = (int) (csProbability + expProbability + laProbability + resProbability + fProbability + comProbability + sProbability + jProbability);
            System.out.println("Overall Probability: " + probability);
            if (probability >= 8.0) {bankProbability = 95;
            }else if (probability >= 7.0 && probability < 8.0 ) {bankProbability = 90;
            }else if (probability >= 6.0 && probability < 7.0) {bankProbability = 85;
            }else if (probability >= 5.0 && probability < 6.0 ) {bankProbability = 80;
            }else if (probability >= 4.0 && probability < 5.0) {bankProbability = 75;
            } else {bankProbability = 70;
            }
            System.out.println("Final Probability Percentage for Fullerton Bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for Cholamandalam bank
        if (bankName.equals("Cholamandalam")) {
            switch (creditScore) {
                case "A+":
                    csProbability = 1.1;
                    break;
                case "A":
                    csProbability = 1.05;
                    break;
                case "B":
                    csProbability = 1.0;
                    break;
                case "C":
                    csProbability = 0.9;
                    break;
                case "NH":
                    csProbability = 1.0;
                    break;
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience >= 36) {expProbability = 1.1;
                } else if (experience > 12 || experience < 36) {expProbability = 1.0;
                } else if (experience == 12) {expProbability = 0.9;
                }else {expProbability = 0.0;
                }
            }
            if (loanAmount >=100000 && loanAmount <= 500000) {laProbability = 1.0;
            } else if (loanAmount > 500000 && loanAmount <= 1000000) {laProbability = 0.9;
            } else if (loanAmount > 1000000 && loanAmount <= 2000000) {laProbability = 0.8;
            } else {laProbability = 0.0;
            }
            switch (residentType) {
                case "Owned":
                    resProbability = 1.1;
                    break;
                case "Rented":
                    resProbability = 0.9;
                    break;
                case "Company Accommodation":
                    resProbability = 0.9;
                    break;
                case "Staying with Friends":
                    resProbability = 0.5;
                    break;
            }
            if (foir <= 20) {fProbability = 1.2;
            } else if (foir > 20 && foir <= 30) {fProbability = 1.1;
            } else if (foir > 30 && foir <= 40) {fProbability = 1.0;
            } else if (foir > 40 && foir <= 50){fProbability = 0.9;
            } else if (foir > 50 && foir <= 70){fProbability = 0.7;
            }else {fProbability = 0.0;
            }
            if(takeHomeSalaryMonthly == 25000) {sProbability = 1.0;
            } else if (takeHomeSalaryMonthly >= (25000+(25000*0.4))) {sProbability = 1.3;
            } else if(takeHomeSalaryMonthly >= (25000+(25000*0.2)) && takeHomeSalaryMonthly < (25000+(25000*0.4))) {sProbability = 1.1;
            }
            switch (companyCategory) {
                case "SA":
                    comProbability = 1.05;
                    break;
                case "A":
                    comProbability = 1.0;
                    break;
                case "B":
                    comProbability = 0.9;
                    break;
                case "C":
                    comProbability = 0.8;
                    break;
                case "D":
                    comProbability = 0.7;
                    break;
                case "E":
                    comProbability = 0.6;
                    break;
                case "O":
                    comProbability = 0.5;
                    break;
            }
            if (jobStability >=1) {jProbability = 1.0;
            } else {jProbability = 0.0;
            }
            int probability = (int) (csProbability + expProbability + laProbability + resProbability + fProbability + comProbability + sProbability + jProbability);
            System.out.println("Overall Probability: " + probability);
            if (probability >= 8.0) {bankProbability = 95;
            }else if (probability >= 7.0 && probability < 8.0 ) {bankProbability = 90;
            }else if (probability >= 6.0 && probability < 7.0) {bankProbability = 85;
            }else if (probability >= 5.0 && probability < 6.0 ) {bankProbability = 80;
            }else if (probability >= 4.0 && probability < 5.0) {bankProbability = 75;
            } else {bankProbability = 70;
            }
            System.out.println("Final Probability Percentage for Cholamandalam bank: " + bankProbability);
            return bankProbability;
        }
        //calculating Probability for TATA Capital bank
        if (bankName.equals("TATA Capital")) {
            switch (creditScore) {
                case "A+":
                    csProbability = 1.1;
                    break;
                case "A":
                    csProbability = 1.05;
                    break;
                case "B":
                    csProbability = 1.0;
                    break;
                case "C":
                    csProbability = 0.9;
                    break;
                case "D":
                    csProbability = 0.8;
                    break;
                case "NH":
                    csProbability = 1.0;
                    break;
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience >= 36) {
                    expProbability = 1.1;
                } else if (experience == 36) {
                    expProbability = 1.0;
                }else {
                    expProbability = 0.0;
                }
            }
            if (loanAmount >= 100000 && loanAmount <= 500000) {laProbability = 1.1;
            }else if (loanAmount > 500000 && loanAmount <= 1000000) {laProbability = 1.0;
            } else if (loanAmount > 1000000 && loanAmount <= 2000000) {laProbability = 0.9;
            } else if (loanAmount > 2000000 && loanAmount <= 3000000) {laProbability = 0.8;
            } else if (loanAmount > 3000000 && loanAmount <= 3500000) {laProbability = 0.7;
            } else {laProbability = 0.0;
            }
            switch (residentType) {
                case "Owned":
                    resProbability = 1.1;
                    break;
                case "Rented":
                    resProbability = 0.9;
                    break;
                case "Company Accommodation":
                    resProbability = 0.9;
                    break;
                case " PG":
                    resProbability = 0.5;
                    break;
                case "Staying with Friends":
                    resProbability = 0.5;
                    break;
            }
            if (foir <= 20) {fProbability = 1.2;
            } else if (foir > 20 && foir <= 30) {fProbability = 1.1;
            } else if (foir > 30 && foir <= 40) {fProbability = 1.0;
            } else if(foir > 40 && foir <= 50){fProbability = 0.9;
            }else if(foir > 50 && foir <= 60){fProbability = 0.8;
            }else if(foir > 60 && foir <= 70){fProbability = 0.7;
            } else {fProbability = 0.0;
            }
            if(takeHomeSalaryMonthly == 20000) {sProbability = 1.0;
            } else if (takeHomeSalaryMonthly >= (20000+(20000*0.4))) {sProbability = 1.3;
            } else if(takeHomeSalaryMonthly >= (20000+(20000*0.2)) && takeHomeSalaryMonthly < (20000+(20000*0.4))) {sProbability = 1.1;
            }
            switch (companyCategory) {
                case "SA":
                    comProbability = 1.05;
                    break;
                case "A":
                    comProbability = 1.0;
                    break;
                case "B":
                    comProbability = 0.9;
                    break;
            }
            if (jobStability >=1) {jProbability = 1.0;
            } else {jProbability = 0.0;
            }
            int probability = (int) (csProbability + expProbability + laProbability + resProbability + fProbability + comProbability + sProbability + jProbability);
            System.out.println("Overall Probability: " + probability);
            if (probability >= 8.0) {bankProbability = 95;
            }else if (probability >= 7.0 && probability < 8.0 ) {bankProbability = 90;
            }else if (probability >= 6.0 && probability < 7.0) {bankProbability = 85;
            }else if (probability >= 5.0 && probability < 6.0 ) {bankProbability = 80;
            }else if (probability >= 4.0 && probability < 5.0) {bankProbability = 75;
            } else {bankProbability = 70;
            }
            System.out.println("Final Probability Percentage for Yes bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for Finnable bank
        if (bankName.equals("Finnable")) {
            switch (creditScore) {
                case "A+":
                    csProbability = 1.1;
                    break;
                case "A":
                    csProbability = 1.05;
                    break;
                case "B":
                    csProbability = 1.0;
                    break;
                case "C":
                    csProbability = 0.9;
                    break;
                case "D":
                    csProbability = 0.8;
                    break;
                case "NH":
                    csProbability = 1.0;
                    break;
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience >= 36) { expProbability = 1.2;
                } else if(experience >=24 && experience > 12) {expProbability = 1.1;
                }else if(experience == 12) {expProbability = 1.0;
                }else {expProbability = 0.0;}
            }
            if (loanAmount >= 200000 && loanAmount <= 500000) {laProbability = 1.0;
            } else if(loanAmount > 500000 && loanAmount <= 1000000) {laProbability = 0.8;
            }else {laProbability = 0.5;}
            switch (residentType) {
                case "Owned":
                    resProbability = 1.1;
                    break;
                case "Rented":
                    resProbability = 0.9;
                    break;
                case "Company Accommodation":
                    resProbability = 0.9;
                    break;
                case "PG":
                    resProbability = 0.5;
                    break;
                case "Staying with Friends":
                    resProbability = 0.5;
                    break;
            }
            if (foir <= 20) {fProbability = 1.2;
            } else if (foir > 20 && foir <= 30) {fProbability = 1.1;
            } else if (foir > 30 && foir <= 40) {fProbability = 1.0;
            } else if(foir > 40 && foir <= 50) {fProbability = 0.9;
            }else if(foir > 50 && foir <= 60) {fProbability = 0.8;
            } else if (foir > 60 && foir <=75) {fProbability = 0.7;
            } else {fProbability = 0.6;}
            if(takeHomeSalaryMonthly == 20000) {sProbability = 1.0;
            } else if (takeHomeSalaryMonthly >= (20000+(20000*0.4))) {sProbability = 1.3;
            } else if(takeHomeSalaryMonthly >= (20000+(20000*0.2)) && takeHomeSalaryMonthly < (20000+(20000*0.4))) {sProbability = 1.1;}
            switch (companyCategory) {
                case "SA":
                    comProbability = 1.05;
                    break;
                case "A":
                    comProbability = 1.0;
                    break;
                case "B":
                    comProbability = 0.9;
                    break;
                case "C":
                    comProbability = 0.8;
                    break;
                case "D":
                    comProbability = 0.7;
                    break;
                case "E":
                    comProbability = 0.6;
                    break;
                case "O":
                    comProbability = 0.5;
                    break;
            }
            if (jobStability >=12) {jProbability = 1.0;
            } else {jProbability = 0.0;}
            int  probability = (int) (csProbability + expProbability + laProbability + resProbability + fProbability + comProbability + sProbability + jProbability);
            System.out.println("Overall Probability: " + probability);
            if (probability >= 8.0) {bankProbability = 95;
            }else if (probability >= 7.0 && probability < 8.0 ) {bankProbability = 90;
            }else if (probability >= 6.0 && probability < 7.0) {bankProbability = 85;
            }else if (probability >= 5.0 && probability < 6.0 ) {bankProbability = 80;
            }else if (probability >= 4.0 && probability < 5.0) {bankProbability = 75;
            } else {bankProbability = 70;}
            System.out.println("Final Probability Percentage for Finnable Bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for Incred bank
        if (bankName.equals("Incred")) {
            switch (creditScore) {
                case "A+":
                    csProbability = 1.1;
                    break;
                case "A":
                    csProbability = 1.05;
                    break;
                case "B":
                    csProbability = 1.0;
                    break;
                case "C":
                    csProbability = 0.9;
                    break;
                case "D":
                    csProbability = 0.8;
                    break;
                case "NH":
                    csProbability = 1.0;
                    break;
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience >= 12) { expProbability = 1.1;
                }else if(experience == 12) {expProbability = 1.0;
                }else {expProbability = 0.0;}

            }
            if (loanAmount >=100000 && loanAmount <= 500000) {laProbability = 1.0;
            } else if (loanAmount > 500000 && loanAmount <= 1000000) {laProbability = 0.9;
            } else{laProbability = 0.0;}
            switch (residentType) {
                case "Owned":
                    resProbability = 1.1;
                    break;
                case "Rented":
                    resProbability = 0.9;
                    break;
                case "PG":
                    resProbability = 0.5;
                    break;
                case "Company Accomodation":
                    resProbability = 0.9;
                    break;
                case "Staying with friends":
                    resProbability = 0.5;
                    break;
            }
            if (foir <= 20) {fProbability = 1.2;
            } else if (foir > 20 && foir <= 30) {fProbability = 1.1;
            } else if (foir > 30 && foir <= 40) {fProbability = 1.0;
            } else if (foir > 40 && foir <= 50){fProbability = 0.9;
            } else if (foir > 50 && foir <= 75){fProbability = 0.7;
            }else {fProbability = 0.0;}
            if(takeHomeSalaryMonthly == 15000) {sProbability = 1.0;
            } else if (takeHomeSalaryMonthly >= (15000+(15000*0.4))) {sProbability = 1.3;
            } else if(takeHomeSalaryMonthly >= (15000+(15000*0.2)) && takeHomeSalaryMonthly < (15000+(15000*0.4))) {sProbability = 1.1;}
            switch (companyCategory) {
                case "SA":
                    comProbability = 1.05;
                    break;
                case "A":
                    comProbability = 1.0;
                    break;
                case "B":
                    comProbability = 0.9;
                    break;
                case "C":
                    comProbability = 0.8;
                    break;
                case "D":
                    comProbability = 0.7;
                    break;
                case "E":
                    comProbability = 0.6;
                    break;
                case "O":
                    comProbability = 0.5;
            }
            if (jobStability >=3) {jProbability = 1.0;
            } else {jProbability = 0.0;}
            int probability = (int) (csProbability + expProbability + laProbability + resProbability + fProbability + comProbability + sProbability + jProbability);
            System.out.println("Overall Probability: " + probability);
            if (probability >= 8.0) {bankProbability = 95;
            }else if (probability >= 7.0 && probability < 8.0 ) {bankProbability = 90;
            }else if (probability >= 6.0 && probability < 7.0) {bankProbability = 85;
            }else if (probability >= 5.0 && probability < 6.0 ) {bankProbability = 80;
            }else if (probability >= 4.0 && probability < 5.0) {bankProbability = 75;
            } else {bankProbability = 70;}
            System.out.println("Final Probability Percentage for Incred bank: " + bankProbability);
            return bankProbability;
        }
        //validating probability for Paysense Bank
        if (bankName.equals("Paysense")) {
            switch (creditScore) {
                case "A+":
                    csProbability = 1.1;
                    break;
                case "A":
                    csProbability = 1.05;
                    break;
                case "B":
                    csProbability = 1.0;
                    break;
                case "C":
                    csProbability = 0.9;
                    break;
                case "D":
                    csProbability = 0.8;
                    break;
                case "NH":
                    csProbability = 1.0;
                    break;
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience >= 36) {expProbability = 1.1;
                } else if (experience >= 4 || experience < 36) { expProbability = 1.0;
                } else if (experience == 4) {expProbability = 0.9;
                }else {expProbability = 0.0;}

            }
            if (loanAmount >= 5000 && loanAmount <= 100000) {laProbability = 1.1;
            } else if (loanAmount > 100000 && loanAmount <= 300000) {laProbability = 1.0;
            } else if (loanAmount > 300000 && loanAmount <= 500000) {laProbability = 0.9;
            } else if (loanAmount > 500000 && loanAmount <= 750000) {laProbability = 0.8;
            } else {laProbability = 0.0;}
            switch (residentType) {
                case "Owned":
                    resProbability = 1.1;
                    break;
                case "Rented":
                    resProbability = 0.9;
                    break;
                case "Staying with Friends":
                    resProbability = 0.5;
                    break;
            }
            if (foir <= 20) {fProbability = 1.2;
            }else if (foir > 20 && foir <= 30) {fProbability = 1.1;
            }else if (foir > 30 && foir <= 40) {fProbability = 1.0;
            }else if(foir > 40 && foir <= 50) {fProbability = 0.9;
            }else if(foir > 50 && foir <= 60) {fProbability = 0.8;
            }else if(foir > 60 && foir <= 75) {fProbability = 0.7;
            }else {fProbability = 0.0;}
            if(takeHomeSalaryMonthly == 20000) {sProbability = 1.0;
            } else if (takeHomeSalaryMonthly >= (20000+(20000*0.4))) {sProbability = 1.3;
            } else if(takeHomeSalaryMonthly >= (20000+(20000*0.2)) && takeHomeSalaryMonthly < (20000+(20000*0.4))) {sProbability = 1.1;}
            switch (companyCategory) {
                case "SA":
                    comProbability = 1.05;
                    break;
                case "A":
                    comProbability = 1.0;
                    break;
                case "B":
                    comProbability = 0.9;
                    break;
                case "C":
                    comProbability = 0.8;
                    break;
                case "D":
                    comProbability = 0.7;
                    break;
                case "E":
                    comProbability = 0.6;
                    break;
                case "O":
                    comProbability = 0.5;
                    break;
            }
            if (jobStability >=3) {jProbability = 1.0;
            } else {jProbability = 0.0;}
            int probability = (int) (csProbability + expProbability + laProbability + resProbability + fProbability + comProbability + sProbability + jProbability);
            System.out.println("Overall Probability: " + probability);
            if (probability >= 8.0) {bankProbability = 95;
            }else if (probability >= 7.0 && probability < 8.0 ) {bankProbability = 90;
            }else if (probability >= 6.0 && probability < 7.0) {bankProbability = 85;
            }else if (probability >= 5.0 && probability < 6.0 ) {bankProbability = 80;
            }else if (probability >= 4.0 && probability < 5.0) {bankProbability = 75;
            } else {bankProbability = 70;}
            System.out.println("Final Probability Percentage for Paysense bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for IndusInd bank
        if (bankName.equals("IndusInd")) {
            switch (creditScore) {
                case "A+":
                    csProbability = 1.1;
                    break;
                case "A":
                    csProbability = 1.0;
                    break;
                case "B":
                    csProbability = 0.9;
                    break;
                case "NH":
                    csProbability = 1.0;
                    break;
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience >= 36) {expProbability = 1.1;
                } else if (experience > 24 || experience < 36) {expProbability = 1.0;
                } else if (experience == 24) {expProbability = 0.9;
                }else {expProbability = 0.0;}

            }
            if (loanAmount >= 100000 && loanAmount <= 500000) {laProbability = 1.0;
            } else if (loanAmount > 500000 && loanAmount <= 1000000) {laProbability = 0.9;
            } else if (loanAmount > 1000000 && loanAmount <= 2000000) {laProbability = 0.8;
            } else if (loanAmount > 2000000 && loanAmount <= 3000000) {laProbability = 0.7;
            } else if(loanAmount > 3000000 && loanAmount <= 4000000){laProbability = 0.6;
            } else if (loanAmount > 4000000 && loanAmount <= 5000000) {laProbability = 0.5;
            } else {laProbability = 0.0;}
            switch (residentType) {
                case "Owned":
                    resProbability = 1.1;
                    break;
                case "Rented":
                    resProbability = 0.9;
                    break;
            }
            if (foir <= 20) {fProbability = 1.2;
            } else if (foir > 20 && foir <= 30) {fProbability = 1.1;
            } else if (foir > 30 && foir <= 40) {fProbability = 1.0;
            } else if(foir > 40 && foir <= 50){fProbability = 0.9;
            } else if(foir > 50 && foir <=60){fProbability = 0.8;
            } else if(foir > 60 && foir <= 70) {fProbability = 0.7;
            }else {fProbability = 0.0;}
            if(takeHomeSalaryMonthly == 25000) {sProbability = 1.0;
            } else if (takeHomeSalaryMonthly >= (25000+(25000*0.4))) {sProbability = 1.3;
            } else if(takeHomeSalaryMonthly >= (25000+(25000*0.2)) && takeHomeSalaryMonthly < (25000+(25000*0.4))) {sProbability = 1.1;}
            switch (companyCategory) {
                case "SA":
                    comProbability = 1.05;
                    break;
                case "A":
                    comProbability = 1.0;
                    break;
                case "B":
                    comProbability = 0.9;
                    break;
                case "C":
                    comProbability = 0.8;
                    break;
                case "D":
                    comProbability = 0.7;
                    break;
                case "E":
                    comProbability = 0.0;
                    break;
                case "O":
                    comProbability = 0.6;
                    break;
            }
            if (jobStability >=1) {jProbability = 1.0;
            } else {jProbability = 0.0;}
            int probability = (int) (csProbability + expProbability + laProbability + resProbability + fProbability + comProbability + sProbability + jProbability);
            System.out.println("Overall Probability: " + probability);
            if (probability >= 8.0) {bankProbability = 95;
            }else if (probability >= 7.0 && probability < 8.0 ) {bankProbability = 90;
            }else if (probability >= 6.0 && probability < 7.0) {bankProbability = 85;
            }else if (probability >= 5.0 && probability < 6.0 ) {bankProbability = 80;
            }else if (probability >= 4.0 && probability < 5.0) {bankProbability = 75;
            } else {bankProbability = 70;}
            System.out.println("Final Probability Percentage for IndusInd Bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for SCB bank
        if (bankName.equals("SCB")) {
            switch (creditScore) {
                case "A+":
                    csProbability = 1.1;
                    break;
                case "A":
                    csProbability = 1.05;
                    break;
                case "B":
                    csProbability = 1.0;
                    break;
                case "C":
                    csProbability = 0.9;
                    break;
                case "NH":
                    csProbability = 1.0;
                    break;
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience >= 36) { expProbability = 1.1;
                } else if (experience > 24 || experience < 36) {expProbability = 1.0;
                } else if (experience == 24) {expProbability = 0.9;
                }else {expProbability = 0.0;}

            }
            if (loanAmount >=100000 && loanAmount <= 500000) {laProbability = 1.0;
            } else if (loanAmount > 500000 && loanAmount <= 1000000) {laProbability = 0.9;
            } else if (loanAmount > 1000000 && loanAmount <= 2000000) {laProbability = 0.8;
            } else if(loanAmount > 2000000 && loanAmount <= 3000000){laProbability = 0.7;
            } else if(loanAmount > 3000000 && loanAmount <= 4000000) {laProbability = 0.6;
            } else if(loanAmount > 4000000 && loanAmount <= 5000000) {laProbability = 0.5;
            } else {laProbability = 0.0;}
            switch (residentType) {
                case "Owned":
                    resProbability = 1.1;
                    break;
                case "Rented":
                    resProbability = 0.9;
                    break;
            }
            if (foir <= 20) {fProbability = 1.2;
            } else if (foir > 20 && foir <= 30) {fProbability = 1.1;
            } else if (foir > 30 && foir <= 40) {fProbability = 1.0;
            } else if (foir > 40 && foir <= 50){fProbability = 0.9;
            } else if (foir > 50 && foir <= 55){fProbability = 0.7;
            }else {fProbability = 0.0;}
            if(takeHomeSalaryMonthly == 30000) {sProbability = 1.0;
            } else if (takeHomeSalaryMonthly >= (30000+(30000*0.4))) {sProbability = 1.3;
            } else if(takeHomeSalaryMonthly >= (30000+(30000*0.2)) && takeHomeSalaryMonthly < (30000+(30000*0.4))) {sProbability = 1.1;}
            switch (companyCategory) {
                case "SA":
                    comProbability = 1.05;
                    break;
                case "A":
                    comProbability = 1.0;
                    break;
                case "B":
                    comProbability = 0.9;
                    break;
                case "C":
                    comProbability = 0.8;
                    break;
                case "D":
                    comProbability = 0.7;
                    break;
                case "E":
                    comProbability = 0.6;
                    break;
                case "O":
                    comProbability = 0.5;
                    break;
            }
            if (jobStability >=1) {jProbability = 1.0;
            } else {jProbability = 0.0;}
            int probability = (int) (csProbability + expProbability + laProbability + resProbability + fProbability + comProbability + sProbability + jProbability);
            System.out.println("Overall Probability: " + probability);
            if (probability >= 8.0) {bankProbability = 95;
            }else if (probability >= 7.0 && probability < 8.0 ) {bankProbability = 90;
            }else if (probability >= 6.0 && probability < 7.0) {bankProbability = 85;
            }else if (probability >= 5.0 && probability < 6.0 ) {bankProbability = 80;
            }else if (probability >= 4.0 && probability < 5.0) {bankProbability = 75;
            } else {bankProbability = 70;}
            System.out.println("Final Probability Percentage for SCB bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for Kotak bank
        if (bankName.equals("Kotak")) {
            switch (creditScore) {
                case "A+":
                    csProbability = 1.1;
                    break;
                case "A":
                    csProbability = 1.05;
                    break;
                case "B":
                    csProbability = 1.0;
                    break;
                case "C":
                    csProbability = 0.9;
                    break;
                case "D":
                    csProbability = 0.8;
                    break;
                case "NH":
                    csProbability = 1.0;
                    break;
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience >= 36) {expProbability = 1.1;
                } else if (experience == 36) {expProbability = 0.9;
                }else {expProbability = 0.0;}

            }
            if (loanAmount >=100000 && loanAmount <= 500000) {laProbability = 1.0;
            } else if (loanAmount > 500000 && loanAmount <= 1000000) {laProbability = 0.9;
            } else if (loanAmount > 1000000 && loanAmount <= 2000000) {laProbability = 0.8;
            } else if(loanAmount > 2000000 && loanAmount <= 3000000){laProbability = 0.7;
            } else if(loanAmount > 3000000 && loanAmount <= 4000000) {laProbability = 0.6;
            } else if(loanAmount > 4000000 && loanAmount <= 5000000) {laProbability = 0.5;
            } else {laProbability = 0.0;}
            switch (residentType) {
                case "Owned":
                    resProbability = 1.1;
                    break;
                case "Rented":
                    resProbability = 0.9;
                    break;
                case "Staying with Friends":
                    resProbability = 0.5;
                    break;
                case "Company Accommodation":
                    resProbability = 0.9;
                    break;
            }
            if (foir <= 20) {fProbability = 1.2;
            } else if (foir > 20 && foir <= 30) {fProbability = 1.1;
            } else if (foir > 30 && foir <= 40) {fProbability = 1.0;
            } else if (foir > 40 && foir <= 50){fProbability = 0.9;
            } else if (foir > 50 && foir <= 60){fProbability = 0.7;
            }else if(foir > 60 && foir <= 70){fProbability = 0.6;
            } else {fProbability = 0.0;}
            if(takeHomeSalaryMonthly == 30000) {sProbability = 1.0;
            } else if (takeHomeSalaryMonthly >= (30000+(30000*0.4))) {sProbability = 1.3;
            } else if(takeHomeSalaryMonthly >= (30000+(30000*0.2)) && takeHomeSalaryMonthly < (30000+(30000*0.4))) {sProbability = 1.1;}
            switch (companyCategory) {
                case "SA":
                    comProbability = 1.05;
                    break;
                case "A":
                    comProbability = 1.0;
                    break;
                case "B":
                    comProbability = 0.9;
                    break;
                case "C":
                    comProbability = 0.8;
                    break;
                case "D":
                    comProbability = 0.7;
                    break;
            }
            if (jobStability >=1) {jProbability = 1.0;
            } else {jProbability = 0.0;}
            int probability = (int) (csProbability + expProbability + laProbability + resProbability + fProbability + comProbability + sProbability + jProbability);
            System.out.println("Overall Probability: " + probability);
            if (probability >= 8.0) {bankProbability = 95;
            }else if (probability >= 7.0 && probability < 8.0 ) {bankProbability = 90;
            }else if (probability >= 6.0 && probability < 7.0) {bankProbability = 85;
            }else if (probability >= 5.0 && probability < 6.0 ) {bankProbability = 80;
            }else if (probability >= 4.0 && probability < 5.0) {bankProbability = 75;
            } else {bankProbability = 70;}
            System.out.println("Final Probability Percentage for Kotak bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for Hero Fincorp bank
        if (bankName.equals("Hero Fincorp")) {
            switch (creditScore) {
                case "A+":
                    csProbability = 1.1;
                    break;
                case "A":
                    csProbability = 1.05;
                    break;
                case "B":
                    csProbability = 1.0;
                    break;
                case "C":
                    csProbability = 0.9;
                    break;
                case "NH":
                    csProbability = 1.0;
                    break;
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience >= 36) {expProbability = 1.1;
                } else if (experience >= 12 && experience < 36) {expProbability = 1.0;
                }else if(experience == 12){expProbability = 0.9;
                } else {expProbability = 0.0;}

            }
            if (loanAmount >=100000 && loanAmount <= 500000) {laProbability = 1.0;
            } else {laProbability = 0.0;}
            switch (residentType) {
                case "Owned":
                    resProbability = 1.1;
                    break;
                case "Rented":
                    resProbability = 0.9;
                    break;
                case "Staying with Friends":
                    resProbability = 0.5;
                    break;
            }
            if (foir <= 20) {fProbability = 1.2;
            } else if (foir > 20 && foir <= 30) {fProbability = 1.1;
            } else if (foir > 30 && foir <= 40) {fProbability = 1.0;
            } else if (foir > 40 && foir <= 50){fProbability = 0.9;
            } else if (foir > 50 && foir <= 60){fProbability = 0.7;
            }else if(foir > 60 && foir <= 75){fProbability = 0.6;
            } else {fProbability = 0.0;}
            if(takeHomeSalaryMonthly == 15000) {sProbability = 1.0;
            } else if (takeHomeSalaryMonthly >= (15000+(15000*0.4))) {sProbability = 1.3;
            } else if(takeHomeSalaryMonthly >= (15000+(15000*0.2)) && takeHomeSalaryMonthly < (15000+(15000*0.4))) {sProbability = 1.1;}
            switch (companyCategory) {
                case "SA":
                    comProbability = 1.05;
                    break;
                case "A":
                    comProbability = 1.0;
                    break;
                case "B":
                    comProbability = 0.9;
                    break;
                case "C":
                    comProbability = 0.8;
                    break;
                case "D":
                    comProbability = 0.7;
                    break;
                case "E":
                    comProbability = 0.6;
                    break;
                case "O":
                    comProbability = 0.5;
                    break;
            }
            if (jobStability >=6) {jProbability = 1.0;
            } else {jProbability = 0.0;}
            int probability = (int) (csProbability + expProbability + laProbability + resProbability + fProbability + comProbability + sProbability + jProbability);
            System.out.println("Overall Probability: " + probability);
            if (probability >= 8.0) {bankProbability = 95;
            }else if (probability >= 7.0 && probability < 8.0 ) {bankProbability = 90;
            }else if (probability >= 6.0 && probability < 7.0) {bankProbability = 85;
            }else if (probability >= 5.0 && probability < 6.0 ) {bankProbability = 80;
            }else if (probability >= 4.0 && probability < 5.0) {bankProbability = 75;
            } else {bankProbability = 70; }
            System.out.println("Final Probability Percentage for Hero Fincorp bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for Muthoot Finance bank
        if (bankName.equals("Muthoot Finance")) {
            switch (creditScore) {
                case "A+":
                    csProbability = 1.1;
                    break;
                case "A":
                    csProbability = 1.05;
                    break;
                case "B":
                    csProbability = 1.0;
                    break;
                case "C":
                    csProbability = 0.9;
                    break;
                case "D":
                    csProbability = 0.8;
                    break;
                case "NH":
                    csProbability = 1.0;
                    break;
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience > 36) {expProbability = 1.1;
                }else if(experience == 36){expProbability = 0.9;
                } else {expProbability = 0.0; }
            }
            if (loanAmount >=100000 && loanAmount <= 500000) {laProbability = 1.0;
            } else if(loanAmount > 500000 && loanAmount <= 750000) {laProbability = 0.9;
            } else if (loanAmount > 750000 && loanAmount <= 1000000) {laProbability = 0.8;
            } else if (loanAmount > 1000000 && loanAmount <= 1250000) {laProbability = 0.7;
            } else if(loanAmount > 1250000 && loanAmount <= 1500000) {laProbability  = 0.6;
            } else {laProbability = 0.0;}
            switch (residentType) {
                case "Owned":
                    resProbability = 1.1;
                    break;
                case "Rented":
                    resProbability = 0.9;
                    break;
                case "Company Accommodation":
                    resProbability = 0.9;
                    break;
                case "Staying with Friends":
                    resProbability = 0.5;
                    break;
                case "PG":
                    resProbability = 0.5;
                    break;
            }
            if (foir <= 20) {fProbability = 1.2;
            } else if (foir > 20 && foir <= 30) {fProbability = 1.1;
            } else if (foir > 30 && foir <= 40) {fProbability = 1.0;
            } else if (foir > 40 && foir <= 50){fProbability = 0.9;
            } else if (foir > 50 && foir <= 60){fProbability = 0.7;
            }else if(foir > 60 && foir <= 70){fProbability = 0.6;
            } else {fProbability = 0.0;}
            if(takeHomeSalaryMonthly == 20000) {sProbability = 1.0;
            } else if (takeHomeSalaryMonthly >= (20000+(20000*0.4))) {sProbability = 1.3;
            } else if(takeHomeSalaryMonthly >= (20000+(20000*0.2)) && takeHomeSalaryMonthly < (20000+(20000*0.4))) {sProbability = 1.1;}
            switch (companyCategory) {
                case "SA":
                    comProbability = 1.05;
                    break;
                case "A":
                    comProbability = 1.0;
                    break;
                case "B":
                    comProbability = 0.9;
                    break;
                case "C":
                    comProbability = 0.8;
                    break;
                case "D":
                    comProbability = 0.7;
                    break;
                case "E":
                    comProbability = 0.6;
                    break;
                case "O":
                    comProbability = 0.5;
                    break;
            }
            if (jobStability >=1) {jProbability = 1.0;
            } else {jProbability = 0.0;}
            int probability = (int) (csProbability + expProbability + laProbability + resProbability + fProbability + comProbability + sProbability + jProbability);
            System.out.println("Overall Probability: " + probability);
            if (probability >= 8.0) {bankProbability = 95;
            }else if (probability >= 7.0 && probability < 8.0 ) {bankProbability = 90;
            }else if (probability >= 6.0 && probability < 7.0) {bankProbability = 85;
            }else if (probability >= 5.0 && probability < 6.0 ) {bankProbability = 80;
            }else if (probability >= 4.0 && probability < 5.0) {bankProbability = 75;
            } else {bankProbability = 70;}
            System.out.println("Final Probability Percentage for Muthoot Finance bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for Finzy bank
        if (bankName.equals("Finzy")) {
            switch (creditScore) {
                case "A+":
                    csProbability = 1.1;
                    break;
                case "A":
                    csProbability = 1.05;
                    break;
                case "B":
                    csProbability = 1.0;
                    break;
                case "C":
                    csProbability = 0.9;
                    break;
                case "D":
                    csProbability = 0.8;
                    break;
                case "NH":
                    csProbability = 1.0;
                    break;
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience > 36) {expProbability = 1.1;
                }else if(experience > 12 && experience <= 36){expProbability = 1.0;
                } else if(experience == 12){expProbability = 0.9;
                } else {expProbability = 0.0;}
            }
            if (loanAmount >=50000 && loanAmount <= 300000) {laProbability = 1.0;
            } else if(loanAmount > 300000 && loanAmount <= 500000) {laProbability = 0.9;
            } else if (loanAmount > 500000 && loanAmount <= 750000) {laProbability = 0.8;
            } else if (loanAmount > 750000 && loanAmount <= 1000000) {laProbability = 0.7;
            } else {laProbability = 0.0;}
            switch (residentType) {
                case "Owned":
                    resProbability = 1.1;
                    break;
                case "Rented":
                    resProbability = 0.9;
                    break;
                case "Company Accommodation":
                    resProbability = 0.9;
                    break;
                case "Staying with Friends":
                    resProbability = 0.5;
                    break;
            }
            if (foir <= 20) {fProbability = 1.2;
            } else if (foir > 20 && foir <= 30) {fProbability = 1.1;
            } else if (foir > 30 && foir <= 40) {fProbability = 1.0;
            } else if (foir > 40 && foir <= 50){fProbability = 0.9;
            } else if (foir > 50 && foir <= 60){fProbability = 0.7;
            }else if(foir > 60 && foir <= 70){fProbability = 0.6;
            } else if(foir > 70 && foir <= 80) {fProbability = 0.5;
            } else {fProbability = 0.0;}
            if(takeHomeSalaryMonthly == 35000) {sProbability = 1.0;
            } else if (takeHomeSalaryMonthly >= (35000+(35000*0.4))) {sProbability = 1.3;
            } else if(takeHomeSalaryMonthly >= (35000+(35000*0.2)) && takeHomeSalaryMonthly < (35000+(35000*0.4))) {sProbability = 1.1;}
            switch (companyCategory) {
                case "SA":
                    comProbability = 1.05;
                    break;
                case "A":
                    comProbability = 1.0;
                    break;
                case "B":
                    comProbability = 0.9;
                    break;
                case "C":
                    comProbability = 0.8;
                    break;
                case "D":
                    comProbability = 0.7;
                    break;
                case "E":
                    comProbability = 0.6;
                    break;
                case "O":
                    comProbability = 0.5;
                    break;
            }
            if (jobStability >=1) {jProbability = 1.0;
            } else {jProbability = 0.0;}
            int probability = (int) (csProbability + expProbability + laProbability + resProbability + fProbability + comProbability + sProbability + jProbability);
            System.out.println("Overall Probability: " + probability);
            if (probability >= 8.0) {bankProbability = 95;
            }else if (probability >= 7.0 && probability < 8.0 ) {bankProbability = 90;
            }else if (probability >= 6.0 && probability < 7.0) {bankProbability = 85;
            }else if (probability >= 5.0 && probability < 6.0 ) {bankProbability = 80;
            }else if (probability >= 4.0 && probability < 5.0) {bankProbability = 75;
            } else {bankProbability = 70; }
            System.out.println("Final Probability Percentage for Finzy bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for Credit Vidhya bank
        if (bankName.equals("Credit Vidya")) {
            switch (creditScore) {
                case "A+":
                    csProbability = 1.1;
                    break;
                case "A":
                    csProbability = 1.05;
                    break;
                case "B":
                    csProbability = 1.0;
                    break;
                case "C":
                    csProbability = 0.9;
                    break;
                case "D":
                    csProbability = 0.8;
                    break;
                case "NH":
                    csProbability = 1.0;
                    break;
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience > 36) { expProbability = 1.1;
                }else if(experience > 3 && experience <= 36){expProbability = 1.0;
                } else if(experience == 3) {expProbability = 0.9;
                }else {expProbability = 0.0;}
            }
            if (loanAmount >=25000 && loanAmount <= 100000) {laProbability = 1.0;
            } else if(loanAmount > 100000 && loanAmount <= 200000) {laProbability = 0.9;
            } else if (loanAmount > 200000 && loanAmount <= 300000) {laProbability = 0.8;
            } else {laProbability = 0.0;}
            switch (residentType) {
                case "Owned":
                    resProbability = 1.1;
                    break;
                case "Rented":
                    resProbability = 0.9;
                    break;
                case "Company Accommodation":
                    resProbability = 0.9;
                    break;
                case "Staying with Friends":
                    resProbability = 0.5;
                    break;
                case "PG":
                    resProbability = 0.5;
                    break;
            }
            if (foir <= 20) {fProbability = 1.2;
            } else if (foir > 20 && foir <= 30) {fProbability = 1.1;
            } else if (foir > 30 && foir <= 40) {fProbability = 1.0;
            } else if (foir > 40 && foir <= 50){fProbability = 0.9;
            } else if (foir > 50 && foir <= 60){fProbability = 0.7;
            }else if(foir > 60 && foir <= 75){fProbability = 0.6;
            } else {fProbability = 0.0;}
            if(takeHomeSalaryMonthly == 15000) {sProbability = 1.0;
            } else if (takeHomeSalaryMonthly >= (15000+(15000*0.4))) {sProbability = 1.3;
            } else if(takeHomeSalaryMonthly >= (15000+(15000*0.2)) && takeHomeSalaryMonthly < (15000+(15000*0.4))) {sProbability = 1.1;}
            switch (companyCategory) {
                case "SA":
                    comProbability = 1.05;
                    break;
                case "A":
                    comProbability = 1.0;
                    break;
                case "B":
                    comProbability = 0.9;
                    break;
                case "C":
                    comProbability = 0.8;
                    break;
                case "D":
                    comProbability = 0.7;
                    break;
                case "E":
                    comProbability = 0.6;
                    break;
                case "O":
                    comProbability = 0.5;
                    break;
            }
            if (jobStability >=3) {jProbability = 1.0;
            } else {jProbability = 0.0;}
            int probability = (int) (csProbability + expProbability + laProbability + resProbability + fProbability + comProbability + sProbability + jProbability);
            System.out.println("Overall Probability: " + probability);
            if (probability >= 8.0) {bankProbability = 95;
            }else if (probability >= 7.0 && probability < 8.0 ) {bankProbability = 90;
            }else if (probability >= 6.0 && probability < 7.0) {bankProbability = 85;
            }else if (probability >= 5.0 && probability < 6.0 ) {bankProbability = 80;
            }else if (probability >= 4.0 && probability < 5.0) {bankProbability = 75;
            } else {bankProbability = 70;}
            System.out.println("Final Probability Percentage for Credit Vidya bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for ABFL bank
        if (bankName.equals("ABFL")) {
            switch (creditScore) {
                case "A+":
                    csProbability = 1.1;
                    break;
                case "A":
                    csProbability = 1.05;
                    break;
                case "B":
                    csProbability = 1.0;
                    break;
                case "C":
                    csProbability = 0.9;
                    break;
                case "NH":
                    csProbability = 1.0;
                    break;
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience > 36) {expProbability = 1.1;
                }else if(experience == 36){expProbability = 1.0;
                } else {expProbability = 0.0;}
            }
            if (loanAmount >=100000 && loanAmount <= 500000) {laProbability = 1.0;
            } else if(loanAmount > 500000 && loanAmount <= 1000000) {laProbability = 0.9;
            } else if (loanAmount > 1000000 && loanAmount <= 2000000) {laProbability = 0.8;
            } else if (loanAmount > 2000000 && loanAmount <= 3000000) {laProbability = 0.7;
            } else if(loanAmount > 3000000 && loanAmount <= 4000000) {laProbability  = 0.6;
            } else if(loanAmount > 4000000 && loanAmount <= 5000000){laProbability = 0.5;
            } else {laProbability = 0.0;}
            switch (residentType) {
                case "Owned":
                    resProbability = 1.1;
                    break;
                case "Rented":
                    resProbability = 0.9;
                    break;
                case "Staying with Friends":
                    resProbability = 0.5;
                    break;
            }
            if (foir <= 20) {fProbability = 1.2;
            } else if (foir > 20 && foir <= 30) {fProbability = 1.1;
            } else if (foir > 30 && foir <= 40) {fProbability = 1.0;
            } else if (foir > 40 && foir <= 50){fProbability = 0.9;
            } else if (foir > 50 && foir <= 60){fProbability = 0.7;
            }else if(foir > 60 && foir <= 70){fProbability = 0.6;
            } else {fProbability = 0.0;}
            if(takeHomeSalaryMonthly == 20000) {sProbability = 1.0;
            } else if (takeHomeSalaryMonthly >= (20000+(20000*0.4))) {sProbability = 1.3;
            } else if(takeHomeSalaryMonthly >= (20000+(20000*0.2)) && takeHomeSalaryMonthly < (20000+(20000*0.4))) {sProbability = 1.1;}
            switch (companyCategory) {
                case "SA":
                    comProbability = 1.05;
                    break;
                case "A":
                    comProbability = 1.0;
                    break;
                case "B":
                    comProbability = 0.9;
                    break;
                case "C":
                    comProbability = 0.8;
                    break;
                case "D":
                    comProbability = 0.7;
                    break;
            }
            if (jobStability >=1) {jProbability = 1.0;
            } else {jProbability = 0.0;}
            int probability = (int) (csProbability + expProbability + laProbability + resProbability + fProbability + comProbability + sProbability + jProbability);
            System.out.println("Overall Probability: " + probability);
            if (probability >= 8.0) {bankProbability = 95;
            }else if (probability >= 7.0 && probability < 8.0 ) {bankProbability = 90;
            }else if (probability >= 6.0 && probability < 7.0) {bankProbability = 85;
            }else if (probability >= 5.0 && probability < 6.0 ) {bankProbability = 80;
            }else if (probability >= 4.0 && probability < 5.0) {bankProbability = 75;
            } else {bankProbability = 70;}
            System.out.println("Final Probability Percentage for ABFL bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for Piramal bank
        if (bankName.equals("Piramal")) {
            switch (creditScore) {
                case "A+":
                    csProbability = 1.1;
                    break;
                case "A":
                    csProbability = 1.05;
                    break;
                case "B":
                    csProbability = 1.0;
                    break;
                case "C":
                    csProbability = 0.9;
                    break;
                case "D":
                    csProbability = 0.8;
                    break;
                case "NH":
                    csProbability = 1.0;
                    break;
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience > 36) { expProbability = 1.1;
                }else if(experience == 36){expProbability = 1.0;
                } else {expProbability = 0.0;}
            }
            if (loanAmount >=100000 && loanAmount <= 300000) {laProbability = 1.0;
            } else if(loanAmount > 300000 && loanAmount <= 500000) {laProbability = 0.9;
            } else if (loanAmount > 500000 && loanAmount <= 700000) {laProbability = 0.8;
            } else if (loanAmount > 700000 && loanAmount <= 1000000) {laProbability = 0.7;
            } else if(loanAmount > 1000000 && loanAmount <= 1200000) {laProbability  = 0.6;
            } else {laProbability = 0.0;}
            switch (residentType) {
                case "Owned":
                    resProbability = 1.1;
                    break;
                case "Rented":
                    resProbability = 0.9;
                    break;
            }
            if (foir <= 20) {fProbability = 1.2;
            } else if (foir > 20 && foir <= 30) {fProbability = 1.1;
            } else if (foir > 30 && foir <= 40) {fProbability = 1.0;
            } else if (foir > 40 && foir <= 50){fProbability = 0.9;
            } else if (foir > 50 && foir <= 60){fProbability = 0.7;
            }else if(foir > 60 && foir <= 75){fProbability = 0.6;
            } else {fProbability = 0.0;}
            if(takeHomeSalaryMonthly == 28000) {sProbability = 1.0;
            } else if (takeHomeSalaryMonthly >= (28000+(28000*0.4))) {sProbability = 1.3;
            } else if(takeHomeSalaryMonthly >= (28000+(28000*0.2)) && takeHomeSalaryMonthly < (28000+(28000*0.4))) {sProbability = 1.1;}
            switch (companyCategory) {
                case "SA":
                    comProbability = 1.05;
                    break;
                case "A":
                    comProbability = 1.0;
                    break;
                case "B":
                    comProbability = 0.9;
                    break;
                case "C":
                    comProbability = 0.8;
                    break;
                case "D":
                    comProbability = 0.7;
                    break;
                case "E":
                    comProbability = 0.6;
                    break;
                case "O":
                    comProbability = 0.5;
                    break;
            }
            if (jobStability >=6) {jProbability = 1.0;
            } else {jProbability = 0.0;}
            int probability = (int) (csProbability + expProbability + laProbability + resProbability + fProbability + comProbability + sProbability + jProbability);
            System.out.println("Overall Probability: " + probability);
            if (probability >= 8.0) {bankProbability = 95;
            }else if (probability >= 7.0 && probability < 8.0 ) {bankProbability = 90;
            }else if (probability >= 6.0 && probability < 7.0) {bankProbability = 85;
            }else if (probability >= 5.0 && probability < 6.0 ) {bankProbability = 80;
            }else if (probability >= 4.0 && probability < 5.0) {bankProbability = 75;
            } else {bankProbability = 70;}
            System.out.println("Final Probability Percentage for Piramal bank: " + bankProbability);
            return bankProbability;
        }
        return bankProbability;
    }
}