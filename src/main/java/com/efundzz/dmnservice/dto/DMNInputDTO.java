package com.efundzz.dmnservice.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Setter
@Getter
public class DMNInputDTO {
	private String creditScore;
	private long takeHomeSalaryMonthly;
	private int age;
	private long experience;
	private String residentType;
	private long loanAmount;
	private int foir;
	private String companyCategory;
	private String salaryCreditType;
	private String ownHouse;
	private int jobStability;
}
