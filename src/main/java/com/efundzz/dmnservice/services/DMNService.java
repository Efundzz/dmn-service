package com.efundzz.dmnservice.services;

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

import com.efundzz.dmnservice.Exception.ValidationException;

import static java.time.format.DateTimeFormatter.*;

@Service
public class DMNService {
    @Autowired
    DMNEvaluator dmnEvaluator;
    private final Logger logger = LoggerFactory.getLogger(DMNService.class);

    public List<String> evaluateDecision(Map<String, Object> inputVariables) {
        String decisionKey = "PL_Decisioning";
        logger.info("Evaluating decision with key: {}", decisionKey);
        logger.debug("Input variables: {}", inputVariables);
        // TODO: Validate input variables
        // Validating input variables
        validateInputVariables(inputVariables);
        List<String> decisionResult = dmnEvaluator.evaluateDecision(decisionKey, inputVariables);
        logger.info("DMN evaluation completed successfully.");
        return decisionResult;
    }

    // Creating a method validateInputVariables for calling each variable validating
    // method
    private void validateInputVariables(Map<String, Object> inputVariables) {
        validateCreditScore(inputVariables);
        validateHomeSalary(inputVariables);
        validateAge(inputVariables);
        validateExperience(inputVariables);
        validateResidentType(inputVariables);
        validateLoanAmount(inputVariables);
        validateFoir(inputVariables);
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
                if (loanAmount > 1000000) {
                    Map<String, String> fieldErrors = new HashMap<>();
                    fieldErrors.put("creditScore", "No bank is offering that amount for your credit grade..");
                    throw new ValidationException(fieldErrors);
                }
            }
        } catch (ValidationException e) {
            throw e;
        }
    }

    //validate HomeSalary method
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
                if (age < 21 || age > 60) {
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
                if (experience < 3) {
                    Map<String, String> fieldErrors = new HashMap<>();
                    fieldErrors.put("experience", "You are not eligible for you Experience concern.");
                    throw new ValidationException(fieldErrors);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("'experienceYears' and 'experienceMonths' must be valid integers.");
            }
        }
    }

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
                || residentType.equalsIgnoreCase("PG or Staying with Friends")
                || residentType.equalsIgnoreCase("Company Accommodation") || residentType.equalsIgnoreCase("Others"))) {
            throw new IllegalArgumentException("'ResidentType' must be in the given list");
        }
    }

    private void validateLoanAmount(Map<String, Object> inputVariables) {
        Object amountObj = inputVariables.get("loanAmount");
        if (amountObj == null) {
            throw new IllegalArgumentException("Missing 'LoanAmount' in input variables.");
        }

        if (amountObj instanceof String) {
            try {
                long loanAmount = Long.parseLong((String) amountObj);
                if (loanAmount < 5000 || loanAmount > 5000000) {
                    Map<String, String> fieldErrors = new HashMap<>();
                    fieldErrors.put("loanAmount", "'No such bank offering for your bargaining Loan Amount '");
                    throw new ValidationException(fieldErrors);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("'LoanAmount' must be Number.");
            }
        }
    }

    private void validateFoir(Map<String, Object> inputVariables) {
        Object foirObj = inputVariables.get("foir");
        if (foirObj == null) {
            throw new IllegalArgumentException("'FOIR' must be in input Variables");
        }
        if (foirObj instanceof String) {
            try {
                int foir = Integer.parseInt((String) foirObj);
                if (foir < 0 || foir > 70) {
                    Map<String, String> fieldErrors = new HashMap<>();
                    fieldErrors.put("foir", "'You are not eligible due to  your FOIR'");
                    throw new ValidationException(fieldErrors);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("'FOIR' must be a number");
            }
        }
    }
}
