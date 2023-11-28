package com.efundzz.dmnservice.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class CRMBreFormRequestDTO {
	private String agentId;
	private String brand;
	private String takeHomeSalary;
	private long takeHomeSalaryMonthly;
	private String dateOfBirth;
	private int age;
	private String creditScore;
	private String experienceYears;
	private String experienceMonths;
	private long experience;
	private String residentType;
	private String amount;
	private long loanAmount;
	private String emi;
	private int foir;
	private String companyCategory;
	private String salaryCreditType;
	private String ownHouse;
	private String currentjobStability;
	private int jobStability;
}
