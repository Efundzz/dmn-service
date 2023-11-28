package com.efundzz.dmnservice.services;

import com.efundzz.dmnservice.dto.CRMBreFormRequestDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProbabilityCalServices {
    void calculateProbabilityForBanks(List<Map<String, Object>> decisionResult, CRMBreFormRequestDTO inputDTO) {
        for (Map<String, Object> bankResult : decisionResult) {
            String bankName = (String) bankResult.get("entity");
            double bankProbability = calculateProbabilityForBank(bankName, inputDTO);
            bankResult.put("probabilityPercentage", bankProbability);
        }
    }
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
            csProbability = switch (creditScore) {
                case "A+" -> 1.1;
                case "A" -> 1.05;
                case "B" -> 1.0;
                case "C" -> 0.9;
                case "D" -> 0.8;
                case "NH" -> 1.0;
                default -> csProbability;
            };
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
            resProbability = switch (residentType) {
                case "Owned" -> 1.1;
                case "Rented", "Company Accommodation" -> 0.9;
                case " PG", "Staying with Friends" -> 0.5;
                default -> resProbability;
            };
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
