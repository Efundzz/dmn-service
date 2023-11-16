package com.efundzz.dmnservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
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
	public String getCompanyCategory() {
		return companyCategory;
	}
	public void setCompanyCategory(String companyCategory) {
		this.companyCategory = companyCategory;
	}
	public String getCreditScore() {
		return creditScore;
	}
	public void setCreditScore(String creditScore) {
		 System.out.println("Setting creditScore: " + creditScore);
	        this.creditScore = creditScore;
	}
	public long getTakeHomeSalaryMonthly() {
		return takeHomeSalaryMonthly;
	}
	public void setTakeHomeSalaryMonthly(long takeHomeSalaryMonthly) {
		this.takeHomeSalaryMonthly = takeHomeSalaryMonthly;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public long getExperience() {
		return experience;
	}
	public void setExperience(long experience) {
		this.experience = experience;
	}
	public String getResidentType() {
		return residentType;
	}
	public void setResidentType(String residentType) {
		this.residentType = residentType;
	}
	public long getLoanAmount() {
		return loanAmount;
	}
	public void setLoanAmount(long loanAmount) {
		this.loanAmount = loanAmount;
	}
	public int getFoir() {
		return foir;
	}
	public void setFoir(int foir) {
		this.foir = foir;
	}
	public String getSalaryCreditType() {
		return salaryCreditType;
	}
	public void setSalaryCreditType(String salaryCreditType) {
		this.salaryCreditType = salaryCreditType;
	}
	public String getOwnHouse() {
		return ownHouse;
	}
	public void setOwnHouse(String ownHouse) {
		this.ownHouse = ownHouse;
	}
	public int getJobStability() {
		return jobStability;
	}
	public void setJobStability(int jobStability) {
		this.jobStability = jobStability;
	}
	
}
