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
import com.efundzz.dmnservice.exception.ValidationException;
@Service
public class DMNService {
    private final DMNEvaluator dmnEvaluator;
    @Autowired
    public DMNService(DMNEvaluator dmnEvaluator) {
        this.dmnEvaluator = dmnEvaluator;
    }
    private final Logger logger = LoggerFactory.getLogger(DMNService.class);
    public List<Map<String,Object>> evaluateDecision(Map<String, Object> inputVariables) {
        String decisionKey = "PLBRE_Decisioning";
        logger.info("Evaluating decision with key: {}", decisionKey);
        logger.debug("Input variables: {}", inputVariables);
        // TODO: Validate input variables
        validateInputVariables(inputVariables);
        List<Map<String, Object>> decisionResult = dmnEvaluator.evaluateDecision(decisionKey, inputVariables);
        // Calculate probability for each eligible bank
        calculateProbabilityForBanks(decisionResult, inputVariables);
        logger.info("DMN evaluation completed successfully.");
        return decisionResult;
    }
    // Creating a method validateInputVariables for calling each variable validating
    private void validateInputVariables(Map<String, Object> inputVariables) {
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
    // validateCreditscore method
    private void validateCreditScore(Map<String, Object> inputVariables) {
        Object creditScoreObj = inputVariables.get("creditScore");
        Object loanObj = inputVariables.get("loanAmount");
        if (creditScoreObj == null) {
            Map<String, String> fieldErrors = new HashMap<>();
            fieldErrors.put("creditScore", "Missing 'creditScore' in input variables.");
            throw new ValidationException(fieldErrors);
        }
        String creditScore = (String) creditScoreObj;
        try {
            if (creditScore.equals("F") || creditScore.equals("G")) {
                Map<String, String> fieldErrors = new HashMap<>();
                fieldErrors.put("creditScore", "You are not eligible for your CreditScore concern.");
                throw new ValidationException(fieldErrors);
            }
            if (creditScore.equals("E") && loanObj != null) {
                long loanAmount = Long.parseLong((String) loanObj);
                if (loanAmount > 300000) {
                    Map<String, String> fieldErrors = new HashMap<>();
                    fieldErrors.put("creditScore", "No bank is offering that amount for your credit grade.");
                    throw new ValidationException(fieldErrors);
                }
            }
            if (creditScore.equals("D") && loanObj != null) {
                long loanAmount = Long.parseLong((String) loanObj);
                if (loanAmount > 5000000) {
                    Map<String, String> fieldErrors = new HashMap<>();
                    fieldErrors.put("creditScore", "No bank is offering that amount for your credit grade..");
                    throw new ValidationException(fieldErrors);
                }
            }
        } catch (ValidationException e) {
            throw e;
        }
    }
    //validate takeHomeSalary method
    private void validateHomeSalary(Map<String, Object> inputVariables) {
        Object salaryObj = inputVariables.get("takeHomeSalaryMonthly");
        if (salaryObj == null) {
            throw new IllegalArgumentException("Missing 'TakeHomeSalaryMonthly' in input variables.");
        }

        if (salaryObj instanceof String) {
            try {
                long takeHomeSalaryMonthly = Long.parseLong((String) salaryObj);
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
    private void validateAge(Map<String, Object> inputVariables) {
        Object dobObj = inputVariables.get("dateOfBirth");
        if (dobObj == null) {
            throw new IllegalArgumentException("Missing 'Date of Birth'.");
        }
        if (dobObj instanceof String) {
            DateTimeFormatter dateFormatter = ofPattern("yyyy-MM-dd");
            String dobStr = (String) dobObj;
            try {
                LocalDate dob = LocalDate.parse(dobStr, dateFormatter);
                long age = calculateAge(dob);
                inputVariables.put("age", age);
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
    private void validateExperience(Map<String, Object> inputVariables) {
        Object experienceYearsObj = inputVariables.get("experienceYears");
        Object experienceMonthsObj = inputVariables.get("experienceMonths");
        if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
            try {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = 0;
                if (experienceYears == 0 && experienceMonths != 0) {
                    experience = experienceMonths;
                    inputVariables.put("experience", experience);
                }
                if (experienceYears != 0 && experienceMonths != 0) {
                    experience = experienceYears * 12 + experienceMonths;
                    inputVariables.put("experience", experience);
                }
                if (experienceYears != 0 && experienceMonths == 0) {
                    experience = experienceYears * 12;
                    inputVariables.put("experience", experience);
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
    private void validateResidentType(Map<String, Object> inputVariables) {
        Object residentObj = inputVariables.get("residentType");
        if (residentObj == null) {
            throw new IllegalArgumentException("Missing 'ResidentType' in input variables.");
        }
        if (!(residentObj instanceof String)) {
            throw new IllegalArgumentException("'ResidentType' must be a String.");
        }
        String residentType = (String) residentObj;
        if (!(residentType.equalsIgnoreCase("Owned") || residentType.equalsIgnoreCase("Rented")
                || residentType.equalsIgnoreCase("PG") || residentType.equalsIgnoreCase("Staying with Friends")
                || residentType.equalsIgnoreCase("Company Accommodation"))) {
            throw new IllegalArgumentException("'ResidentType' must be in the given list");
        }
    }
    //Validating LoanAmount method
    private void validateLoanAmount(Map<String, Object> inputVariables) {
        Object amountObj = inputVariables.get("loanAmount");
        if (amountObj == null) {
            throw new IllegalArgumentException("Missing 'LoanAmount' in input variables.");
        }
        if (amountObj instanceof String) {
            try {
                long loanAmount = Long.parseLong((String) amountObj);
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
    private void validateFoir(Map<String, Object> inputVariables) {
        Object emiObj = inputVariables.get("emi");
        Object salaryObj = inputVariables.get("takeHomeSalaryMonthly");
        Object companyObj = inputVariables.get("companyCategory");
        Object amountObj = inputVariables.get("loanAmount");
        int proposedEmi = 0;
        double totalDebt = 0;
        int foir = 0;
        if (emiObj == null) {
            throw new IllegalArgumentException("'EMI' must be in input Variables");
        }
        if (emiObj instanceof String) {
            try {
                double emi = Double.parseDouble((String) emiObj);
                String companyCategory = (String) companyObj;
                double takeHomeSalaryMonthly = Double.parseDouble((String) salaryObj);
                double loanAmount = Double.parseDouble((String) amountObj);
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
                        inputVariables.put("foir", foir);
                    } else if (companyCategory.equalsIgnoreCase("B")) {
                        roi = 15.0;
                        tenure = 48.0;
                        proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure))/ (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        totalDebt = proposedEmi;
                        foir = (int) ((totalDebt/takeHomeSalaryMonthly)*100);
                        System.out.println("foir is" + foir);
                        inputVariables.put("foir", foir);
                    } else if (companyCategory.equalsIgnoreCase("C")) {
                        roi = 18.0;
                        tenure = 36.0;
                        proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure))/ (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        totalDebt = proposedEmi;
                        foir = (int) ((totalDebt/takeHomeSalaryMonthly)*100);
                        System.out.println("foir is" + foir);
                        inputVariables.put("foir", foir);
                    } else if (companyCategory.equalsIgnoreCase("D") ||companyCategory.equalsIgnoreCase("E") || companyCategory.equalsIgnoreCase("O")) {
                        roi = 20.0;
                        tenure = 36.0;
                        proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure))/ (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        totalDebt = proposedEmi;
                        foir = (int) ((totalDebt/takeHomeSalaryMonthly)*100);
                        System.out.println("foir is" + foir);
                        inputVariables.put("foir", foir);
                    }
                }
                if (emi!= 0) {
                    inputVariables.put("proposedEmi", proposedEmi);
                    if (companyCategory.equalsIgnoreCase("SA") ||companyCategory.equalsIgnoreCase("A")) {
                        roi = 12.0;
                        tenure = 60.0;
                        proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure))/ (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        totalDebt = emi + proposedEmi;
                        foir = (int) ((totalDebt/takeHomeSalaryMonthly)*100);
                        System.out.println("foir is" + foir);
                        inputVariables.put("foir", foir);
                    } else if (companyCategory.equalsIgnoreCase("B")) {
                        roi = 15.0;
                        tenure = 48.0;
                        proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure))/ (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        totalDebt = emi + proposedEmi;
                        foir = (int) ((totalDebt/takeHomeSalaryMonthly)*100);
                        System.out.println("foir is" + foir);
                        inputVariables.put("foir", foir);
                    } else if (companyCategory.equalsIgnoreCase("C")) {
                        roi = 18.0;
                        tenure = 36.0;
                        proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure))/ (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        totalDebt = emi + proposedEmi;
                        foir = (int) ((totalDebt/takeHomeSalaryMonthly)*100);
                        System.out.println("foir is" + foir);
                        inputVariables.put("foir", foir);
                    } else if (companyCategory.equalsIgnoreCase("D") ||companyCategory.equalsIgnoreCase("E") || companyCategory.equalsIgnoreCase("O")) {
                        roi = 20.0;
                        tenure = 36.0;
                        proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure))/ (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        totalDebt = emi + proposedEmi;
                        foir = (int) ((totalDebt/takeHomeSalaryMonthly)*100);
                        System.out.println("foir is" + foir);
                        inputVariables.put("foir", foir);
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
    private void validateCompanyCategory(Map<String, Object> inputVariables) {
        Object companyObj = inputVariables.get("companyCategory");
        if (companyObj == null) {
            throw new IllegalArgumentException("CompanyCategory must be in inputvariables");
        }
        if (!(companyObj instanceof String)) {
            throw new IllegalArgumentException("CompanyCategory must be a String");
        }
        String companyCategory = (String) companyObj;
        if (!(companyCategory.equalsIgnoreCase("SA") || companyCategory.equalsIgnoreCase("A") || companyCategory.equalsIgnoreCase("B") || companyCategory.equalsIgnoreCase("C") || companyCategory.equalsIgnoreCase("D") || companyCategory.equalsIgnoreCase("E") || companyCategory.equalsIgnoreCase("O"))) {
            throw new IllegalArgumentException("Company Category must be in the given List");
        }
    }
    //Validating SalaryCreditType method
    private void validateSalaryCreditType(Map<String, Object> inputVariables) {
        Object creditObj = inputVariables.get("salaryCreditType");
        if (creditObj == null) {
            throw new IllegalArgumentException("SalaryCredit must be in InputVariables");
        }
        if (!(creditObj instanceof String)) {
            throw new IllegalArgumentException("SalaryCreditType must be String");
        }
        String salaryCreditType = (String) creditObj;
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
    private void validateOwnHouseAnyWhere(Map<String, Object> inputVariables) {
        Object ownHouseObj = inputVariables.get("ownHouse");
        if (ownHouseObj == null) {
            throw new IllegalArgumentException("OwnHouse Anywhere must be in inputVariables");
        }
        if (!(ownHouseObj instanceof String)) {
            throw new IllegalArgumentException("OwmHouse Anywhere must be String");
        }
        String ownHouse = (String) ownHouseObj;
        if(!(ownHouse.equalsIgnoreCase("Yes") || ownHouse.equalsIgnoreCase("No"))) {
            throw new IllegalArgumentException("OwnHouse Anywhere must be in the given list only");
        }
    }
    //Validating CurrentJobStability method
    private void validateCurrentJobStability(Map<String, Object> inputVariables) {
        Object currentJobObj = inputVariables.get("jobStability");
        if (currentJobObj == null) {
            throw new IllegalArgumentException("CurrentJobStability must be in the inputVariables");
        }
        if((currentJobObj instanceof String)) {
            try {
                int jobStability = Integer.parseInt((String) currentJobObj);
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
    private void calculateProbabilityForBanks(List<Map<String, Object>> decisionResult, Map<String, Object> inputVariables) {
        for (Map<String, Object> bankResult : decisionResult) {
            String bankName = (String) bankResult.get("entity"); // Use "entity" instead of "bankName"
            double bankProbability = calculateProbabilityForBank(bankName, inputVariables);
            bankResult.put("probabilityPercentage", bankProbability);
        }
    }
    // Adding the following method to calculate probability for each bank
    private double calculateProbabilityForBank(String bankName, Map<String, Object> inputVariables) {
        Object creditScoreObj = inputVariables.get("creditScore");
        Object salaryObj = inputVariables.get("takeHomeSalaryMonthly");
        Object experienceYearsObj = inputVariables.get("experienceYears");
        Object experienceMonthsObj = inputVariables.get("experienceMonths");
        Object residentObj = inputVariables.get("residentType");
        Object amountObj = inputVariables.get("loanAmount");
        Object foirObj = inputVariables.get("foir");
        Object companyObj = inputVariables.get("companyCategory");
        Object currentJobObj = inputVariables.get("jobStability");

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
            if (creditScoreObj instanceof String) {
                String creditScore = (String) creditScoreObj;
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
                inputVariables.put("csProbability", csProbability);
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience >= 36) {
                    expProbability = 1.1;
                } else if (experience > 12 && experience < 36) {
                    expProbability = 1.0;
                } else if (experience == 12) {
                    expProbability = 0.9;
                }else {
                    expProbability = 0.0;
                }
                inputVariables.put("expProbability", expProbability);
            }
            if (amountObj instanceof String) {
                long loanAmount = Long.parseLong((String) amountObj);
                if (loanAmount >= 10000 && loanAmount <= 400000) {
                    laProbability = 1.1;
                }
                inputVariables.put("laProbability", laProbability);
            }
            if (residentObj instanceof String) {
                String residentType = (String) residentObj;
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
                inputVariables.put("resProbability", resProbability);
            }
            if (foirObj instanceof Integer) {
                int foir = (int) foirObj;
                if (foir <= 20) {
                    fProbability = 1.2;
                } else if (foir > 20 && foir <= 30) {
                    fProbability = 1.1;
                } else if (foir > 30 && foir <= 40) {
                    fProbability = 1.0;
                } else {
                    fProbability = 0.9;
                }
                inputVariables.put("fProbability", fProbability);
            }
            if (salaryObj instanceof String) {
                long takeHomeSalaryMonthly = Long.parseLong((String) salaryObj);
                if(takeHomeSalaryMonthly == 12000) {
                    sProbability = 1.0;
                } else if (takeHomeSalaryMonthly >= (12000+(12000*0.4))) {
                    sProbability = 1.3;
                } else if(takeHomeSalaryMonthly >= (12000+(12000*0.2)) && takeHomeSalaryMonthly < (12000+(12000*0.4))) {
                    sProbability = 1.1;
                }else {
                    sProbability = 0.0;
                }
                inputVariables.put("sProbability", sProbability);
            }
            if (companyObj instanceof String) {
                String companyCategory = (String) companyObj;
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
                inputVariables.put("comProbability", comProbability);
            }
            if (currentJobObj instanceof String) {
                int jobStability = Integer.parseInt((String) currentJobObj);
                if (jobStability >=1) {
                    jProbability = 1.0;
                } else {
                    jProbability = 0.0;
                }
                inputVariables.put("jProbability", jProbability);
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
            System.out.println("Final Probability Percentage for Cashe bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for MySubhLife bank
        if (bankName.equals("MyShubhLife")) {
            if (creditScoreObj instanceof String) {
                String creditScore = (String) creditScoreObj;
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
                inputVariables.put("csProbability", csProbability);
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience >= 36) {
                    expProbability = 1.1;
                } else if (experience > 12 && experience < 36) {
                    expProbability = 1.0;
                } else if (experience == 12) {
                    expProbability = 0.9;
                }else {
                    expProbability = 0.0;
                }
                inputVariables.put("expProbability", expProbability);
            }
            if (amountObj instanceof String) {
                long loanAmount = Long.parseLong((String) amountObj);
                if (loanAmount >= 5000 && loanAmount <= 300000) {
                    laProbability = 1.1;
                }
                inputVariables.put("laProbability", laProbability);
            }
            if (residentObj instanceof String) {
                String residentType = (String) residentObj;
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
                inputVariables.put("resProbability", resProbability);
            }
            if (foirObj instanceof Integer) {
                int foir = (int) foirObj;
                if (foir <= 20) {
                    fProbability = 1.2;
                } else if (foir > 20 && foir <= 30) {
                    fProbability = 1.1;
                } else if (foir > 30 && foir <= 40) {
                    fProbability = 1.0;
                } else {
                    fProbability = 0.9;
                }
                inputVariables.put("fProbability", fProbability);
            }
            if (salaryObj instanceof String) {
                long takeHomeSalaryMonthly = Long.parseLong((String) salaryObj);
                if(takeHomeSalaryMonthly == 12000) {
                    sProbability = 1.0;
                } else if (takeHomeSalaryMonthly >= (12000+(12000*0.4))) {
                    sProbability = 1.3;
                } else if(takeHomeSalaryMonthly >= (12000+(12000*0.2)) && takeHomeSalaryMonthly < (12000+(12000*0.4))) {
                    sProbability = 1.1;
                }
                inputVariables.put("sProbability", sProbability);
            }
            if (companyObj instanceof String) {
                String companyCategory = (String) companyObj;
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
                inputVariables.put("comProbability", comProbability);
            }
            if (currentJobObj instanceof String) {
                int jobStability = Integer.parseInt((String) currentJobObj);
                if (jobStability >=1) {
                    jProbability = 1.0;
                } else {
                    jProbability = 0.0;
                }
                inputVariables.put("jProbability", jProbability);
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
            if (creditScoreObj instanceof String) {
                String creditScore = (String) creditScoreObj;
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
                inputVariables.put("csProbability", csProbability);
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
                inputVariables.put("expProbability", expProbability);
            }
            if (amountObj instanceof String) {
                long loanAmount = Long.parseLong((String) amountObj);
                if (loanAmount >=100000 && loanAmount <= 500000) {
                    laProbability = 1.1;
                } else if (loanAmount > 500000 && loanAmount <= 1000000) {
                    laProbability = 1.0;
                } else if (loanAmount > 1000000 && loanAmount <= 2000000) {
                    laProbability = 0.9;
                } else if (loanAmount > 2000000 && loanAmount <= 3000000) {
                    laProbability = 0.8;
                } else if (loanAmount > 3000000 && loanAmount <= 5000000) {
                    laProbability = 0.7;
                } else {
                    laProbability = 0.0;
                }
                inputVariables.put("laProbability", laProbability);
            }
            if (residentObj instanceof String) {
                String residentType = (String) residentObj;
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
                inputVariables.put("resProbability", resProbability);
            }
            if (foirObj instanceof Integer) {
                int foir = (int) foirObj;
                if (foir <= 20) {
                    fProbability = 1.2;
                } else if (foir > 20 && foir <= 30) {
                    fProbability = 1.1;
                } else if (foir > 30 && foir <= 40) {
                    fProbability = 1.0;
                } else if (foir > 40 && foir <= 50){
                    fProbability = 0.9;
                } else if (foir > 50 && foir <= 70){
                    fProbability = 0.7;
                }
                inputVariables.put("fProbability", fProbability);
            }
            if (salaryObj instanceof String) {
                long takeHomeSalaryMonthly = Long.parseLong((String) salaryObj);
                if(takeHomeSalaryMonthly == 20000) {
                    sProbability = 1.0;
                } else if (takeHomeSalaryMonthly >= (20000+(20000*0.4))) {
                    sProbability = 1.3;
                } else if(takeHomeSalaryMonthly >= (20000+(20000*0.2)) && takeHomeSalaryMonthly < (20000+(20000*0.4))) {
                    sProbability = 1.1;
                }
                inputVariables.put("sProbability", sProbability);
            }
            if (companyObj instanceof String) {
                String companyCategory = (String) companyObj;
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
                inputVariables.put("comProbability", comProbability);
            }
            if (currentJobObj instanceof String) {
                int jobStability = Integer.parseInt((String) currentJobObj);
                if (jobStability >=3) {
                    jProbability = 1.0;
                } else {
                    jProbability = 0.0;
                }
                inputVariables.put("jProbability", jProbability);
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
            if (creditScoreObj instanceof String) {
                String creditScore = (String) creditScoreObj;
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
                inputVariables.put("csProbability", csProbability);
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience >= 36) {
                    expProbability = 1.1;
                } else if (experience > 12 && experience < 36) {
                    expProbability = 1.0;
                } else if (experience == 12) {
                    expProbability = 0.9;
                }else {
                    expProbability = 0.0;
                }
                inputVariables.put("expProbability", expProbability);
            }
            if (amountObj instanceof String) {
                long loanAmount = Long.parseLong((String) amountObj);
                if (loanAmount >= 100000 && loanAmount <= 500000) {
                    laProbability = 1.1;
                } else if (loanAmount > 500000 && loanAmount <= 1000000) {
                    laProbability = 1.0;
                } else if (loanAmount > 1000000 && loanAmount <= 2000000) {
                    laProbability = 0.9;
                } else if (loanAmount > 2000000 && loanAmount <= 3000000) {
                    laProbability = 0.8;
                } else if (loanAmount > 3000000 && loanAmount <= 4000000) {
                    laProbability = 0.7;
                } else {
                    laProbability = 0.0;
                }
                inputVariables.put("laProbability", laProbability);
            }
            if (residentObj instanceof String) {
                String residentType = (String) residentObj;
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
                inputVariables.put("resProbability", resProbability);
            }
            if (foirObj instanceof Integer) {
                int foir = (int) foirObj;
                if (foir <= 20) {
                    fProbability = 1.2;
                } else if (foir > 20 && foir <= 30) {
                    fProbability = 1.1;
                } else if (foir > 30 && foir <= 40) {
                    fProbability = 1.0;
                } else if(foir > 40 && foir <= 50) {
                    fProbability = 0.9;
                }else {
                    fProbability = 0.8;
                }
                inputVariables.put("fProbability", fProbability);
            }
            if (salaryObj instanceof String) {
                long takeHomeSalaryMonthly = Long.parseLong((String) salaryObj);
                if(takeHomeSalaryMonthly == 25000) {
                    sProbability = 1.0;
                } else if (takeHomeSalaryMonthly >= (25000+(25000*0.4))) {
                    sProbability = 1.3;
                } else if(takeHomeSalaryMonthly >= (25000+(25000*0.2)) && takeHomeSalaryMonthly < (25000+(25000*0.4))) {
                    sProbability = 1.1;
                }
                inputVariables.put("sProbability", sProbability);
            }
            if (companyObj instanceof String) {
                String companyCategory = (String) companyObj;
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
                inputVariables.put("comProbability", comProbability);
            }
            if (currentJobObj instanceof String) {
                int jobStability = Integer.parseInt((String) currentJobObj);
                if (jobStability >=1) {
                    jProbability = 1.0;
                } else {
                    jProbability = 0.0;
                }
                inputVariables.put("jProbability", jProbability);
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
            if (creditScoreObj instanceof String) {
                String creditScore = (String) creditScoreObj;
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
                inputVariables.put("csProbability", csProbability);
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience >= 36) {
                    expProbability = 1.1;
                } else if (experience > 12 && experience < 36) {
                    expProbability = 1.0;
                } else if (experience == 12) {
                    expProbability = 0.9;
                }else {
                    expProbability = 0.0;
                }
                inputVariables.put("expProbability", expProbability);
            }
            if (amountObj instanceof String) {
                long loanAmount = Long.parseLong((String) amountObj);
                if (loanAmount >= 50000 && loanAmount <= 500000) {
                    laProbability = 1.1;
                } else if (loanAmount > 500000 && loanAmount <= 1000000) {
                    laProbability = 1.0;
                } else if (loanAmount > 1000000 && loanAmount <= 2000000) {
                    laProbability = 0.9;
                } else if (loanAmount > 2000000 && loanAmount <= 3000000) {
                    laProbability = 0.8;
                } else if (loanAmount > 3000000 && loanAmount <= 4000000) {
                    laProbability = 0.7;
                } else if(loanAmount > 4000000 && loanAmount <= 6000000){
                    laProbability = 0.6;
                } else {
                    laProbability = 0.5;
                }
                inputVariables.put("laProbability", laProbability);
            }
            if (residentObj instanceof String) {
                String residentType = (String) residentObj;
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
                inputVariables.put("resProbability", resProbability);
            }
            if (foirObj instanceof Integer) {
                int foir = (int) foirObj;
                if (foir <= 20) {
                    fProbability = 1.2;
                } else if (foir > 20 && foir <= 30) {
                    fProbability = 1.1;
                } else if (foir > 30 && foir <= 40) {
                    fProbability = 1.0;
                } else if(foir > 40 && foir <= 50){
                    fProbability = 0.9;
                } else {
                    fProbability = 0.8;
                }
                inputVariables.put("fProbability", fProbability);
            }
            if (salaryObj instanceof String) {
                long takeHomeSalaryMonthly = Long.parseLong((String) salaryObj);
                if(takeHomeSalaryMonthly == 25000) {
                    sProbability = 1.0;
                } else if (takeHomeSalaryMonthly >= (25000+(25000*0.4))) {
                    sProbability = 1.3;
                } else if(takeHomeSalaryMonthly >= (25000+(25000*0.2)) && takeHomeSalaryMonthly < (25000+(25000*0.4))) {
                    sProbability = 1.1;
                }
                inputVariables.put("sProbability", sProbability);
            }
            if (companyObj instanceof String) {
                String companyCategory = (String) companyObj;
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
                inputVariables.put("comProbability", comProbability);
            }
            if (currentJobObj instanceof String) {
                int jobStability = Integer.parseInt((String) currentJobObj);
                if (jobStability >=1) {
                    jProbability = 1.0;
                } else {
                    jProbability = 0.0;
                }
                inputVariables.put("jProbability", jProbability);
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
            System.out.println("Final Probability Percentage for HDFC Bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for ICICI bank
        if (bankName.equals("ICICI")) {
            if (creditScoreObj instanceof String) {
                String creditScore = (String) creditScoreObj;
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
                inputVariables.put("csProbability", csProbability);
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
                inputVariables.put("expProbability", expProbability);
            }
            if (amountObj instanceof String) {
                long loanAmount = Long.parseLong((String) amountObj);
                if (loanAmount >=100000 && loanAmount <= 500000) {
                    laProbability = 1.1;
                } else if (loanAmount > 500000 && loanAmount <= 1000000) {
                    laProbability = 1.0;
                } else if (loanAmount > 1000000 && loanAmount <= 2000000) {
                    laProbability = 0.9;
                } else if (loanAmount > 2000000 && loanAmount <= 3000000) {
                    laProbability = 0.8;
                } else if (loanAmount > 3000000 && loanAmount <= 5000000) {
                    laProbability = 0.7;
                } else if(loanAmount > 5000000 && loanAmount <= 7000000){
                    laProbability = 0.6;
                } else {
                    laProbability = 0.5;
                }
                inputVariables.put("laProbability", laProbability);
            }
            if (residentObj instanceof String) {
                String residentType = (String) residentObj;
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
                inputVariables.put("resProbability", resProbability);
            }
            if (foirObj instanceof Integer) {
                int foir = (int) foirObj;
                if (foir <= 20) {
                    fProbability = 1.2;
                } else if (foir > 20 && foir <= 30) {
                    fProbability = 1.1;
                } else if (foir > 30 && foir <= 40) {
                    fProbability = 1.0;
                } else if (foir > 40 && foir <= 50){
                    fProbability = 0.9;
                } else if (foir > 50 && foir <= 70){
                    fProbability = 0.7;
                }else {
                    fProbability = 0.6;
                }
                inputVariables.put("fProbability", fProbability);
            }
            if (salaryObj instanceof String) {
                long takeHomeSalaryMonthly = Long.parseLong((String) salaryObj);
                if(takeHomeSalaryMonthly == 25000) {
                    sProbability = 1.0;
                } else if (takeHomeSalaryMonthly >= (25000+(25000*0.4))) {
                    sProbability = 1.3;
                } else if(takeHomeSalaryMonthly >= (25000+(25000*0.2)) && takeHomeSalaryMonthly < (25000+(25000*0.4))) {
                    sProbability = 1.1;
                }
                inputVariables.put("sProbability", sProbability);
            }
            if (companyObj instanceof String) {
                String companyCategory = (String) companyObj;
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
                inputVariables.put("comProbability", comProbability);
            }
            if (currentJobObj instanceof String) {
                int jobStability = Integer.parseInt((String) currentJobObj);
                if (jobStability >=1) {
                    jProbability = 1.0;
                } else {
                    jProbability = 0.0;
                }
                inputVariables.put("jProbability", jProbability);
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
            System.out.println("Final Probability Percentage for ICICI bank: " + bankProbability);
            return bankProbability;
        }
        //calculating Probability for Yes Bank
        if (bankName.equals("Yes Bank")) {
            if (creditScoreObj instanceof String) {
                String creditScore = (String) creditScoreObj;
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
                inputVariables.put("csProbability", csProbability);
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
                inputVariables.put("expProbability", expProbability);
            }
            if (amountObj instanceof String) {
                long loanAmount = Long.parseLong((String) amountObj);
                if (loanAmount >= 50000 && loanAmount <= 500000) {
                    laProbability = 1.1;
                }else if (loanAmount > 500000 && loanAmount <= 1000000) {
                    laProbability = 1.0;
                } else if (loanAmount > 1000000 && loanAmount <= 2000000) {
                    laProbability = 0.9;
                } else if (loanAmount > 2000000 && loanAmount <= 3000000) {
                    laProbability = 0.8;
                } else if (loanAmount > 3000000 && loanAmount <= 5000000) {
                    laProbability = 0.7;
                } else {
                    laProbability = 0.6;
                }
                inputVariables.put("laProbability", laProbability);
            }
            if (residentObj instanceof String) {
                String residentType = (String) residentObj;
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
                inputVariables.put("resProbability", resProbability);
            }
            if (foirObj instanceof Integer) {
                int foir = (int) foirObj;
                if (foir <= 20) {
                    fProbability = 1.2;
                } else if (foir > 20 && foir <= 30) {
                    fProbability = 1.1;
                } else if (foir > 30 && foir <= 40) {
                    fProbability = 1.0;
                } else if(foir > 40 && foir <= 50){
                    fProbability = 0.9;
                }else if(foir > 50 && foir <= 60){
                    fProbability = 0.8;
                }else {
                    fProbability = 0.7;
                }
                inputVariables.put("fProbability", fProbability);
            }
            if (salaryObj instanceof String) {
                long takeHomeSalaryMonthly = Long.parseLong((String) salaryObj);
                if(takeHomeSalaryMonthly == 20000) {
                    sProbability = 1.0;
                } else if (takeHomeSalaryMonthly >= (20000+(20000*0.4))) {
                    sProbability = 1.3;
                } else if(takeHomeSalaryMonthly >= (20000+(20000*0.2)) && takeHomeSalaryMonthly < (20000+(20000*0.4))) {
                    sProbability = 1.1;
                }
                inputVariables.put("sProbability", sProbability);
            }
            if (companyObj instanceof String) {
                String companyCategory = (String) companyObj;
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
                inputVariables.put("comProbability", comProbability);
            }
            if (currentJobObj instanceof String) {
                int jobStability = Integer.parseInt((String) currentJobObj);
                if (jobStability >=1) {
                    jProbability = 1.0;
                } else {
                    jProbability = 0.0;
                }
                inputVariables.put("jProbability", jProbability);
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
            System.out.println("Final Probability Percentage for Yes bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for Axis Finance bank
        if (bankName.equals("Axis Finance")) {
            if (creditScoreObj instanceof String) {
                String creditScore = (String) creditScoreObj;
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
                inputVariables.put("csProbability", csProbability);
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
                inputVariables.put("expProbability", expProbability);
            }
            if (amountObj instanceof String) {
                long loanAmount = Long.parseLong((String) amountObj);
                if (loanAmount >= 200000 && loanAmount <= 500000) {
                    laProbability = 1.1;
                } else if(loanAmount > 500000 && loanAmount <= 1000000) {
                    laProbability = 1.0;
                } else if(loanAmount > 1000000 && loanAmount <= 2000000) {
                    laProbability = 0.9;
                } else if(loanAmount > 2000000 && loanAmount <= 3000000) {
                    laProbability = 0.8;
                }else if(loanAmount > 3000000 && loanAmount <= 4000000) {
                    laProbability = 0.7;
                }else if(loanAmount > 4000000 && loanAmount <= 5000000) {
                    laProbability = 0.6;
                }else {
                    laProbability = 0.5;
                }
                inputVariables.put("laProbability", laProbability);
            }
            if (residentObj instanceof String) {
                String residentType = (String) residentObj;
                String companyCategory = (String) companyObj;
                switch (residentType) {
                    case "Owned":
                        resProbability = 1.1;
                        break;
                    case "Rented":
                        resProbability = 0.9;
                        break;
                    case "Staying with Friends":
                        if(companyCategory.equalsIgnoreCase("SA") || companyCategory.equalsIgnoreCase("A")) {
                            resProbability = 0.5;
                        }else {
                            resProbability = 0.0;
                        }
                        break;
                }
                inputVariables.put("resProbability", resProbability);
            }
            if (foirObj instanceof Integer) {
                int foir = (int) foirObj;
                if (foir <= 20) {
                    fProbability = 1.2;
                } else if (foir > 20 && foir <= 30) {
                    fProbability = 1.1;
                } else if (foir > 30 && foir <= 40) {
                    fProbability = 1.0;
                } else if(foir > 40 && foir <= 50) {
                    fProbability = 0.9;
                }else if(foir > 50 && foir <= 60) {
                    fProbability = 0.8;
                } else {
                    fProbability = 0.7;
                }
                inputVariables.put("fProbability", fProbability);
            }
            if (salaryObj instanceof String) {
                long takeHomeSalaryMonthly = Long.parseLong((String) salaryObj);
                if(takeHomeSalaryMonthly == 30000) {
                    sProbability = 1.0;
                } else if (takeHomeSalaryMonthly >= (30000+(30000*0.4))) {
                    sProbability = 1.3;
                } else if(takeHomeSalaryMonthly >= (30000+(30000*0.2)) && takeHomeSalaryMonthly < (30000+(30000*0.4))) {
                    sProbability = 1.1;
                }
                inputVariables.put("sProbability", sProbability);
            }
            if (companyObj instanceof String) {
                String companyCategory = (String) companyObj;
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
                inputVariables.put("comProbability", comProbability);
            }
            if (currentJobObj instanceof String) {
                int jobStability = Integer.parseInt((String) currentJobObj);
                if (jobStability >=12) {
                    jProbability = 1.0;
                } else {
                    jProbability = 0.0;
                }
                inputVariables.put("jProbability", jProbability);
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
            System.out.println("Final Probability Percentage for Axis Finance Bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for Bajaj Finserv bank
        if (bankName.equals("Bajaj Finserv")) {
            if (creditScoreObj instanceof String) {
                String creditScore = (String) creditScoreObj;
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
                inputVariables.put("csProbability", csProbability);
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
                inputVariables.put("expProbability", expProbability);
            }
            if (amountObj instanceof String) {
                long loanAmount = Long.parseLong((String) amountObj);
                if (loanAmount >=100000 && loanAmount <= 500000) {
                    laProbability = 1.1;
                } else if (loanAmount > 500000 && loanAmount <= 1000000) {
                    laProbability = 1.0;
                } else if (loanAmount > 1000000 && loanAmount <= 2000000) {
                    laProbability = 0.9;
                } else if (loanAmount > 2000000 && loanAmount <= 3000000) {
                    laProbability = 0.8;
                } else if (loanAmount > 3000000 && loanAmount <= 3500000) {
                    laProbability = 0.7;
                } else {
                    laProbability = 0.0;
                }
                inputVariables.put("laProbability", laProbability);
            }
            if (residentObj instanceof String) {
                String residentType = (String) residentObj;
                switch (residentType) {
                    case "Owned":
                        resProbability = 1.1;
                        break;
                    case "Rented":
                        resProbability = 0.9;
                        break;
                }
                inputVariables.put("resProbability", resProbability);
            }
            if (foirObj instanceof Integer) {
                int foir = (int) foirObj;
                if (foir <= 20) {
                    fProbability = 1.2;
                } else if (foir > 20 && foir <= 30) {
                    fProbability = 1.1;
                } else if (foir > 30 && foir <= 40) {
                    fProbability = 1.0;
                } else if (foir > 40 && foir <= 50){
                    fProbability = 0.9;
                } else if (foir > 50 && foir <= 70){
                    fProbability = 0.7;
                }
                inputVariables.put("fProbability", fProbability);
            }
            if (salaryObj instanceof String) {
                long takeHomeSalaryMonthly = Long.parseLong((String) salaryObj);
                if(takeHomeSalaryMonthly == 27000) {
                    sProbability = 1.0;
                } else if (takeHomeSalaryMonthly >= (27000+(27000*0.4))) {
                    sProbability = 1.3;
                } else if(takeHomeSalaryMonthly >= (27000+(27000*0.2)) && takeHomeSalaryMonthly < (27000+(27000*0.4))) {
                    sProbability = 1.1;
                }
                inputVariables.put("sProbability", sProbability);
            }
            if (companyObj instanceof String) {
                String companyCategory = (String) companyObj;
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
                inputVariables.put("comProbability", comProbability);
            }
            if (currentJobObj instanceof String) {
                int jobStability = Integer.parseInt((String) currentJobObj);
                if (jobStability >=1) {
                    jProbability = 1.0;
                } else {
                    jProbability = 0.0;
                }
                inputVariables.put("jProbability", jProbability);
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
            System.out.println("Final Probability Percentage for Bajaj Finserv bank: " + bankProbability);
            return bankProbability;
        }
        //validating probability for Poonawala Bank
        if (bankName.equals("Poonawala")) {
            if (creditScoreObj instanceof String) {
                String creditScore = (String) creditScoreObj;
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
                inputVariables.put("csProbability", csProbability);
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
                inputVariables.put("expProbability", expProbability);
            }
            if (amountObj instanceof String) {
                long loanAmount = Long.parseLong((String) amountObj);
                if (loanAmount >= 100000 && loanAmount <= 500000) {
                    laProbability = 1.1;
                } else if (loanAmount > 500000 && loanAmount <= 1000000) {
                    laProbability = 1.0;
                } else if (loanAmount > 1000000 && loanAmount <= 2000000) {
                    laProbability = 0.9;
                } else if (loanAmount > 2000000 && loanAmount <= 3000000) {
                    laProbability = 0.8;
                } else {
                    laProbability = 0.0;
                }
                inputVariables.put("laProbability", laProbability);
            }
            if (residentObj instanceof String) {
                String residentType = (String) residentObj;
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
                inputVariables.put("resProbability", resProbability);
            }
            if (foirObj instanceof Integer) {
                int foir = (int) foirObj;
                if (foir <= 20) {
                    fProbability = 1.2;
                } else if (foir > 20 && foir <= 30) {
                    fProbability = 1.1;
                } else if (foir > 30 && foir <= 40) {
                    fProbability = 1.0;
                } else if(foir > 40 && foir <= 50) {
                    fProbability = 0.9;
                }else if(foir > 50 && foir <= 60) {
                    fProbability = 0.8;
                }else if(foir > 60 && foir <= 75) {
                    fProbability = 0.7;
                }else {
                    fProbability = 0.0;
                }
                inputVariables.put("fProbability", fProbability);
            }
            if (salaryObj instanceof String) {
                long takeHomeSalaryMonthly = Long.parseLong((String) salaryObj);
                if(takeHomeSalaryMonthly == 30000) {
                    sProbability = 1.0;
                } else if (takeHomeSalaryMonthly >= (30000+(30000*0.4))) {
                    sProbability = 1.3;
                } else if(takeHomeSalaryMonthly >= (30000+(30000*0.2)) && takeHomeSalaryMonthly < (30000+(30000*0.4))) {
                    sProbability = 1.1;
                }
                inputVariables.put("sProbability", sProbability);
            }
            if (companyObj instanceof String) {
                String companyCategory = (String) companyObj;
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
                inputVariables.put("comProbability", comProbability);
            }
            if (currentJobObj instanceof String) {
                int jobStability = Integer.parseInt((String) currentJobObj);
                if (jobStability >=1) {
                    jProbability = 1.0;
                } else {
                    jProbability = 0.0;
                }
                inputVariables.put("jProbability", jProbability);
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
            System.out.println("Final Probability Percentage for Poonawala bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for Fullerton bank
        if (bankName.equals("Fullerton")) {
            if (creditScoreObj instanceof String) {
                String creditScore = (String) creditScoreObj;
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
                inputVariables.put("csProbability", csProbability);
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
                inputVariables.put("expProbability", expProbability);
            }
            if (amountObj instanceof String) {
                long loanAmount = Long.parseLong((String) amountObj);
                if (loanAmount >= 100000 && loanAmount <= 500000) {
                    laProbability = 1.0;
                } else if (loanAmount > 500000 && loanAmount <= 1000000) {
                    laProbability = 0.9;
                } else if (loanAmount > 1000000 && loanAmount <= 2000000) {
                    laProbability = 0.8;
                } else if (loanAmount > 2000000 && loanAmount <= 2500000) {
                    laProbability = 0.7;
                } else {
                    laProbability = 0.0;
                }
                inputVariables.put("laProbability", laProbability);
            }
            if (residentObj instanceof String) {
                String residentType = (String) residentObj;
                switch (residentType) {
                    case "Owned":
                        resProbability = 1.1;
                        break;
                    case "Rented":
                        resProbability = 0.9;
                        break;
                }
                inputVariables.put("resProbability", resProbability);
            }
            if (foirObj instanceof Integer) {
                int foir = (int) foirObj;
                if (foir <= 20) {
                    fProbability = 1.2;
                } else if (foir > 20 && foir <= 30) {
                    fProbability = 1.1;
                } else if (foir > 30 && foir <= 40) {
                    fProbability = 1.0;
                } else if(foir > 40 && foir <= 50){
                    fProbability = 0.9;
                } else if(foir > 50 && foir <=60){
                    fProbability = 0.8;
                } else if(foir > 60 && foir <= 70) {
                    fProbability = 0.7;
                }else {
                    fProbability = 0.0;
                }
                inputVariables.put("fProbability", fProbability);
            }
            if (salaryObj instanceof String) {
                long takeHomeSalaryMonthly = Long.parseLong((String) salaryObj);
                if(takeHomeSalaryMonthly == 20000) {
                    sProbability = 1.0;
                } else if (takeHomeSalaryMonthly >= (20000+(20000*0.4))) {
                    sProbability = 1.3;
                } else if(takeHomeSalaryMonthly >= (20000+(25000*0.2)) && takeHomeSalaryMonthly < (20000+(20000*0.4))) {
                    sProbability = 1.1;
                }
                inputVariables.put("sProbability", sProbability);
            }
            if (companyObj instanceof String) {
                String companyCategory = (String) companyObj;
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
                inputVariables.put("comProbability", comProbability);
            }
            if (currentJobObj instanceof String) {
                int jobStability = Integer.parseInt((String) currentJobObj);
                if (jobStability >=1) {
                    jProbability = 1.0;
                } else {
                    jProbability = 0.0;
                }
                inputVariables.put("jProbability", jProbability);
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
            System.out.println("Final Probability Percentage for Fullerton Bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for Cholamandalam bank
        if (bankName.equals("Cholamandalam")) {
            if (creditScoreObj instanceof String) {
                String creditScore = (String) creditScoreObj;
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
                inputVariables.put("csProbability", csProbability);
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
                inputVariables.put("expProbability", expProbability);
            }
            if (amountObj instanceof String) {
                long loanAmount = Long.parseLong((String) amountObj);
                if (loanAmount >=100000 && loanAmount <= 500000) {
                    laProbability = 1.0;
                } else if (loanAmount > 500000 && loanAmount <= 1000000) {
                    laProbability = 0.9;
                } else if (loanAmount > 1000000 && loanAmount <= 2000000) {
                    laProbability = 0.8;
                } else {
                    laProbability = 0.0;
                }
                inputVariables.put("laProbability", laProbability);
            }
            if (residentObj instanceof String) {
                String residentType = (String) residentObj;
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
                inputVariables.put("resProbability", resProbability);
            }
            if (foirObj instanceof Integer) {
                int foir = (int) foirObj;
                if (foir <= 20) {
                    fProbability = 1.2;
                } else if (foir > 20 && foir <= 30) {
                    fProbability = 1.1;
                } else if (foir > 30 && foir <= 40) {
                    fProbability = 1.0;
                } else if (foir > 40 && foir <= 50){
                    fProbability = 0.9;
                } else if (foir > 50 && foir <= 70){
                    fProbability = 0.7;
                }else {
                    fProbability = 0.0;
                }
                inputVariables.put("fProbability", fProbability);
            }
            if (salaryObj instanceof String) {
                long takeHomeSalaryMonthly = Long.parseLong((String) salaryObj);
                if(takeHomeSalaryMonthly == 25000) {
                    sProbability = 1.0;
                } else if (takeHomeSalaryMonthly >= (25000+(25000*0.4))) {
                    sProbability = 1.3;
                } else if(takeHomeSalaryMonthly >= (25000+(25000*0.2)) && takeHomeSalaryMonthly < (25000+(25000*0.4))) {
                    sProbability = 1.1;
                }
                inputVariables.put("sProbability", sProbability);
            }
            if (companyObj instanceof String) {
                String companyCategory = (String) companyObj;
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
                inputVariables.put("comProbability", comProbability);
            }
            if (currentJobObj instanceof String) {
                int jobStability = Integer.parseInt((String) currentJobObj);
                if (jobStability >=1) {
                    jProbability = 1.0;
                } else {
                    jProbability = 0.0;
                }
                inputVariables.put("jProbability", jProbability);
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
            System.out.println("Final Probability Percentage for Cholamandalam bank: " + bankProbability);
            return bankProbability;
        }
        //calculating Probability for TATA Capital bank
        if (bankName.equals("TATA Capital")) {
            if (creditScoreObj instanceof String) {
                String creditScore = (String) creditScoreObj;
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
                inputVariables.put("csProbability", csProbability);
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
                inputVariables.put("expProbability", expProbability);
            }
            if (amountObj instanceof String) {
                long loanAmount = Long.parseLong((String) amountObj);
                if (loanAmount >= 100000 && loanAmount <= 500000) {
                    laProbability = 1.1;
                }else if (loanAmount > 500000 && loanAmount <= 1000000) {
                    laProbability = 1.0;
                } else if (loanAmount > 1000000 && loanAmount <= 2000000) {
                    laProbability = 0.9;
                } else if (loanAmount > 2000000 && loanAmount <= 3000000) {
                    laProbability = 0.8;
                } else if (loanAmount > 3000000 && loanAmount <= 3500000) {
                    laProbability = 0.7;
                } else {
                    laProbability = 0.0;
                }
                inputVariables.put("laProbability", laProbability);
            }
            if (residentObj instanceof String) {
                String residentType = (String) residentObj;
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
                inputVariables.put("resProbability", resProbability);
            }
            if (foirObj instanceof Integer) {
                int foir = (int) foirObj;
                if (foir <= 20) {
                    fProbability = 1.2;
                } else if (foir > 20 && foir <= 30) {
                    fProbability = 1.1;
                } else if (foir > 30 && foir <= 40) {
                    fProbability = 1.0;
                } else if(foir > 40 && foir <= 50){
                    fProbability = 0.9;
                }else if(foir > 50 && foir <= 60){
                    fProbability = 0.8;
                }else if(foir > 60 && foir <= 70){
                    fProbability = 0.7;
                } else {
                    fProbability = 0.0;
                }
                inputVariables.put("fProbability", fProbability);
            }
            if (salaryObj instanceof String) {
                long takeHomeSalaryMonthly = Long.parseLong((String) salaryObj);
                if(takeHomeSalaryMonthly == 20000) {
                    sProbability = 1.0;
                } else if (takeHomeSalaryMonthly >= (20000+(20000*0.4))) {
                    sProbability = 1.3;
                } else if(takeHomeSalaryMonthly >= (20000+(20000*0.2)) && takeHomeSalaryMonthly < (20000+(20000*0.4))) {
                    sProbability = 1.1;
                }
                inputVariables.put("sProbability", sProbability);
            }
            if (companyObj instanceof String) {
                String companyCategory = (String) companyObj;
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
                inputVariables.put("comProbability", comProbability);
            }
            if (currentJobObj instanceof String) {
                int jobStability = Integer.parseInt((String) currentJobObj);
                if (jobStability >=1) {
                    jProbability = 1.0;
                } else {
                    jProbability = 0.0;
                }
                inputVariables.put("jProbability", jProbability);
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
            System.out.println("Final Probability Percentage for Yes bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for Finnable bank
        if (bankName.equals("Finnable")) {
            if (creditScoreObj instanceof String) {
                String creditScore = (String) creditScoreObj;
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
                inputVariables.put("csProbability", csProbability);
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience >= 36) {
                    expProbability = 1.2;
                } else if(experience >=24 && experience > 12) {
                    expProbability = 1.1;
                }else if(experience == 12) {
                    expProbability = 1.0;
                }else {
                    expProbability = 0.0;
                }
                inputVariables.put("expProbability", expProbability);
            }
            if (amountObj instanceof String) {
                long loanAmount = Long.parseLong((String) amountObj);
                if (loanAmount >= 200000 && loanAmount <= 500000) {
                    laProbability = 1.0;
                } else if(loanAmount > 500000 && loanAmount <= 1000000) {
                    laProbability = 0.8;
                }else {
                    laProbability = 0.5;
                }
                inputVariables.put("laProbability", laProbability);
            }
            if (residentObj instanceof String) {
                String residentType = (String) residentObj;
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
                inputVariables.put("resProbability", resProbability);
            }
            if (foirObj instanceof Integer) {
                int foir = (int) foirObj;
                if (foir <= 20) {
                    fProbability = 1.2;
                } else if (foir > 20 && foir <= 30) {
                    fProbability = 1.1;
                } else if (foir > 30 && foir <= 40) {
                    fProbability = 1.0;
                } else if(foir > 40 && foir <= 50) {
                    fProbability = 0.9;
                }else if(foir > 50 && foir <= 60) {
                    fProbability = 0.8;
                } else if (foir > 60 && foir <=75) {
                    fProbability = 0.7;
                } else {
                    fProbability = 0.6;
                }
                inputVariables.put("fProbability", fProbability);
            }
            if (salaryObj instanceof String) {
                long takeHomeSalaryMonthly = Long.parseLong((String) salaryObj);
                if(takeHomeSalaryMonthly == 20000) {
                    sProbability = 1.0;
                } else if (takeHomeSalaryMonthly >= (20000+(20000*0.4))) {
                    sProbability = 1.3;
                } else if(takeHomeSalaryMonthly >= (20000+(20000*0.2)) && takeHomeSalaryMonthly < (20000+(20000*0.4))) {
                    sProbability = 1.1;
                }
                inputVariables.put("sProbability", sProbability);
            }
            if (companyObj instanceof String) {
                String companyCategory = (String) companyObj;
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
                inputVariables.put("comProbability", comProbability);
            }
            if (currentJobObj instanceof String) {
                int jobStability = Integer.parseInt((String) currentJobObj);
                if (jobStability >=12) {
                    jProbability = 1.0;
                } else {
                    jProbability = 0.0;
                }
                inputVariables.put("jProbability", jProbability);
            }
            int   probability = (int) (csProbability + expProbability + laProbability + resProbability + fProbability + comProbability + sProbability + jProbability);
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
            System.out.println("Final Probability Percentage for Finnable Bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for Incred bank
        if (bankName.equals("Incred")) {
            if (creditScoreObj instanceof String) {
                String creditScore = (String) creditScoreObj;
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
                inputVariables.put("csProbability", csProbability);
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience >= 12) {
                    expProbability = 1.1;
                }else if(experience == 12) {
                    expProbability = 1.0;
                } else {
                    expProbability = 0.0;
                }
                inputVariables.put("expProbability", expProbability);
            }
            if (amountObj instanceof String) {
                long loanAmount = Long.parseLong((String) amountObj);
                if (loanAmount >=100000 && loanAmount <= 500000) {
                    laProbability = 1.0;
                } else if (loanAmount > 500000 && loanAmount <= 1000000) {
                    laProbability = 0.9;
                } else{
                    laProbability = 0.0;
                }
                inputVariables.put("laProbability", laProbability);
            }
            if (residentObj instanceof String) {
                String residentType = (String) residentObj;
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
                inputVariables.put("resProbability", resProbability);
            }
            if (foirObj instanceof Integer) {
                int foir = (int) foirObj;
                if (foir <= 20) {
                    fProbability = 1.2;
                } else if (foir > 20 && foir <= 30) {
                    fProbability = 1.1;
                } else if (foir > 30 && foir <= 40) {
                    fProbability = 1.0;
                } else if (foir > 40 && foir <= 50){
                    fProbability = 0.9;
                } else if (foir > 50 && foir <= 75){
                    fProbability = 0.7;
                }else {
                    fProbability = 0.0;
                }
                inputVariables.put("fProbability", fProbability);
            }
            if (salaryObj instanceof String) {
                long takeHomeSalaryMonthly = Long.parseLong((String) salaryObj);
                if(takeHomeSalaryMonthly == 15000) {
                    sProbability = 1.0;
                } else if (takeHomeSalaryMonthly >= (15000+(15000*0.4))) {
                    sProbability = 1.3;
                } else if(takeHomeSalaryMonthly >= (15000+(15000*0.2)) && takeHomeSalaryMonthly < (15000+(15000*0.4))) {
                    sProbability = 1.1;
                }
                inputVariables.put("sProbability", sProbability);
            }
            if (companyObj instanceof String) {
                String companyCategory = (String) companyObj;
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
                inputVariables.put("comProbability", comProbability);
            }
            if (currentJobObj instanceof String) {
                int jobStability = Integer.parseInt((String) currentJobObj);
                if (jobStability >=3) {
                    jProbability = 1.0;
                } else {
                    jProbability = 0.0;
                }
                inputVariables.put("jProbability", jProbability);
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
            System.out.println("Final Probability Percentage for Incred bank: " + bankProbability);
            return bankProbability;
        }
        //validating probability for Paysense Bank
        if (bankName.equals("Paysense")) {
            if (creditScoreObj instanceof String) {
                String creditScore = (String) creditScoreObj;
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
                inputVariables.put("csProbability", csProbability);
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience >= 36) {
                    expProbability = 1.1;
                } else if (experience >= 4 || experience < 36) {
                    expProbability = 1.0;
                } else if (experience == 4) {
                    expProbability = 0.9;
                }else {
                    expProbability = 0.0;
                }
                inputVariables.put("expProbability", expProbability);
            }
            if (amountObj instanceof String) {
                long loanAmount = Long.parseLong((String) amountObj);
                if (loanAmount >= 5000 && loanAmount <= 100000) {
                    laProbability = 1.1;
                } else if (loanAmount > 100000 && loanAmount <= 300000) {
                    laProbability = 1.0;
                } else if (loanAmount > 300000 && loanAmount <= 500000) {
                    laProbability = 0.9;
                } else if (loanAmount > 500000 && loanAmount <= 750000) {
                    laProbability = 0.8;
                } else {
                    laProbability = 0.0;
                }
                inputVariables.put("laProbability", laProbability);
            }
            if (residentObj instanceof String) {
                String residentType = (String) residentObj;
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
                inputVariables.put("resProbability", resProbability);
            }
            if (foirObj instanceof Integer) {
                int foir = (int) foirObj;
                if (foir <= 20) {
                    fProbability = 1.2;
                } else if (foir > 20 && foir <= 30) {
                    fProbability = 1.1;
                } else if (foir > 30 && foir <= 40) {
                    fProbability = 1.0;
                } else if(foir > 40 && foir <= 50) {
                    fProbability = 0.9;
                }else if(foir > 50 && foir <= 60) {
                    fProbability = 0.8;
                }else if(foir > 60 && foir <= 75) {
                    fProbability = 0.7;
                }else {
                    fProbability = 0.0;
                }
                inputVariables.put("fProbability", fProbability);
            }
            if (salaryObj instanceof String) {
                long takeHomeSalaryMonthly = Long.parseLong((String) salaryObj);
                if(takeHomeSalaryMonthly == 20000) {
                    sProbability = 1.0;
                } else if (takeHomeSalaryMonthly >= (20000+(20000*0.4))) {
                    sProbability = 1.3;
                } else if(takeHomeSalaryMonthly >= (20000+(20000*0.2)) && takeHomeSalaryMonthly < (20000+(20000*0.4))) {
                    sProbability = 1.1;
                }
                inputVariables.put("sProbability", sProbability);
            }
            if (companyObj instanceof String) {
                String companyCategory = (String) companyObj;
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
                inputVariables.put("comProbability", comProbability);
            }
            if (currentJobObj instanceof String) {
                int jobStability = Integer.parseInt((String) currentJobObj);
                if (jobStability >=3) {
                    jProbability = 1.0;
                } else {
                    jProbability = 0.0;
                }
                inputVariables.put("jProbability", jProbability);
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
            System.out.println("Final Probability Percentage for Paysense bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for IndusInd bank
        if (bankName.equals("IndusInd")) {
            if (creditScoreObj instanceof String) {
                String creditScore = (String) creditScoreObj;
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
                inputVariables.put("csProbability", csProbability);
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
                inputVariables.put("expProbability", expProbability);
            }
            if (amountObj instanceof String) {
                long loanAmount = Long.parseLong((String) amountObj);
                if (loanAmount >= 100000 && loanAmount <= 500000) {
                    laProbability = 1.0;
                } else if (loanAmount > 500000 && loanAmount <= 1000000) {
                    laProbability = 0.9;
                } else if (loanAmount > 1000000 && loanAmount <= 2000000) {
                    laProbability = 0.8;
                } else if (loanAmount > 2000000 && loanAmount <= 3000000) {
                    laProbability = 0.7;
                } else if(loanAmount > 3000000 && loanAmount <= 4000000){
                    laProbability = 0.6;
                } else if (loanAmount > 4000000 && loanAmount <= 5000000) {
                    laProbability = 0.5;
                } else {
                    laProbability = 0.0;
                }
                inputVariables.put("laProbability", laProbability);
            }
            if (residentObj instanceof String) {
                String residentType = (String) residentObj;
                switch (residentType) {
                    case "Owned":
                        resProbability = 1.1;
                        break;
                    case "Rented":
                        resProbability = 0.9;
                        break;
                }
                inputVariables.put("resProbability", resProbability);
            }
            if (foirObj instanceof Integer) {
                int foir = (int) foirObj;
                if (foir <= 20) {
                    fProbability = 1.2;
                } else if (foir > 20 && foir <= 30) {
                    fProbability = 1.1;
                } else if (foir > 30 && foir <= 40) {
                    fProbability = 1.0;
                } else if(foir > 40 && foir <= 50){
                    fProbability = 0.9;
                } else if(foir > 50 && foir <=60){
                    fProbability = 0.8;
                } else if(foir > 60 && foir <= 70) {
                    fProbability = 0.7;
                }else {
                    fProbability = 0.0;
                }
                inputVariables.put("fProbability", fProbability);
            }
            if (salaryObj instanceof String) {
                long takeHomeSalaryMonthly = Long.parseLong((String) salaryObj);
                if(takeHomeSalaryMonthly == 25000) {
                    sProbability = 1.0;
                } else if (takeHomeSalaryMonthly >= (25000+(25000*0.4))) {
                    sProbability = 1.3;
                } else if(takeHomeSalaryMonthly >= (25000+(25000*0.2)) && takeHomeSalaryMonthly < (25000+(25000*0.4))) {
                    sProbability = 1.1;
                }
                inputVariables.put("sProbability", sProbability);
            }
            if (companyObj instanceof String) {
                String companyCategory = (String) companyObj;
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
                inputVariables.put("comProbability", comProbability);
            }
            if (currentJobObj instanceof String) {
                int jobStability = Integer.parseInt((String) currentJobObj);
                if (jobStability >=1) {
                    jProbability = 1.0;
                } else {
                    jProbability = 0.0;
                }
                inputVariables.put("jProbability", jProbability);
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
            System.out.println("Final Probability Percentage for IndusInd Bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for SCB bank
        if (bankName.equals("SCB")) {
            if (creditScoreObj instanceof String) {
                String creditScore = (String) creditScoreObj;
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
                inputVariables.put("csProbability", csProbability);
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
                inputVariables.put("expProbability", expProbability);
            }
            if (amountObj instanceof String) {
                long loanAmount = Long.parseLong((String) amountObj);
                if (loanAmount >=100000 && loanAmount <= 500000) {
                    laProbability = 1.0;
                } else if (loanAmount > 500000 && loanAmount <= 1000000) {
                    laProbability = 0.9;
                } else if (loanAmount > 1000000 && loanAmount <= 2000000) {
                    laProbability = 0.8;
                } else if(loanAmount > 2000000 && loanAmount <= 3000000){
                    laProbability = 0.7;
                } else if(loanAmount > 3000000 && loanAmount <= 4000000) {
                    laProbability = 0.6;
                } else if(loanAmount > 4000000 && loanAmount <= 5000000) {
                    laProbability = 0.5;
                } else {
                    laProbability = 0.0;
                }
                inputVariables.put("laProbability", laProbability);
            }
            if (residentObj instanceof String) {
                String residentType = (String) residentObj;
                switch (residentType) {
                    case "Owned":
                        resProbability = 1.1;
                        break;
                    case "Rented":
                        resProbability = 0.9;
                        break;
                }
                inputVariables.put("resProbability", resProbability);
            }
            if (foirObj instanceof Integer) {
                int foir = (int) foirObj;
                if (foir <= 20) {
                    fProbability = 1.2;
                } else if (foir > 20 && foir <= 30) {
                    fProbability = 1.1;
                } else if (foir > 30 && foir <= 40) {
                    fProbability = 1.0;
                } else if (foir > 40 && foir <= 50){
                    fProbability = 0.9;
                } else if (foir > 50 && foir <= 55){
                    fProbability = 0.7;
                }else {
                    fProbability = 0.0;
                }
                inputVariables.put("fProbability", fProbability);
            }
            if (salaryObj instanceof String) {
                long takeHomeSalaryMonthly = Long.parseLong((String) salaryObj);
                if(takeHomeSalaryMonthly == 30000) {
                    sProbability = 1.0;
                } else if (takeHomeSalaryMonthly >= (30000+(30000*0.4))) {
                    sProbability = 1.3;
                } else if(takeHomeSalaryMonthly >= (30000+(30000*0.2)) && takeHomeSalaryMonthly < (30000+(30000*0.4))) {
                    sProbability = 1.1;
                }
                inputVariables.put("sProbability", sProbability);
            }
            if (companyObj instanceof String) {
                String companyCategory = (String) companyObj;
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
                inputVariables.put("comProbability", comProbability);
            }
            if (currentJobObj instanceof String) {
                int jobStability = Integer.parseInt((String) currentJobObj);
                if (jobStability >=1) {
                    jProbability = 1.0;
                } else {
                    jProbability = 0.0;
                }
                inputVariables.put("jProbability", jProbability);
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
            System.out.println("Final Probability Percentage for SCB bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for Kotak bank
        if (bankName.equals("Kotak")) {
            if (creditScoreObj instanceof String) {
                String creditScore = (String) creditScoreObj;
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
                inputVariables.put("csProbability", csProbability);
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience >= 36) {
                    expProbability = 1.1;
                } else if (experience == 36) {
                    expProbability = 0.9;
                }else {
                    expProbability = 0.0;
                }
                inputVariables.put("expProbability", expProbability);
            }
            if (amountObj instanceof String) {
                long loanAmount = Long.parseLong((String) amountObj);
                if (loanAmount >=100000 && loanAmount <= 500000) {
                    laProbability = 1.0;
                } else if (loanAmount > 500000 && loanAmount <= 1000000) {
                    laProbability = 0.9;
                } else if (loanAmount > 1000000 && loanAmount <= 2000000) {
                    laProbability = 0.8;
                } else if(loanAmount > 2000000 && loanAmount <= 3000000){
                    laProbability = 0.7;
                } else if(loanAmount > 3000000 && loanAmount <= 4000000) {
                    laProbability = 0.6;
                } else if(loanAmount > 4000000 && loanAmount <= 5000000) {
                    laProbability = 0.5;
                } else {
                    laProbability = 0.0;
                }
                inputVariables.put("laProbability", laProbability);
            }
            if (residentObj instanceof String) {
                String residentType = (String) residentObj;
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
                inputVariables.put("resProbability", resProbability);
            }
            if (foirObj instanceof Integer) {
                int foir = (int) foirObj;
                if (foir <= 20) {
                    fProbability = 1.2;
                } else if (foir > 20 && foir <= 30) {
                    fProbability = 1.1;
                } else if (foir > 30 && foir <= 40) {
                    fProbability = 1.0;
                } else if (foir > 40 && foir <= 50){
                    fProbability = 0.9;
                } else if (foir > 50 && foir <= 60){
                    fProbability = 0.7;
                }else if(foir > 60 && foir <= 70){
                    fProbability = 0.6;
                } else {
                    fProbability = 0.0;
                }
                inputVariables.put("fProbability", fProbability);
            }
            if (salaryObj instanceof String) {
                long takeHomeSalaryMonthly = Long.parseLong((String) salaryObj);
                if(takeHomeSalaryMonthly == 30000) {
                    sProbability = 1.0;
                } else if (takeHomeSalaryMonthly >= (30000+(30000*0.4))) {
                    sProbability = 1.3;
                } else if(takeHomeSalaryMonthly >= (30000+(30000*0.2)) && takeHomeSalaryMonthly < (30000+(30000*0.4))) {
                    sProbability = 1.1;
                }
                inputVariables.put("sProbability", sProbability);
            }
            if (companyObj instanceof String) {
                String companyCategory = (String) companyObj;
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
                inputVariables.put("comProbability", comProbability);
            }
            if (currentJobObj instanceof String) {
                int jobStability = Integer.parseInt((String) currentJobObj);
                if (jobStability >=1) {
                    jProbability = 1.0;
                } else {
                    jProbability = 0.0;
                }
                inputVariables.put("jProbability", jProbability);
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
            System.out.println("Final Probability Percentage for Kotak bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for Hero Fincorp bank
        if (bankName.equals("Hero Fincorp")) {
            if (creditScoreObj instanceof String) {
                String creditScore = (String) creditScoreObj;
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
                inputVariables.put("csProbability", csProbability);
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience >= 36) {
                    expProbability = 1.1;
                } else if (experience >= 12 && experience < 36) {
                    expProbability = 1.0;
                }else if(experience == 12){
                    expProbability = 0.9;
                } else {
                    expProbability = 0.0;
                }
                inputVariables.put("expProbability", expProbability);
            }
            if (amountObj instanceof String) {
                long loanAmount = Long.parseLong((String) amountObj);
                if (loanAmount >=100000 && loanAmount <= 500000) {
                    laProbability = 1.0;
                } else {
                    laProbability = 0.0;
                }
                inputVariables.put("laProbability", laProbability);
            }
            if (residentObj instanceof String) {
                String residentType = (String) residentObj;
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
                inputVariables.put("resProbability", resProbability);
            }
            if (foirObj instanceof Integer) {
                int foir = (int) foirObj;
                if (foir <= 20) {
                    fProbability = 1.2;
                } else if (foir > 20 && foir <= 30) {
                    fProbability = 1.1;
                } else if (foir > 30 && foir <= 40) {
                    fProbability = 1.0;
                } else if (foir > 40 && foir <= 50){
                    fProbability = 0.9;
                } else if (foir > 50 && foir <= 60){
                    fProbability = 0.7;
                }else if(foir > 60 && foir <= 75){
                    fProbability = 0.6;
                } else {
                    fProbability = 0.0;
                }
                inputVariables.put("fProbability", fProbability);
            }
            if (salaryObj instanceof String) {
                long takeHomeSalaryMonthly = Long.parseLong((String) salaryObj);
                if(takeHomeSalaryMonthly == 15000) {
                    sProbability = 1.0;
                } else if (takeHomeSalaryMonthly >= (15000+(15000*0.4))) {
                    sProbability = 1.3;
                } else if(takeHomeSalaryMonthly >= (15000+(15000*0.2)) && takeHomeSalaryMonthly < (15000+(15000*0.4))) {
                    sProbability = 1.1;
                }
                inputVariables.put("sProbability", sProbability);
            }
            if (companyObj instanceof String) {
                String companyCategory = (String) companyObj;
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
                inputVariables.put("comProbability", comProbability);
            }
            if (currentJobObj instanceof String) {
                int jobStability = Integer.parseInt((String) currentJobObj);
                if (jobStability >=6) {
                    jProbability = 1.0;
                } else {
                    jProbability = 0.0;
                }
                inputVariables.put("jProbability", jProbability);
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
            System.out.println("Final Probability Percentage for Hero Fincorp bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for Muthoot Finance bank
        if (bankName.equals("Muthoot Finance")) {
            if (creditScoreObj instanceof String) {
                String creditScore = (String) creditScoreObj;
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
                inputVariables.put("csProbability", csProbability);
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience > 36) {
                    expProbability = 1.1;
                }else if(experience == 36){
                    expProbability = 0.9;
                } else {
                    expProbability = 0.0;
                }
                inputVariables.put("expProbability", expProbability);
            }
            if (amountObj instanceof String) {
                long loanAmount = Long.parseLong((String) amountObj);
                if (loanAmount >=100000 && loanAmount <= 500000) {
                    laProbability = 1.0;
                } else if(loanAmount > 500000 && loanAmount <= 750000) {
                    laProbability = 0.9;
                } else if (loanAmount > 750000 && loanAmount <= 1000000) {
                    laProbability = 0.8;
                } else if (loanAmount > 1000000 && loanAmount <= 1250000) {
                    laProbability = 0.7;
                } else if(loanAmount > 1250000 && loanAmount <= 1500000) {
                    laProbability  = 0.6;
                } else {
                    laProbability = 0.0;
                }
                inputVariables.put("laProbability", laProbability);
            }
            if (residentObj instanceof String) {
                String residentType = (String) residentObj;
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
                inputVariables.put("resProbability", resProbability);
            }
            if (foirObj instanceof Integer) {
                int foir = (int) foirObj;
                if (foir <= 20) {
                    fProbability = 1.2;
                } else if (foir > 20 && foir <= 30) {
                    fProbability = 1.1;
                } else if (foir > 30 && foir <= 40) {
                    fProbability = 1.0;
                } else if (foir > 40 && foir <= 50){
                    fProbability = 0.9;
                } else if (foir > 50 && foir <= 60){
                    fProbability = 0.7;
                }else if(foir > 60 && foir <= 70){
                    fProbability = 0.6;
                } else {
                    fProbability = 0.0;
                }
                inputVariables.put("fProbability", fProbability);
            }
            if (salaryObj instanceof String) {
                long takeHomeSalaryMonthly = Long.parseLong((String) salaryObj);
                if(takeHomeSalaryMonthly == 20000) {
                    sProbability = 1.0;
                } else if (takeHomeSalaryMonthly >= (20000+(20000*0.4))) {
                    sProbability = 1.3;
                } else if(takeHomeSalaryMonthly >= (20000+(20000*0.2)) && takeHomeSalaryMonthly < (20000+(20000*0.4))) {
                    sProbability = 1.1;
                }
                inputVariables.put("sProbability", sProbability);
            }
            if (companyObj instanceof String) {
                String companyCategory = (String) companyObj;
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
                inputVariables.put("comProbability", comProbability);
            }
            if (currentJobObj instanceof String) {
                int jobStability = Integer.parseInt((String) currentJobObj);
                if (jobStability >=1) {
                    jProbability = 1.0;
                } else {
                    jProbability = 0.0;
                }
                inputVariables.put("jProbability", jProbability);
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
            System.out.println("Final Probability Percentage for Muthoot Finance bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for Finzy bank
        if (bankName.equals("Finzy")) {
            if (creditScoreObj instanceof String) {
                String creditScore = (String) creditScoreObj;
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
                inputVariables.put("csProbability", csProbability);
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience > 36) {
                    expProbability = 1.1;
                }else if(experience > 12 && experience <= 36){
                    expProbability = 1.0;
                } else if(experience == 12){
                    expProbability = 0.9;
                } else {
                    expProbability = 0.0;
                }
                inputVariables.put("expProbability", expProbability);
            }
            if (amountObj instanceof String) {
                long loanAmount = Long.parseLong((String) amountObj);
                if (loanAmount >=50000 && loanAmount <= 300000) {
                    laProbability = 1.0;
                } else if(loanAmount > 300000 && loanAmount <= 500000) {
                    laProbability = 0.9;
                } else if (loanAmount > 500000 && loanAmount <= 750000) {
                    laProbability = 0.8;
                } else if (loanAmount > 750000 && loanAmount <= 1000000) {
                    laProbability = 0.7;
                } else {
                    laProbability = 0.0;
                }
                inputVariables.put("laProbability", laProbability);
            }
            if (residentObj instanceof String) {
                String residentType = (String) residentObj;
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
                inputVariables.put("resProbability", resProbability);
            }
            if (foirObj instanceof Integer) {
                int foir = (int) foirObj;
                if (foir <= 20) {
                    fProbability = 1.2;
                } else if (foir > 20 && foir <= 30) {
                    fProbability = 1.1;
                } else if (foir > 30 && foir <= 40) {
                    fProbability = 1.0;
                } else if (foir > 40 && foir <= 50){
                    fProbability = 0.9;
                } else if (foir > 50 && foir <= 60){
                    fProbability = 0.7;
                }else if(foir > 60 && foir <= 70){
                    fProbability = 0.6;
                } else if(foir > 70 && foir <= 80) {
                    fProbability = 0.5;
                } else {
                    fProbability = 0.0;
                }
                inputVariables.put("fProbability", fProbability);
            }
            if (salaryObj instanceof String) {
                long takeHomeSalaryMonthly = Long.parseLong((String) salaryObj);
                if(takeHomeSalaryMonthly == 35000) {
                    sProbability = 1.0;
                } else if (takeHomeSalaryMonthly >= (35000+(35000*0.4))) {
                    sProbability = 1.3;
                } else if(takeHomeSalaryMonthly >= (35000+(35000*0.2)) && takeHomeSalaryMonthly < (35000+(35000*0.4))) {
                    sProbability = 1.1;
                }
                inputVariables.put("sProbability", sProbability);
            }
            if (companyObj instanceof String) {
                String companyCategory = (String) companyObj;
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
                inputVariables.put("comProbability", comProbability);
            }
            if (currentJobObj instanceof String) {
                int jobStability = Integer.parseInt((String) currentJobObj);
                if (jobStability >=1) {
                    jProbability = 1.0;
                } else {
                    jProbability = 0.0;
                }
                inputVariables.put("jProbability", jProbability);
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
            System.out.println("Final Probability Percentage for Finzy bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for Credit Vidhya bank
        if (bankName.equals("Credit Vidya")) {
            if (creditScoreObj instanceof String) {
                String creditScore = (String) creditScoreObj;
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
                inputVariables.put("csProbability", csProbability);
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience > 36) {
                    expProbability = 1.1;
                }else if(experience > 3 && experience <= 36){
                    expProbability = 1.0;
                } else if(experience == 3) {
                    expProbability = 0.9;
                }else {
                    expProbability = 0.0;
                }
                inputVariables.put("expProbability", expProbability);
            }
            if (amountObj instanceof String) {
                long loanAmount = Long.parseLong((String) amountObj);
                if (loanAmount >=25000 && loanAmount <= 100000) {
                    laProbability = 1.0;
                } else if(loanAmount > 100000 && loanAmount <= 200000) {
                    laProbability = 0.9;
                } else if (loanAmount > 200000 && loanAmount <= 300000) {
                    laProbability = 0.8;
                } else {
                    laProbability = 0.0;
                }
                inputVariables.put("laProbability", laProbability);
            }
            if (residentObj instanceof String) {
                String residentType = (String) residentObj;
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
                inputVariables.put("resProbability", resProbability);
            }
            if (foirObj instanceof Integer) {
                int foir = (int) foirObj;
                if (foir <= 20) {
                    fProbability = 1.2;
                } else if (foir > 20 && foir <= 30) {
                    fProbability = 1.1;
                } else if (foir > 30 && foir <= 40) {
                    fProbability = 1.0;
                } else if (foir > 40 && foir <= 50){
                    fProbability = 0.9;
                } else if (foir > 50 && foir <= 60){
                    fProbability = 0.7;
                }else if(foir > 60 && foir <= 75){
                    fProbability = 0.6;
                } else {
                    fProbability = 0.0;
                }
                inputVariables.put("fProbability", fProbability);
            }
            if (salaryObj instanceof String) {
                long takeHomeSalaryMonthly = Long.parseLong((String) salaryObj);
                if(takeHomeSalaryMonthly == 15000) {
                    sProbability = 1.0;
                } else if (takeHomeSalaryMonthly >= (15000+(15000*0.4))) {
                    sProbability = 1.3;
                } else if(takeHomeSalaryMonthly >= (15000+(15000*0.2)) && takeHomeSalaryMonthly < (15000+(15000*0.4))) {
                    sProbability = 1.1;
                }
                inputVariables.put("sProbability", sProbability);
            }
            if (companyObj instanceof String) {
                String companyCategory = (String) companyObj;
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
                inputVariables.put("comProbability", comProbability);
            }
            if (currentJobObj instanceof String) {
                int jobStability = Integer.parseInt((String) currentJobObj);
                if (jobStability >=3) {
                    jProbability = 1.0;
                } else {
                    jProbability = 0.0;
                }
                inputVariables.put("jProbability", jProbability);
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
            System.out.println("Final Probability Percentage for Credit Vidya bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for ABFL bank
        if (bankName.equals("ABFL")) {
            if (creditScoreObj instanceof String) {
                String creditScore = (String) creditScoreObj;
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
                inputVariables.put("csProbability", csProbability);
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience > 36) {
                    expProbability = 1.1;
                }else if(experience == 36){
                    expProbability = 1.0;
                } else {
                    expProbability = 0.0;
                }
                inputVariables.put("expProbability", expProbability);
            }
            if (amountObj instanceof String) {
                long loanAmount = Long.parseLong((String) amountObj);
                if (loanAmount >=100000 && loanAmount <= 500000) {
                    laProbability = 1.0;
                } else if(loanAmount > 500000 && loanAmount <= 1000000) {
                    laProbability = 0.9;
                } else if (loanAmount > 1000000 && loanAmount <= 2000000) {
                    laProbability = 0.8;
                } else if (loanAmount > 2000000 && loanAmount <= 3000000) {
                    laProbability = 0.7;
                } else if(loanAmount > 3000000 && loanAmount <= 4000000) {
                    laProbability  = 0.6;
                } else if(loanAmount > 4000000 && loanAmount <= 5000000){
                    laProbability = 0.5;
                } else {
                    laProbability = 0.0;
                }
                inputVariables.put("laProbability", laProbability);
            }
            if (residentObj instanceof String) {
                String residentType = (String) residentObj;
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
                inputVariables.put("resProbability", resProbability);
            }
            if (foirObj instanceof Integer) {
                int foir = (int) foirObj;
                if (foir <= 20) {
                    fProbability = 1.2;
                } else if (foir > 20 && foir <= 30) {
                    fProbability = 1.1;
                } else if (foir > 30 && foir <= 40) {
                    fProbability = 1.0;
                } else if (foir > 40 && foir <= 50){
                    fProbability = 0.9;
                } else if (foir > 50 && foir <= 60){
                    fProbability = 0.7;
                }else if(foir > 60 && foir <= 70){
                    fProbability = 0.6;
                } else {
                    fProbability = 0.0;
                }
                inputVariables.put("fProbability", fProbability);
            }
            if (salaryObj instanceof String) {
                long takeHomeSalaryMonthly = Long.parseLong((String) salaryObj);
                if(takeHomeSalaryMonthly == 20000) {
                    sProbability = 1.0;
                } else if (takeHomeSalaryMonthly >= (20000+(20000*0.4))) {
                    sProbability = 1.3;
                } else if(takeHomeSalaryMonthly >= (20000+(20000*0.2)) && takeHomeSalaryMonthly < (20000+(20000*0.4))) {
                    sProbability = 1.1;
                }
                inputVariables.put("sProbability", sProbability);
            }
            if (companyObj instanceof String) {
                String companyCategory = (String) companyObj;
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
                inputVariables.put("comProbability", comProbability);
            }
            if (currentJobObj instanceof String) {
                int jobStability = Integer.parseInt((String) currentJobObj);
                if (jobStability >=1) {
                    jProbability = 1.0;
                } else {
                    jProbability = 0.0;
                }
                inputVariables.put("jProbability", jProbability);
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
            System.out.println("Final Probability Percentage for ABFL bank: " + bankProbability);
            return bankProbability;
        }
        //Validating probability for Piramal bank
        if (bankName.equals("Piramal")) {
            if (creditScoreObj instanceof String) {
                String creditScore = (String) creditScoreObj;
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
                inputVariables.put("csProbability", csProbability);
            }
            if (experienceYearsObj instanceof String && experienceMonthsObj instanceof String) {
                long experienceYears = Long.parseLong((String) experienceYearsObj);
                long experienceMonths = Long.parseLong((String) experienceMonthsObj);
                long experience = experienceYears * 12 + experienceMonths;
                if (experience > 36) {
                    expProbability = 1.1;
                }else if(experience == 36){
                    expProbability = 1.0;
                } else {
                    expProbability = 0.0;
                }
                inputVariables.put("expProbability", expProbability);
            }
            if (amountObj instanceof String) {
                long loanAmount = Long.parseLong((String) amountObj);
                if (loanAmount >=100000 && loanAmount <= 300000) {
                    laProbability = 1.0;
                } else if(loanAmount > 300000 && loanAmount <= 500000) {
                    laProbability = 0.9;
                } else if (loanAmount > 500000 && loanAmount <= 700000) {
                    laProbability = 0.8;
                } else if (loanAmount > 700000 && loanAmount <= 1000000) {
                    laProbability = 0.7;
                } else if(loanAmount > 1000000 && loanAmount <= 1200000) {
                    laProbability  = 0.6;
                } else {
                    laProbability = 0.0;
                }
                inputVariables.put("laProbability", laProbability);
            }
            if (residentObj instanceof String) {
                String residentType = (String) residentObj;
                switch (residentType) {
                    case "Owned":
                        resProbability = 1.1;
                        break;
                    case "Rented":
                        resProbability = 0.9;
                        break;
                }
                inputVariables.put("resProbability", resProbability);
            }
            if (foirObj instanceof Integer) {
                int foir = (int) foirObj;
                if (foir <= 20) {
                    fProbability = 1.2;
                } else if (foir > 20 && foir <= 30) {
                    fProbability = 1.1;
                } else if (foir > 30 && foir <= 40) {
                    fProbability = 1.0;
                } else if (foir > 40 && foir <= 50){
                    fProbability = 0.9;
                } else if (foir > 50 && foir <= 60){
                    fProbability = 0.7;
                }else if(foir > 60 && foir <= 75){
                    fProbability = 0.6;
                } else {
                    fProbability = 0.0;
                }
                inputVariables.put("fProbability", fProbability);
            }
            if (salaryObj instanceof String) {
                long takeHomeSalaryMonthly = Long.parseLong((String) salaryObj);
                if(takeHomeSalaryMonthly == 28000) {
                    sProbability = 1.0;
                } else if (takeHomeSalaryMonthly >= (28000+(28000*0.4))) {
                    sProbability = 1.3;
                } else if(takeHomeSalaryMonthly >= (28000+(28000*0.2)) && takeHomeSalaryMonthly < (28000+(28000*0.4))) {
                    sProbability = 1.1;
                }
                inputVariables.put("sProbability", sProbability);
            }
            if (companyObj instanceof String) {
                String companyCategory = (String) companyObj;
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
                inputVariables.put("comProbability", comProbability);
            }
            if (currentJobObj instanceof String) {
                int jobStability = Integer.parseInt((String) currentJobObj);
                if (jobStability >=6) {
                    jProbability = 1.0;
                } else {
                    jProbability = 0.0;
                }
                inputVariables.put("jProbability", jProbability);
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
            System.out.println("Final Probability Percentage for Piramal bank: " + bankProbability);
            return bankProbability;
        }
        return bankProbability;
    }
}