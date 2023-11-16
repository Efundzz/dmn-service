package com.efundzz.dmnservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CRMBreFormRequestDTO {
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
	public String getTakeHomeSalary() {
		return takeHomeSalary;
	}
	public void setTakeHomeSalary(String takeHomeSalary) {
		this.takeHomeSalary = takeHomeSalary;
	}
	public long getTakeHomeSalaryMonthly() {
		return takeHomeSalaryMonthly;
	}
	public void setTakeHomeSalaryMonthly(long takeHomeSalaryMonthly) {
		this.takeHomeSalaryMonthly = takeHomeSalaryMonthly;
	}
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getCreditScore() {
		return creditScore;
	}
	public void setCreditScore(String creditScore) {
		this.creditScore = creditScore;
	}
	public String getExperienceYears() {
		return experienceYears;
	}
	public void setExperienceYears(String experienceYears) {
		this.experienceYears = experienceYears;
	}
	public String getExperienceMonths() {
		return experienceMonths;
	}
	public void setExperienceMonths(String experienceMonths) {
		this.experienceMonths = experienceMonths;
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
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public long getLoanAmount() {
		return loanAmount;
	}
	public void setLoanAmount(long loanAmount) {
		this.loanAmount = loanAmount;
	}
	public String getEmi() {
		return emi;
	}
	public void setEmi(String emi) {
		this.emi = emi;
	}
	public int getFoir() {
		return foir;
	}
	public void setFoir(int foir) {
		this.foir = foir;
	}
	public String getCompanyCategory() {
		return companyCategory;
	}
	public void setCompanyCategory(String companyCategory) {
		this.companyCategory = companyCategory;
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
	public String getCurrentjobStability() {
		return currentjobStability;
	}
	public void setCurrentjobStability(String currentjobStability) {
		this.currentjobStability = currentjobStability;
	}
	public int getJobStability() {
		return jobStability;
	}
	public void setJobStability(int jobStability) {
		this.jobStability = jobStability;
	}
	

}
